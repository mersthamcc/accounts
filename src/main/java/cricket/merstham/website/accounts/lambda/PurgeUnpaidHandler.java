package cricket.merstham.website.accounts.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import cricket.merstham.website.accounts.model.ApiResponse;
import cricket.merstham.website.accounts.model.PurgeRequest;
import cricket.merstham.website.accounts.sage.ApiException;
import cricket.merstham.website.accounts.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.text.MessageFormat.format;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;

public class PurgeUnpaidHandler
        implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final Logger LOG = LoggerFactory.getLogger(PurgeUnpaidHandler.class);

    private final SageAccountingService sageAccountingService;
    private final SerializationService serializationService;
    private final ConfigurationService configurationService;
    private final SageApiClient sageApiClient;

    public PurgeUnpaidHandler(
            SageAccountingService sageAccountingService,
            SerializationService serializationService,
            ConfigurationService configurationService,
            SageApiClient sageApiClient) {
        this.sageAccountingService = sageAccountingService;
        this.serializationService = serializationService;
        this.configurationService = configurationService;
        this.sageApiClient = sageApiClient;
    }

    public PurgeUnpaidHandler() {
        this.configurationService = new ConfigurationService();
        this.serializationService = SerializationService.getInstance();
        DynamoService dynamoService = new DynamoService(configurationService);
        this.sageApiClient =
                new SageApiClient(
                        dynamoService.getConfig(),
                        configurationService,
                        new TokenManager(dynamoService));
        this.sageAccountingService = new SageAccountingService(null, dynamoService, sageApiClient);
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(
            APIGatewayProxyRequestEvent input, Context context) {

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

        LOG.info("Received request: {}", input.getBody());
        PurgeRequest request =
                serializationService.deserialise(input.getBody(), PurgeRequest.class);

        try {
            LOG.info("Finding QEs between {} and {}", request.getStartDate(), request.getEndDate());
            List<String> idsToPurge =
                    sageAccountingService.getUnpaidIds(
                            request.getStartDate(), request.getEndDate());

            LOG.info("Found {} entries to purge", idsToPurge.size());
            sageAccountingService.deleteEntries(idsToPurge);
            LOG.info("Done");
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(200)
                    .withBody(
                            serializationService.serialise(
                                    new ApiResponse()
                                            .setId(input.getRequestContext().getRequestId())
                                            .setCode(200)
                                            .setMessage(
                                                    format(
                                                            "Purged {0} entries!",
                                                            idsToPurge.size()))));

        } catch (ApiException | JsonProcessingException e) {
            LOG.error("Error purging entries", e);
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(500)
                    .withBody(
                            serializationService.serialise(
                                    new ApiResponse()
                                            .setId(input.getRequestContext().getRequestId())
                                            .setCode(500)
                                            .setMessage(e.getMessage())));
        }
    }
}
