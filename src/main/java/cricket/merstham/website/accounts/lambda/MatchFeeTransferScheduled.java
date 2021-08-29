package cricket.merstham.website.accounts.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import cricket.merstham.website.accounts.model.PlayCricketMatch;
import cricket.merstham.website.accounts.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

public class MatchFeeTransferScheduled implements RequestHandler<ScheduledEvent, String> {
    private static final Logger LOG = LoggerFactory.getLogger(MatchFeeTransferScheduled.class);
    private static final List<String> TEAMS =
            List.of(
                    "1st XI",
                    "2nd XI",
                    "3rd XI",
                    "4th XI",
                    "5th XI",
                    "Development XI",
                    "Sunday 1st XI",
                    "Friendly XI");
    private final SerializationService serializationService;
    private final PlayCricketService playCricketService;
    private final SqsService sqsService;

    public MatchFeeTransferScheduled() {
        this.serializationService = new SerializationService();
        this.playCricketService = new PlayCricketService(new ConfigurationService());
        ConfigurationService configurationService = new ConfigurationService();
        DynamoService dynamoService = new DynamoService(configurationService);
        this.sqsService =
                new SqsService(
                        configurationService,
                        serializationService,
                        dynamoService.getConfig().getApiConfiguration());
    }

    @Override
    public String handleRequest(ScheduledEvent input, Context context) {
        LOG.info("Received request: {}", input.toString());
        LocalDate endDate =
                LocalDate.of(
                        input.getTime().getYear(),
                        input.getTime().getMonthOfYear(),
                        input.getTime().getDayOfMonth());
        LocalDate startDate = endDate.minusDays(7);

        List<PlayCricketMatch> matches = playCricketService.getMatches(startDate, endDate, TEAMS);

        String requestId = input.getId();
        matches.forEach(
                m -> {
                    LOG.info("Sending match {} to queue", m.getId());
                    sqsService.sendMessage(m, requestId, "match-fee");
                });
        return "OK";
    }
}
