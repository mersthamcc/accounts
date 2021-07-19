package cricket.merstham.website.accounts.services;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import cricket.merstham.website.accounts.configuration.Configuration;
import cricket.merstham.website.accounts.model.TokenStore;

public class DynamoService {

    private final ConfigurationService configurationService;
    private final AmazonDynamoDB dynamoDB;
    private final DynamoDBMapperConfig mapperConfig;
    private final DynamoDBMapper mapper;

    public DynamoService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
        this.dynamoDB =
                AmazonDynamoDBClientBuilder.standard()
                        .withRegion(configurationService.getAwsRegion())
                        .build();

        this.mapperConfig = new DynamoDBMapperConfig.Builder().build();
        this.mapper = new DynamoDBMapper(dynamoDB, mapperConfig);
    }

    public Configuration getConfig() {
        return mapper.load(Configuration.class, configurationService.getConfigurationName());
    }

    public TokenStore getToken() {
        return mapper.load(TokenStore.class, configurationService.getConfigurationName());
    }

    public TokenStore putToken(TokenStore tokenStore) {
        tokenStore.setId(configurationService.getConfigurationName());
        mapper.save(tokenStore);

        return tokenStore;
    }
}
