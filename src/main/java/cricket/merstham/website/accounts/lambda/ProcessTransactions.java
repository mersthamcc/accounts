package cricket.merstham.website.accounts.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import cricket.merstham.website.accounts.configuration.ApiConfiguration;
import cricket.merstham.website.accounts.configuration.Configuration;
import cricket.merstham.website.accounts.model.EposNowTransaction;
import cricket.merstham.website.accounts.model.PlayCricketMatch;
import cricket.merstham.website.accounts.model.PlayCricketPlayer;
import cricket.merstham.website.accounts.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ProcessTransactions implements RequestHandler<SQSEvent, Void> {
    private static final Logger LOG = LoggerFactory.getLogger(EndOfDay.class);

    public static final String MESSAGE_TYPE_ATTRIBUTE = "message-type";
    public static final String MATCH_FEE_TRANSACTION = "match-fee";
    public static final String EPOS_NOW_TRANSACTION = "epos";

    private final SageAccountingService sageAccountingService;
    private final SerializationService serializationService;
    private final PlayCricketService playCricketService;

    public ProcessTransactions() {
        ConfigurationService configurationService = new ConfigurationService();
        DynamoService dynamoService = new DynamoService(configurationService);
        Configuration configuration = dynamoService.getConfig();
        ApiConfiguration apiConfiguration = configuration.getApiConfiguration();
        EposNowService eposNowService = new EposNowService(new EposNowApiClient(apiConfiguration));
        this.playCricketService = new PlayCricketService(configurationService);
        this.serializationService = new SerializationService();
        this.sageAccountingService =
                new SageAccountingService(
                        configuration,
                        configurationService,
                        new TokenManager(dynamoService),
                        new MappingService(
                                configurationService,
                                eposNowService,
                                dynamoService,
                                playCricketService),
                        dynamoService);
    }

    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        try {
            for (var message : event.getRecords()) {
                SQSEvent.MessageAttribute transactionType =
                        message.getMessageAttributes().get(MESSAGE_TYPE_ATTRIBUTE);

                if (transactionType == null) {
                    LOG.info("Processing EPOS transaction message");
                    sageAccountingService.createEposNowSalesTransaction(
                            serializationService.deserialise(
                                    message.getBody(), EposNowTransaction.class));
                } else {
                    switch (transactionType.getStringValue()) {
                        case EPOS_NOW_TRANSACTION:
                            LOG.info("Processing EPOS transaction message");
                            sageAccountingService.createEposNowSalesTransaction(
                                    serializationService.deserialise(
                                            message.getBody(), EposNowTransaction.class));
                            break;
                        case MATCH_FEE_TRANSACTION:
                            LOG.info("Processing match fee message");
                            PlayCricketMatch match =
                                    serializationService.deserialise(
                                            message.getBody(), PlayCricketMatch.class);

                            List<PlayCricketPlayer> players =
                                    playCricketService.getOurPlayers(match.getId());

                            sageAccountingService.createQuickEntriesForMatchFees(match, players);
                            break;
                        default:
                            LOG.error(
                                    "Unknown message type encountered {}",
                                    transactionType.getStringValue());
                            break;
                    }
                }
            }
        } catch (JsonProcessingException e) {
            LOG.error("Error processing Sage data", e);
            throw new RuntimeException("Error processing Sage data", e);
        }
        return null;
    }
}
