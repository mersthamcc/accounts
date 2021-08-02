package cricket.merstham.website.accounts.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import cricket.merstham.website.accounts.helpers.LocalDateTimeConverter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@DynamoDBTable(tableName = "audit")
public class Audit {
    private String barcode;
    private String sageReference;
    private String sageCustomerName;
    private String sageCustomerId;
    private String sageDocumentType;
    private List<String> sagePaymentId = new ArrayList<>();
    private LocalDateTime dateTransferred;

    @DynamoDBHashKey(attributeName = "barcode")
    public String getBarcode() {
        return barcode;
    }

    public Audit setBarcode(String barcode) {
        this.barcode = barcode;
        return this;
    }

    @DynamoDBAttribute(attributeName = "sage_reference")
    public String getSageReference() {
        return sageReference;
    }

    public Audit setSageReference(String sageReference) {
        this.sageReference = sageReference;
        return this;
    }

    @DynamoDBAttribute(attributeName = "sage_customer_name")
    public String getSageCustomerName() {
        return sageCustomerName;
    }

    public Audit setSageCustomerName(String sageCustomerName) {
        this.sageCustomerName = sageCustomerName;
        return this;
    }

    @DynamoDBAttribute(attributeName = "sage_customer_id")
    public String getSageCustomerId() {
        return sageCustomerId;
    }

    public Audit setSageCustomerId(String sageCustomerId) {
        this.sageCustomerId = sageCustomerId;
        return this;
    }

    @DynamoDBAttribute(attributeName = "sage_document_type")
    public String getSageDocumentType() {
        return sageDocumentType;
    }

    public Audit setSageDocumentType(String sageDocumentType) {
        this.sageDocumentType = sageDocumentType;
        return this;
    }

    @DynamoDBAttribute(attributeName = "sage_payment_id")
    public List<String> getSagePaymentId() {
        return sagePaymentId;
    }

    public Audit setSagePaymentId(List<String> sagePaymentId) {
        this.sagePaymentId = sagePaymentId;
        return this;
    }

    public Audit addSagePaymentId(String sagePaymentId) {
        this.sagePaymentId.add(sagePaymentId);
        return this;
    }

    @DynamoDBAttribute(attributeName = "date_transferred")
    @DynamoDBTypeConverted(converter = LocalDateTimeConverter.class)
    public LocalDateTime getDateTransferred() {
        return dateTransferred;
    }

    public Audit setDateTransferred(LocalDateTime dateTransferred) {
        this.dateTransferred = dateTransferred;
        return this;
    }
}
