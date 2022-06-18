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
import java.util.stream.Collectors;

import static java.text.MessageFormat.format;

public class ProcessTransactions implements RequestHandler<SQSEvent, Void> {
    private static final Logger LOG = LoggerFactory.getLogger(EndOfDay.class);

    public static final String MESSAGE_TYPE_ATTRIBUTE = "message-type";
    public static final String MESSAGE_ID_ATTRIBUTE = "message-id";
    public static final String MATCH_FEE_TRANSACTION = "match-fee";
    public static final String EPOS_NOW_TRANSACTION = "epos";

    private final SageAccountingService sageAccountingService;
    private final SerializationService serializationService;
    private final PlayCricketService playCricketService;
    private final SageApiClient sageApiClient;

    public ProcessTransactions(
            SageAccountingService sageAccountingService,
            SerializationService serializationService,
            PlayCricketService playCricketService,
            SageApiClient sageApiClient) {
        this.sageAccountingService = sageAccountingService;
        this.serializationService = serializationService;
        this.playCricketService = playCricketService;
        this.sageApiClient = sageApiClient;
    }

    public ProcessTransactions() {
        ConfigurationService configurationService = new ConfigurationService();
        DynamoService dynamoService = new DynamoService(configurationService);
        Configuration configuration = dynamoService.getConfig();
        ApiConfiguration apiConfiguration = configuration.getApiConfiguration();
        EposNowService eposNowService = new EposNowService(new EposNowApiClient(apiConfiguration));
        this.playCricketService = new PlayCricketService(configurationService);
        this.serializationService = SerializationService.getInstance();
        this.sageApiClient =
                new SageApiClient(
                        dynamoService.getConfig(),
                        configurationService,
                        new TokenManager(dynamoService));
        this.sageAccountingService =
                new SageAccountingService(
                        new MappingService(eposNowService, dynamoService, playCricketService),
                        dynamoService,
                        sageApiClient);
    }

    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        LOG.info("Starting processing on instance {}", context.getAwsRequestId());
        try {
            for (var message : event.getRecords()) {
                SQSEvent.MessageAttribute transactionType =
                        message.getMessageAttributes().get(MESSAGE_TYPE_ATTRIBUTE);

                if (transactionType == null) {
                    LOG.info(
                            "Processing EPOS transaction message. Instance {}",
                            context.getAwsRequestId());
                    sageAccountingService.createEposNowSalesTransaction(
                            serializationService.deserialise(
                                    message.getBody(), EposNowTransaction.class));
                } else {
                    switch (transactionType.getStringValue()) {
                        case EPOS_NOW_TRANSACTION:
                            LOG.info(
                                    "Processing EPOS transaction message. Instance {}",
                                    context.getAwsRequestId());
                            sageAccountingService.createEposNowSalesTransaction(
                                    serializationService.deserialise(
                                            message.getBody(), EposNowTransaction.class));
                            break;
                        case MATCH_FEE_TRANSACTION:
                            LOG.info(
                                    "Processing match fee message. Instance {}",
                                    context.getAwsRequestId());
                            LOG.info("Message = {}", message.getBody());
                            PlayCricketMatch match =
                                    serializationService.deserialise(
                                            message.getBody(), PlayCricketMatch.class);

                            List<PlayCricketPlayer> players =
                                    playCricketService.getOurPlayers(match.getId());
                            LOG.info(
                                    "Player List for {} vs {}: {}",
                                    match.getHomeTeamName(),
                                    match.getAwayTeamName(),
                                    String.join(
                                            ", ",
                                            players.stream()
                                                    .map(p -> p.getPlayerName())
                                                    .collect(Collectors.toList())));
                            sageAccountingService.createQuickEntriesForMatchFees(match, players);
                            break;
                        default:
                            LOG.error(
                                    "Unknown message type encountered {}. Instance {}",
                                    transactionType.getStringValue(),
                                    context.getAwsRequestId());
                            break;
                    }
                }
            }
        } catch (JsonProcessingException e) {
            var message =
                    format("Error processing Sage data. Instance {0}", context.getAwsRequestId());
            LOG.error(message, e);
            throw new RuntimeException(message, e);
        }
        LOG.info("Finishing processing on instance {}", context.getAwsRequestId());
        return null;
    }
}
