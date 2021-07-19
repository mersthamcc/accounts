package cricket.merstham.website.accounts.services;

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
}
