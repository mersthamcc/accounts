package cricket.merstham.website.accounts.services;

import cricket.merstham.website.accounts.configuration.ApiConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.util.Map;

import static cricket.merstham.website.accounts.lambda.ProcessTransactions.MESSAGE_TYPE_ATTRIBUTE;
import static java.text.MessageFormat.format;

public class SqsService {
    private static final Logger LOG = LoggerFactory.getLogger(SqsService.class);

    private final SqsClient client;
    private final ConfigurationService configurationService;
    private final SerializationService serializationService;
    private final ApiConfiguration apiConfiguration;

    public SqsService(
            ConfigurationService configurationService,
            SerializationService serializationService,
            ApiConfiguration apiConfiguration) {
        this.configurationService = configurationService;
        this.serializationService = serializationService;
        this.apiConfiguration = apiConfiguration;
        this.client =
                SqsClient.builder()
                        .region(Region.of(this.configurationService.getAwsRegion()))
                        .build();
    }

    public void sendMessage(
            Object message,
            String requestId,
            String groupId,
            String messageId,
            String messageType) {
        MessageAttributeValue attribute =
                MessageAttributeValue.builder().dataType("String").stringValue(messageType).build();
        SendMessageRequest request =
                SendMessageRequest.builder()
                        .queueUrl(apiConfiguration.getQueueUrl())
                        .messageAttributes(Map.of(MESSAGE_TYPE_ATTRIBUTE, attribute))
                        .messageGroupId(groupId.concat("-").concat(requestId))
                        .messageDeduplicationId(format("{0}-{1}", requestId, messageId))
                        .messageBody(serializationService.serialise(message))
                        .build();
        try {
            SendMessageResponse result = client.sendMessage(request);
        } catch (Exception ex) {
            LOG.error("Error sending to queue", ex);
            throw ex;
        }
    }
}
