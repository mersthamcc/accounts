package cricket.merstham.website.accounts.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import cricket.merstham.website.accounts.configuration.Configuration;
import cricket.merstham.website.accounts.sage.ApiClient;
import cricket.merstham.website.accounts.sage.ApiException;
import cricket.merstham.website.accounts.sage.ApiResponse;
import cricket.merstham.website.accounts.sage.Pair;
import dev.failsafe.Failsafe;
import dev.failsafe.FailsafeException;
import dev.failsafe.RetryPolicy;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.text.MessageFormat.format;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.apache.http.HttpStatus.SC_BAD_GATEWAY;
import static org.apache.http.HttpStatus.SC_GATEWAY_TIMEOUT;
import static org.apache.http.HttpStatus.SC_SERVICE_UNAVAILABLE;

public class SageApiClient extends ApiClient {
    private static final Logger LOG = LoggerFactory.getLogger(SageApiClient.class);
    private static final URI AUTH_URL = URI.create("https://www.sageone.com/oauth2/auth/central");
    private static final URI TOKEN_URL = URI.create("https://oauth.accounting.sage.com/token");
    private static final List<Integer> RETRY_STATUS =
            List.of(SC_BAD_GATEWAY, SC_GATEWAY_TIMEOUT, SC_SERVICE_UNAVAILABLE);

    private final Configuration configuration;
    private final ConfigurationService configurationService;
    private final TokenManager tokenManager;

    public SageApiClient(
            Configuration configuration,
            ConfigurationService configurationService,
            TokenManager tokenManager) {
        this.configuration = configuration;
        this.configurationService = configurationService;
        this.tokenManager = tokenManager;
    }

    @Override
    protected void performAdditionalClientConfiguration(ClientConfig clientConfig) {
        setUserAgent("mersthamcc.co.uk Accounts Interface");
    }

    @Override
    public <T> ApiResponse<T> invokeAPI(
            String path,
            String method,
            List<Pair> queryParams,
            Object body,
            Map<String, String> headerParams,
            Map<String, Object> formParams,
            String accept,
            String contentType,
            String[] authNames,
            GenericType<T> returnType)
            throws ApiException {
        var retryPolicy =
                RetryPolicy.<ApiResponse<T>>builder()
                        .handle(IOException.class)
                        .handle(JsonProcessingException.class)
                        .handle(SocketTimeoutException.class)
                        .handle(SocketException.class)
                        .handleResultIf(r -> RETRY_STATUS.contains(r.getStatusCode()))
                        .withBackoff(5, 60, SECONDS)
                        .withMaxRetries(5)
                        .onFailedAttempt(
                                e -> LOG.error("Connection attempt failed", e.getLastException()))
                        .onRetry(e -> LOG.warn("Failure #{}. Retrying.", e.getAttemptCount()))
                        .onSuccess(e -> LOG.info("Received successful response"))
                        .build();

        try {
            return Failsafe.with(retryPolicy)
                    .get(
                            () -> {
                                refreshToken();
                                return super.invokeAPI(
                                        path,
                                        method,
                                        queryParams,
                                        body,
                                        headerParams,
                                        formParams,
                                        accept,
                                        contentType,
                                        authNames,
                                        returnType);
                            });
        } catch (FailsafeException e) {
            throw new ApiException(e.getCause());
        }
    }

    public URI getAuthUrl(String state) {
        return UriBuilder.fromUri(AUTH_URL)
                .queryParam("country", "gb")
                .queryParam("locale", "en-GB")
                .queryParam("client_id", configuration.getApiConfiguration().getSageApiKey())
                .queryParam("response_type", "code")
                .queryParam(
                        "redirect_uri",
                        format("{0}/sage-callback", configurationService.getBaseUrl()))
                .queryParam("state", state)
                .queryParam("scopes", "full_access")
                .build();
    }

    public String exchangeForToken(String code) throws JsonProcessingException {
        var form = new Form();
        form.param("grant_type", "authorization_code");
        form.param("code", code);
        form.param("redirect_uri", format("{0}/sage-callback", configurationService.getBaseUrl()));

        return tokenRequest(form);
    }

    public void refreshToken() throws JsonProcessingException {
        if (tokenManager.isAccessTokenExpired()) {
            var form = new Form();
            form.param("grant_type", "refresh_token");
            form.param("refresh_token", tokenManager.getTokenStore().getRefreshToken());

            setAccessToken(tokenRequest(form));
        }
        setAccessToken(tokenManager.getTokenStore().getAccessToken());
    }

    private String tokenRequest(Form form) throws JsonProcessingException {
        Client client = JerseyClientBuilder.createClient();
        form.param("client_id", configuration.getApiConfiguration().getSageApiKey());
        form.param("client_secret", configuration.getApiConfiguration().getSageApiSecret());
        LOG.info(
                "Performing token request with parameters = {}",
                String.join(
                        "&",
                        form.asMap().entrySet().stream()
                                .map(
                                        e ->
                                                String.join(
                                                        ",",
                                                        e.getValue().stream()
                                                                .map(
                                                                        v ->
                                                                                format(
                                                                                        "{0}={1}",
                                                                                        e.getKey(),
                                                                                        v))
                                                                .collect(Collectors.toList())))
                                .collect(Collectors.toList())));
        Response response = client.target(TOKEN_URL).request().post(Entity.form(form));
        String body = response.readEntity(String.class);
        LOG.info("Received token response = {}", body);
        JsonNode json = getJSON().getContext(JsonNode.class).readTree(body);
        if (json.has("error_description")) {
            throw new RuntimeException(json.get("error_description").asText());
        }
        long expiresIn = json.get("expires_in").asLong(0);
        long refreshExpiresIn = json.get("refresh_token_expires_in").asLong(0);
        LOG.info(
                "Access token expires in {} seconds - Refresh Token Expires in {} seconds",
                expiresIn,
                refreshExpiresIn);
        LocalDateTime accessTokenExpiry = LocalDateTime.now().plusSeconds(expiresIn);
        LocalDateTime refreshTokenExpiry = LocalDateTime.now().plusSeconds(refreshExpiresIn);
        tokenManager.update(
                json.get("access_token").asText(),
                accessTokenExpiry,
                json.get("refresh_token").asText(),
                refreshTokenExpiry);
        return tokenManager.getTokenStore().getAccessToken();
    }
}
