package cricket.merstham.website.accounts.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import cricket.merstham.website.accounts.model.TokenStore;
import cricket.merstham.website.accounts.services.*;

import java.util.Map;
import java.util.UUID;

public class SageLogin
        implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final ConfigurationService configurationService;
    private final DynamoService dynamoService;
    private final SageAccountingService sageAccountingService;

    public SageLogin() {
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
        var state = UUID.randomUUID().toString();
        var tokenStore = new TokenStore();
        tokenStore.setState(state);
        dynamoService.putToken(tokenStore);
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(302)
                .withHeaders(
                        Map.of("Location", sageAccountingService.getAuthUrl(state).toString()));
    }
}
