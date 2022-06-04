package cricket.merstham.website.accounts.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import cricket.merstham.website.accounts.model.TokenStore;
import cricket.merstham.website.accounts.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;

public class SageCallback
        implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final Logger LOG = LoggerFactory.getLogger(SageCallback.class);

    private final ConfigurationService configurationService;
    private final DynamoService dynamoService;
    private final SageApiClient sageApiClient;

    public SageCallback() {
        this.configurationService = new ConfigurationService();
        this.dynamoService = new DynamoService(configurationService);
        this.sageApiClient =
                new SageApiClient(
                        dynamoService.getConfig(),
                        configurationService,
                        new TokenManager(dynamoService));
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(
            APIGatewayProxyRequestEvent input, Context context) {
        String state =
                input.getQueryStringParameters()
                        .getOrDefault("state", UUID.randomUUID().toString());
        String code = input.getQueryStringParameters().getOrDefault("code", "");
        TokenStore tokenStore = dynamoService.getToken();
        if (tokenStore != null
                && tokenStore.getState() != null
                && tokenStore.getState().equals(state)
                && !code.isBlank()) {
            try {
                sageApiClient.exchangeForToken(code);
            } catch (JsonProcessingException e) {
                LOG.error("Error parsing token response", e);
                return new APIGatewayProxyResponseEvent()
                        .withStatusCode(SC_INTERNAL_SERVER_ERROR)
                        .withBody(e.getMessage());
            }
            LOG.info("Token successfully retrieved from Sage IDP and stored");
            return new APIGatewayProxyResponseEvent()
                    .withBody("Token successfully retrieved from Sage IDP and stored");
        }
        LOG.warn("Invalid state or code detected on Sage callback!");
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(SC_UNAUTHORIZED)
                .withBody("You are bad!!");
    }
}
