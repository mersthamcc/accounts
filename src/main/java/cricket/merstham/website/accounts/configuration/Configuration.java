package cricket.merstham.website.accounts.configuration;

import com.amazonaws.services.dynamodbv2.datamodeling.*;

@DynamoDBTable(tableName = "config")
@DynamoDBDocument
public class Configuration {

    private String id;
    private ApiConfiguration apiConfiguration;
    private MappingConfiguration mappingConfiguration;

    @DynamoDBHashKey(attributeName = "id")
    public String getId() {
        return id;
    }

    public Configuration setId(String id) {
        this.id = id;
        return this;
    }

    @DynamoDBAttribute(attributeName = "api_configuration")
    public ApiConfiguration getApiConfiguration() {
        return apiConfiguration;
    }

    public Configuration setApiConfiguration(ApiConfiguration apiConfiguration) {
        this.apiConfiguration = apiConfiguration;
        return this;
    }

    @DynamoDBAttribute(attributeName = "mapping_configuration")
    public MappingConfiguration getMappingConfiguration() {
        return mappingConfiguration;
    }

    public Configuration setMappingConfiguration(MappingConfiguration mappingConfiguration) {
        this.mappingConfiguration = mappingConfiguration;
        return this;
    }
}
