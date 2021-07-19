package cricket.merstham.website.accounts.configuration;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;

import java.util.List;

@DynamoDBDocument
public class MappingConfiguration {

    private String defaultCustomerId;
    private String defaultLedgerAccountId;
    private String defaultTaxRateId;
    private List<Mapping> ledgerMapping;
    private List<Mapping> taxRateMapping;
    private List<Mapping> tenderMapping;

    @DynamoDBAttribute(attributeName = "default_customer_id")
    public String getDefaultCustomerId() {
        return defaultCustomerId;
    }

    public MappingConfiguration setDefaultCustomerId(String defaultCustomerId) {
        this.defaultCustomerId = defaultCustomerId;
        return this;
    }

    @DynamoDBAttribute(attributeName = "default_ledger_account_id")
    public String getDefaultLedgerAccountId() {
        return defaultLedgerAccountId;
    }

    public MappingConfiguration setDefaultLedgerAccountId(String defaultLedgerAccountId) {
        this.defaultLedgerAccountId = defaultLedgerAccountId;
        return this;
    }

    @DynamoDBAttribute(attributeName = "default_tax_rate_id")
    public String getDefaultTaxRateId() {
        return defaultTaxRateId;
    }

    public MappingConfiguration setDefaultTaxRateId(String defaultTaxRateId) {
        this.defaultTaxRateId = defaultTaxRateId;
        return this;
    }

    @DynamoDBAttribute(attributeName = "ledger_mapping")
    public List<Mapping> getLedgerMapping() {
        return ledgerMapping;
    }

    public MappingConfiguration setLedgerMapping(List<Mapping> ledgerMapping) {
        this.ledgerMapping = ledgerMapping;
        return this;
    }

    @DynamoDBAttribute(attributeName = "tax_rate_mapping")
    public List<Mapping> getTaxRateMapping() {
        return taxRateMapping;
    }

    public MappingConfiguration setTaxRateMapping(List<Mapping> taxRateMapping) {
        this.taxRateMapping = taxRateMapping;
        return this;
    }

    @DynamoDBAttribute(attributeName = "tender_mapping")
    public List<Mapping> getTenderMapping() {
        return tenderMapping;
    }

    public MappingConfiguration setTenderMapping(List<Mapping> tenderMapping) {
        this.tenderMapping = tenderMapping;
        return this;
    }

    public String getTaxRateMapping(long taxRateId) {
        return taxRateMapping.stream()
                .filter(t -> Long.parseLong(t.getEposValue()) == taxRateId)
                .findFirst()
                .orElse(new Mapping().setSageValue(defaultTaxRateId))
                .getSageValue();
    }

    public String getLedgerMapping(long productCode) {
        return ledgerMapping.stream()
                .filter(l -> Long.parseLong(l.getEposValue()) == productCode)
                .findFirst()
                .orElse(new Mapping().setSageValue(defaultLedgerAccountId))
                .getSageValue();
    }

    public String getTenderMapping(long tenderType) {
        return tenderMapping.stream()
                .filter(t -> Long.parseLong(t.getEposValue()) == tenderType)
                .findFirst()
                .orElseThrow()
                .getSageValue();
    }
}
