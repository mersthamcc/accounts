package cricket.merstham.website.accounts.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import cricket.merstham.website.accounts.configuration.ApiConfiguration;
import cricket.merstham.website.accounts.configuration.Configuration;
import cricket.merstham.website.accounts.model.ApiResponse;
import cricket.merstham.website.accounts.model.EposNowEndOfDay;
import cricket.merstham.website.accounts.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static cricket.merstham.website.accounts.lambda.ProcessTransactions.MESSAGE_TYPE_ATTRIBUTE;
import static java.text.MessageFormat.format;
import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;

public class EndOfDay
        implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final Logger LOG = LoggerFactory.getLogger(EndOfDay.class);

    private final EposNowService eposNowService;
    private final ApiConfiguration apiConfiguration;
    private final SerializationService serializationService;
    private final SqsClient sqsClient;
    private final ConfigurationService configurationService;

    public EndOfDay() {
        ConfigurationService configurationService = new ConfigurationService();
        DynamoService dynamoService = new DynamoService(configurationService);
        Configuration config = dynamoService.getConfig();
        this.apiConfiguration = config.getApiConfiguration();
        this.eposNowService = new EposNowService(new EposNowApiClient(apiConfiguration));
        this.serializationService = new SerializationService();
        this.sqsClient =
                SqsClient.builder().region(Region.of(configurationService.getAwsRegion())).build();
        this.configurationService = new ConfigurationService();
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
            var transactions = eposNowService.getTransactionsForDay(endOfDay);
            LOG.info("Retrieved {} transactions from EposNow", transactions.size());
            String groupId = UUID.randomUUID().toString();
            int count = 0;
            for (var t : transactions) {
                MessageAttributeValue attribute =
                        MessageAttributeValue.builder()
                                .dataType("String")
                                .stringValue("epos")
                                .build();

                SendMessageRequest request =
                        SendMessageRequest.builder()
                                .queueUrl(apiConfiguration.getQueueUrl())
                                .messageAttributes(Map.of(MESSAGE_TYPE_ATTRIBUTE, attribute))
                                .messageGroupId(
                                        groupId.concat("-")
                                                .concat(input.getRequestContext().getRequestId()))
                                .messageDeduplicationId(
                                        format(
                                                "{0}-{1}",
                                                input.getRequestContext().getRequestId(),
                                                t.getBarcode()))
                                .messageBody(serializationService.serialise(t))
                                .build();
                LOG.info(
                        "Sending transaction {} to queue {}",
                        t.getBarcode(),
                        apiConfiguration.getQueueUrl());
                try {
                    var result = sqsClient.sendMessage(request);
                    LOG.info(
                            "Transaction {} on queue with message ID {} sequence {}",
                            t.getBarcode(),
                            result.messageId(),
                            result.sequenceNumber());
                    count++;
                } catch (Exception ex) {
                    LOG.error("Error sending to queue", ex);
                    throw ex;
                }
            }
            LOG.info("{} messages successfully sent to queue", count);
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
