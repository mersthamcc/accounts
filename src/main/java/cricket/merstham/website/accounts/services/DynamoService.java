package cricket.merstham.website.accounts.services;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;
import cricket.merstham.website.accounts.configuration.Configuration;
import cricket.merstham.website.accounts.model.Audit;
import cricket.merstham.website.accounts.model.Error;
import cricket.merstham.website.accounts.model.TokenStore;

import java.util.Optional;

public class DynamoService {

    private final ConfigurationService configurationService;
    private final AmazonDynamoDB dynamoDB;
    private final DynamoDBMapper mapper;
    private final DynamoDBMapper instanceMapper;

    public DynamoService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
        this.dynamoDB =
                AmazonDynamoDBClientBuilder.standard()
                        .withRegion(configurationService.getAwsRegion())
                        .build();

        this.mapper = new DynamoDBMapper(dynamoDB, new DynamoDBMapperConfig.Builder().build());
        this.instanceMapper =
                new DynamoDBMapper(
                        dynamoDB,
                        new DynamoDBMapperConfig.Builder()
                                .withConsistentReads(
                                        DynamoDBMapperConfig.ConsistentReads.CONSISTENT)
                                .withTableNameOverride(
                                        DynamoDBMapperConfig.TableNameOverride.withTableNamePrefix(
                                                configurationService
                                                        .getConfigurationName()
                                                        .concat("-")))
                                .build());
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

    public void writeAuditLog(Audit audit) {
        instanceMapper.save(audit);
    }

    public Optional<Audit> getAuditLog(String auditKey) {
        Audit audit = instanceMapper.load(Audit.class, auditKey);
        return Optional.ofNullable(audit);
    }

    public void writeErrorLog(Error error) {
        instanceMapper.save(error);
    }
}
