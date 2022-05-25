package cricket.merstham.website.accounts.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import cricket.merstham.website.accounts.configuration.ApiConfiguration;
import cricket.merstham.website.accounts.configuration.Configuration;
import cricket.merstham.website.accounts.model.EposNowEndOfDay;
import cricket.merstham.website.accounts.services.ConfigurationService;
import cricket.merstham.website.accounts.services.DynamoService;
import cricket.merstham.website.accounts.services.EposNowApiClient;
import cricket.merstham.website.accounts.services.EposNowService;
import cricket.merstham.website.accounts.services.SerializationService;
import cricket.merstham.website.accounts.services.SqsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.util.UUID;

public class ProcessEndOfDay implements RequestHandler<String, Integer> {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessEndOfDay.class);

    private final EposNowService eposNowService;
    private final ApiConfiguration apiConfiguration;
    private final SqsService sqsService;
    private final SerializationService serializationService;

    public ProcessEndOfDay() {
        ConfigurationService configurationService = new ConfigurationService();
        DynamoService dynamoService = new DynamoService(configurationService);
        Configuration config = dynamoService.getConfig();
        this.apiConfiguration = config.getApiConfiguration();
        this.eposNowService = new EposNowService(new EposNowApiClient(apiConfiguration));
        this.serializationService = new SerializationService();
        this.sqsService =
                new SqsService(configurationService, serializationService, apiConfiguration);
    }

    @Override
    public Integer handleRequest(String input, Context context) {
        var endOfDay = serializationService.deserialise(input, EposNowEndOfDay.class);
        var transactions = eposNowService.getTransactionsForDay(endOfDay);
        LOG.info("Retrieved {} transactions from EposNow", transactions.size());
        int count = 0;
        for (var t : transactions) {
            String messageId = UUID.randomUUID().toString();
            LOG.info(
                    "Sending transaction {} to queue {}",
                    t.getBarcode(),
                    apiConfiguration.getQueueUrl());
            try {
                SendMessageResponse result = sqsService.sendMessage(t, messageId, "epos");
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
        return count;
    }
}
