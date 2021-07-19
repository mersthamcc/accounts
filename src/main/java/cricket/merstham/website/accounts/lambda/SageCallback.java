package cricket.merstham.website.accounts.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import cricket.merstham.website.accounts.model.TokenStore;
import cricket.merstham.website.accounts.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;

public class SageCallback
        implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final Logger LOG = LoggerFactory.getLogger(SageCallback.class);

    private final ConfigurationService configurationService;
    private final DynamoService dynamoService;
    private final SageAccountingService sageAccountingService;

    public SageCallback() {
        this.configurationService = new ConfigurationService();
        this.dynamoService = new DynamoService(configurationService);
        this.sageAccountingService =
                new SageAccountingService(
                        dynamoService.getConfig(),
                        configurationService,
                        new TokenManager(dynamoService),
                        new MappingService(
                                configurationService,
                                new EposNowService(
                                        new EposNowApiClient(
                                                dynamoService.getConfig().getApiConfiguration())),
                                dynamoService));
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
            sageAccountingService.exchangeForToken(code);
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
