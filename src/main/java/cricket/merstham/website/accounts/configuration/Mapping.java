package cricket.merstham.website.accounts.configuration;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;

@DynamoDBDocument
public class Mapping {
    private String eposValue;
    private String sageValue;

    @DynamoDBAttribute(attributeName = "epos_value")
    public String getEposValue() {
        return eposValue;
    }

    @DynamoDBAttribute(attributeName = "sage_value")
    public String getSageValue() {
        return sageValue;
    }

    public Mapping setEposValue(String eposValue) {
        this.eposValue = eposValue;
        return this;
    }

    public Mapping setSageValue(String sageValue) {
        this.sageValue = sageValue;
        return this;
    }
}
