package cricket.merstham.website.accounts.configuration;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;

@DynamoDBDocument
public class PlayCricketTeamMapping {

    private String playCricketName;
    private String customerId;
    private String ledgerId;

    @DynamoDBAttribute(attributeName = "play_cricket_team_name")
    public String getPlayCricketName() {
        return playCricketName;
    }

    public PlayCricketTeamMapping setPlayCricketName(String playCricketName) {
        this.playCricketName = playCricketName;
        return this;
    }

    @DynamoDBAttribute(attributeName = "sage_customer_id")
    public String getCustomerId() {
        return customerId;
    }

    public PlayCricketTeamMapping setCustomerId(String customerId) {
        this.customerId = customerId;
        return this;
    }

    @DynamoDBAttribute(attributeName = "sage_ledger_id")
    public String getLedgerId() {
        return ledgerId;
    }

    public PlayCricketTeamMapping setLedgerId(String ledgerId) {
        this.ledgerId = ledgerId;
        return this;
    }
}
