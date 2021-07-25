package cricket.merstham.website.accounts.services;

import java.util.Optional;

public class ConfigurationService {

    public String getAwsRegion() {
        return System.getenv().getOrDefault("AWS_REGION", "eu-west-2");
    }

    public String getConfigurationName() {
        return System.getenv().getOrDefault("CONFIG_NAME", "test");
    }

    public String getBaseUrl() {
        return System.getenv().getOrDefault("BASE_URL", "http://localhost:8085");
    }

    public Optional<String> getEndOfDayAuth() {
        return Optional.ofNullable(System.getenv().get("END_OF_DAY_AUTH"));
    }

    public int getPlayCricketSiteId() {
        return Integer.parseInt(System.getenv().getOrDefault("PLAY_CRICKET_SITE_ID", "4305"));
    }

    public String getPlayCricketApiToken() {
        return System.getenv()
                .getOrDefault("PLAY_CRICKET_API_TOKEN", "d4c27baa90a8192e1343fa9fdda04ada");
    }
}
