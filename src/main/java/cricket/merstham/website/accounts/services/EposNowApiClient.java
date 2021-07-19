package cricket.merstham.website.accounts.services;

import cricket.merstham.website.accounts.configuration.ApiConfiguration;
import org.glassfish.jersey.client.JerseyClientBuilder;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import java.net.URI;
import java.util.Base64;
import java.util.Map;

import static java.text.MessageFormat.format;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static org.apache.http.HttpStatus.SC_OK;

public class EposNowApiClient {

    private final ApiConfiguration apiConfiguration;
    private URI baseURI = URI.create("https://api.eposnowhq.com");

    public EposNowApiClient(ApiConfiguration apiConfiguration) {
        this.apiConfiguration = apiConfiguration;
    }

    public <T> T getRequest(String path, Map<String, Object> params, Class<T> clazz) {
        return performGet(path, params).readEntity(clazz);
    }

    public <T> T getRequest(String path, Map<String, Object> params, GenericType<T> clazz) {
        return performGet(path, params).readEntity(clazz);
    }

    private Response performGet(String path, Map<String, Object> params) {
        Client client = JerseyClientBuilder.createClient();
        UriBuilder builder = UriBuilder.fromUri(baseURI).path(path);
        for (var param : params.entrySet()) {
            builder.queryParam(param.getKey(), param.getValue());
        }
        WebTarget target = client.target(builder);
        Response response =
                target.request()
                        .header(AUTHORIZATION, getAuthHeader())
                        .accept(MediaType.APPLICATION_JSON_TYPE)
                        .get();
        if (response.getStatus() != SC_OK) throw new RuntimeException("Failed to call EposNow");
        return response;
    }

    private String getAuthHeader() {
        String token =
                format(
                        "{0}:{1}",
                        apiConfiguration.getEposApiKey(), apiConfiguration.getEposApiSecret());
        return format("Basic {0}", Base64.getEncoder().encodeToString(token.getBytes()));
    }
}
