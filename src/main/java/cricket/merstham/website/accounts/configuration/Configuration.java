package cricket.merstham.website.accounts.configuration;

import com.amazonaws.services.dynamodbv2.datamodeling.*;

import java.util.List;

@DynamoDBTable(tableName = "config")
@DynamoDBDocument
public class Configuration {

    private String id;
    private ApiConfiguration apiConfiguration;
    private MappingConfiguration mappingConfiguration;
    private List<PlayCricketTeamMapping> playCricketTeamMapping;

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

    @DynamoDBAttribute(attributeName = "play_cricket_team_mapping")
    public List<PlayCricketTeamMapping> getPlayCricketTeamMapping() {
        return playCricketTeamMapping;
    }

    public Configuration setPlayCricketTeamMapping(
            List<PlayCricketTeamMapping> playCricketTeamMapping) {
        this.playCricketTeamMapping = playCricketTeamMapping;
        return this;
    }
}
