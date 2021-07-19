package cricket.merstham.website.accounts.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import cricket.merstham.website.accounts.configuration.ApiConfiguration;
import cricket.merstham.website.accounts.configuration.Configuration;
import cricket.merstham.website.accounts.model.EposNowTransaction;
import cricket.merstham.website.accounts.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.util.List;
import java.util.stream.Collectors;

public class ProcessTransactions implements RequestHandler<SQSEvent, Void> {
    private static final Logger LOG = LoggerFactory.getLogger(EndOfDay.class);

    private final EposNowService eposNowService;
    private final SageAccountingService sageAccountingService;
    private final ApiConfiguration apiConfiguration;
    private final SerializationService serializationService;
    private final SqsClient sqsClient;

    public ProcessTransactions() {
        ConfigurationService configurationService = new ConfigurationService();
        DynamoService dynamoService = new DynamoService(configurationService);
        Configuration configuration = dynamoService.getConfig();
        this.apiConfiguration = configuration.getApiConfiguration();
        this.eposNowService = new EposNowService(new EposNowApiClient(apiConfiguration));
        this.sageAccountingService =
                new SageAccountingService(
                        configuration,
                        configurationService,
                        new TokenManager(dynamoService),
                        new MappingService(configurationService, eposNowService, dynamoService));
        this.serializationService = new SerializationService();
        this.sqsClient =
                SqsClient.builder().region(Region.of(configurationService.getAwsRegion())).build();
    }

    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        List<EposNowTransaction> transactions =
                event.getRecords().stream()
                        .map(
                                m ->
                                        serializationService.deserialise(
                                                m.getBody(), EposNowTransaction.class))
                        .collect(Collectors.toList());
        try {
            for (var transaction : transactions) {
                sageAccountingService.createEposNowSalesTransaction(transaction);
            }
        } catch (JsonProcessingException e) {
            LOG.error("Error processing Sage data", e);
            throw new RuntimeException("Error processing Sage data", e);
        }
        return null;
    }
}
