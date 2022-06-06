package cricket.merstham.website.accounts.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import cricket.merstham.website.accounts.helpers.LocalDateTimeConverter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@DynamoDBTable(tableName = "error")
public class Error {
    private String id;
    private LocalDateTime transferDate;
    private String source;
    private String exceptionName;
    private String exceptionMessage;
    private String stackTrace;
    private int statusCode;
    private boolean reviewed = false;
    private Map<String, String> progress = new HashMap<>();

    @DynamoDBHashKey(attributeName = "id")
    public String getId() {
        return id;
    }

    public Error setId(String id) {
        this.id = id;
        return this;
    }

    @DynamoDBAttribute(attributeName = "transfer_date")
    @DynamoDBTypeConverted(converter = LocalDateTimeConverter.class)
    public LocalDateTime getTransferDate() {
        return transferDate;
    }

    public Error setTransferDate(LocalDateTime transferDate) {
        this.transferDate = transferDate;
        return this;
    }

    @DynamoDBAttribute(attributeName = "source")
    public String getSource() {
        return source;
    }

    public Error setSource(String source) {
        this.source = source;
        return this;
    }

    @DynamoDBAttribute(attributeName = "exception_name")
    public String getExceptionName() {
        return exceptionName;
    }

    public Error setExceptionName(String exceptionName) {
        this.exceptionName = exceptionName;
        return this;
    }

    @DynamoDBAttribute(attributeName = "exception_message")
    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public Error setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
        return this;
    }

    @DynamoDBAttribute(attributeName = "stacktrace")
    public String getStackTrace() {
        return stackTrace;
    }

    public Error setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
        return this;
    }

    @DynamoDBAttribute(attributeName = "reviewed")
    public boolean isReviewed() {
        return reviewed;
    }

    public Error setReviewed(boolean reviewed) {
        this.reviewed = reviewed;
        return this;
    }

    @DynamoDBAttribute(attributeName = "progress")
    public Map<String, String> getProgress() {
        return progress;
    }

    public Error setProgress(Map<String, String> progress) {
        this.progress = progress;
        return this;
    }

    @DynamoDBAttribute(attributeName = "status_code")
    public int getStatusCode() {
        return statusCode;
    }

    public Error setStatusCode(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }
}
