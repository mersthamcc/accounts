package cricket.merstham.website.accounts.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.ScheduledEvent;
import cricket.merstham.website.accounts.configuration.Configuration;
import cricket.merstham.website.accounts.services.ConfigurationService;
import cricket.merstham.website.accounts.services.DynamoService;
import cricket.merstham.website.accounts.services.SageAccountingService;
import cricket.merstham.website.accounts.services.TokenManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RefreshSageTokenScheduled implements RequestHandler<ScheduledEvent, String> {
    private static final Logger LOG = LoggerFactory.getLogger(RefreshSageTokenScheduled.class);
    private final SageAccountingService sageAccountingService;

    public RefreshSageTokenScheduled() {
        ConfigurationService configurationService = new ConfigurationService();
        DynamoService dynamoService = new DynamoService(configurationService);
        Configuration configuration = dynamoService.getConfig();
        this.sageAccountingService =
                new SageAccountingService(
                        configuration,
                        configurationService,
                        new TokenManager(dynamoService),
                        null,
                        dynamoService);
    }

    @Override
    public String handleRequest(ScheduledEvent input, Context context) {
        LOG.info("Refreshing Sage tokens");
        sageAccountingService.refreshToken();
        return "OK";
    }
}
