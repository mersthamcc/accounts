package cricket.merstham.website.accounts.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import cricket.merstham.website.accounts.model.MatchFeeRequest;
import cricket.merstham.website.accounts.model.PlayCricketMatch;
import cricket.merstham.website.accounts.services.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static java.text.MessageFormat.format;
import static org.apache.http.HttpStatus.SC_OK;

public class MatchFeeTransfer
        implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final Logger LOG = LoggerFactory.getLogger(MatchFeeTransfer.class);

    private final SerializationService serializationService;
    private final PlayCricketService playCricketService;
    private final SqsService sqsService;

    public MatchFeeTransfer() {
        this.serializationService = SerializationService.getInstance();
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
    public APIGatewayProxyResponseEvent handleRequest(
            APIGatewayProxyRequestEvent input, Context context) {

        LOG.info("Received request: {}", input.getBody());
        MatchFeeRequest request =
                serializationService.deserialise(input.getBody(), MatchFeeRequest.class);

        List<PlayCricketMatch> matches =
                playCricketService.getMatches(
                        request.getStartDate(), request.getEndDate(), request.getTeams());

        matches.forEach(
                m -> {
                    String messageId = format("{0}-{1}", context.getAwsRequestId(), m.getId());
                    LOG.info("Sending match {} to queue", m.getId());
                    sqsService.sendMessage(m, messageId, "match-fee");
                });

        return new APIGatewayProxyResponseEvent()
                .withBody(serializationService.serialise(matches))
                .withStatusCode(SC_OK);
    }
}
