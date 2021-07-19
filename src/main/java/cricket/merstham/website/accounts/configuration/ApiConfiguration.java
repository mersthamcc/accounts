package cricket.merstham.website.accounts.configuration;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;

@DynamoDBDocument
public class ApiConfiguration {
    private String eposApiKey;
    private String eposApiSecret;
    private String sageApiKey;
    private String sageApiSecret;
    private boolean eposValidateEndOfDay = true;
    private String queueUrl;

    @DynamoDBAttribute(attributeName = "epos_api_key")
    public String getEposApiKey() {
        return eposApiKey;
    }

    public ApiConfiguration setEposApiKey(String eposApiKey) {
        this.eposApiKey = eposApiKey;
        return this;
    }

    @DynamoDBAttribute(attributeName = "epos_api_secret")
    public String getEposApiSecret() {
        return eposApiSecret;
    }

    public ApiConfiguration setEposApiSecret(String eposApiSecret) {
        this.eposApiSecret = eposApiSecret;
        return this;
    }

    @DynamoDBAttribute(attributeName = "sage_api_key")
    public String getSageApiKey() {
        return sageApiKey;
    }

    public ApiConfiguration setSageApiKey(String sageApiKey) {
        this.sageApiKey = sageApiKey;
        return this;
    }

    @DynamoDBAttribute(attributeName = "sage_api_secret")
    public String getSageApiSecret() {
        return sageApiSecret;
    }

    public ApiConfiguration setSageApiSecret(String sageApiSecret) {
        this.sageApiSecret = sageApiSecret;
        return this;
    }

    @DynamoDBAttribute(attributeName = "epos_validate_end_of_day")
    public boolean isEposValidateEndOfDay() {
        return eposValidateEndOfDay;
    }

    public ApiConfiguration setEposValidateEndOfDay(boolean eposValidateEndOfDay) {
        this.eposValidateEndOfDay = eposValidateEndOfDay;
        return this;
    }

    @DynamoDBAttribute(attributeName = "queue_url")
    public String getQueueUrl() {
        return queueUrl;
    }

    public ApiConfiguration setQueueUrl(String queueUrl) {
        this.queueUrl = queueUrl;
        return this;
    }
}
