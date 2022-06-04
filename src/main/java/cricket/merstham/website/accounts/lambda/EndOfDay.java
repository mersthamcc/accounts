package cricket.merstham.website.accounts.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import cricket.merstham.website.accounts.configuration.ApiConfiguration;
import cricket.merstham.website.accounts.configuration.Configuration;
import cricket.merstham.website.accounts.model.ApiResponse;
import cricket.merstham.website.accounts.model.EposNowEndOfDay;
import cricket.merstham.website.accounts.services.ConfigurationService;
import cricket.merstham.website.accounts.services.DynamoService;
import cricket.merstham.website.accounts.services.EposNowApiClient;
import cricket.merstham.website.accounts.services.EposNowService;
import cricket.merstham.website.accounts.services.SerializationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.lambda.LambdaClient;
import software.amazon.awssdk.services.lambda.model.InvocationType;
import software.amazon.awssdk.services.lambda.model.InvokeRequest;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static software.amazon.awssdk.regions.Region.EU_WEST_2;

public class EndOfDay
        implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final Logger LOG = LoggerFactory.getLogger(EndOfDay.class);

    private final EposNowService eposNowService;
    private final ApiConfiguration apiConfiguration;
    private final SerializationService serializationService;
    private final ConfigurationService configurationService;
    private final LambdaClient client;

    public EndOfDay() {
        ConfigurationService configurationService = new ConfigurationService();
        DynamoService dynamoService = new DynamoService(configurationService);
        Configuration config = dynamoService.getConfig();
        this.apiConfiguration = config.getApiConfiguration();
        this.eposNowService = new EposNowService(new EposNowApiClient(apiConfiguration));
        this.serializationService = SerializationService.getInstance();
        this.configurationService = new ConfigurationService();
        this.client = LambdaClient.builder().region(EU_WEST_2).build();
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(
            APIGatewayProxyRequestEvent input, Context context) {
        if (!("EndOfDay".equals(input.getHeaders().get("Epos-Object"))
                && "Create".equals(input.getHeaders().get("Epos-Action")))) {
            LOG.error(
                    "Request {} has invalid Epos action headers.",
                    input.getRequestContext().getRequestId());
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(401)
                    .withBody(
                            serializationService.serialise(
                                    new ApiResponse()
                                            .setId(input.getRequestContext().getRequestId())
                                            .setCode(401)
                                            .setMessage("Unauthorised")));
        }

        Optional<String> authorisation = configurationService.getEndOfDayAuth();

        if (authorisation.isPresent()) {
            String suppliedAuth = input.getHeaders().get(AUTHORIZATION);
            LOG.info(suppliedAuth);

            if (Objects.isNull(suppliedAuth)
                    || !"Basic ".concat(authorisation.get()).equals(suppliedAuth)) {
                LOG.error(
                        "Request {} has incorrect credentials.",
                        input.getRequestContext().getRequestId());
                return new APIGatewayProxyResponseEvent()
                        .withStatusCode(401)
                        .withBody(
                                serializationService.serialise(
                                        new ApiResponse()
                                                .setId(input.getRequestContext().getRequestId())
                                                .setCode(401)
                                                .setMessage("Unauthorised")));
            }
        }

        LOG.info(
                "Starting request {} - Request Body = {}",
                input.getRequestContext().getRequestId(),
                input.getBody());
        EposNowEndOfDay endOfDay =
                serializationService.deserialise(input.getBody(), EposNowEndOfDay.class);
        LOG.info("Received End Of Day Webhook from EposNow with ID = {}", endOfDay.getId());

        if ((!apiConfiguration.isEposValidateEndOfDay())
                || eposNowService.validateEndOfDay(endOfDay)) {
            var invokeResult =
                    client.invoke(
                            InvokeRequest.builder()
                                    .functionName(System.getenv("PROCESSING_LAMBDA_ARN"))
                                    .qualifier("$LATEST")
                                    .payload(SdkBytes.fromUtf8String(input.getBody()))
                                    .invocationType(InvocationType.EVENT)
                                    .build());
            LOG.info(
                    "Invoke result {}: {}",
                    invokeResult.statusCode(),
                    invokeResult.payload().asString(StandardCharsets.UTF_8));
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody(
                            serializationService.serialise(
                                    new ApiResponse()
                                            .setId(input.getRequestContext().getRequestId())
                                            .setCode(200)
                                            .setMessage("OK")));
        }
        LOG.error("Epos EndOfDay Hook failed validation");
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(400)
                .withBody(
                        serializationService.serialise(
                                new ApiResponse()
                                        .setId(input.getRequestContext().getRequestId())
                                        .setCode(400)
                                        .setMessage("Epos EndOfDay Hook failed validation")));
    }
}
