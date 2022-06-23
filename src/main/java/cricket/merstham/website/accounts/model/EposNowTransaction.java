package cricket.merstham.website.accounts.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EposNowTransaction {

    @JsonProperty("Id")
    private BigInteger id;

    @JsonProperty("CustomerId")
    private BigInteger customerId;

    @JsonProperty("StaffId")
    private BigInteger staffId;

    @JsonProperty("TableId")
    private BigInteger tableId;

    @JsonProperty("DeviceId")
    private BigInteger deviceId;

    @JsonProperty("Device")
    private String device;

    @JsonProperty("DateTime")
    private LocalDateTime dateTime;

    @JsonProperty("StatusId")
    private BigInteger statusId;

    @JsonProperty("Barcode")
    private String barcode;

    @JsonProperty("ServiceType")
    private BigInteger serviceType;

    @JsonProperty("TotalAmount")
    private BigDecimal totalAmount;

    @JsonProperty("ServiceCharge")
    private BigDecimal serviceCharge;

    @JsonProperty("Gratuity")
    private BigDecimal gratuity;

    @JsonProperty("DiscountValue")
    private BigDecimal discountValue;

    @JsonProperty("NonDiscountable")
    private BigDecimal nonDiscountable;

    @JsonProperty("NonTax")
    private BigDecimal nonTax;

    @JsonProperty("DiscountReasonId")
    private BigInteger discountReasonId;

    @JsonProperty("ReferenceCode")
    private String referenceCode;

    @JsonProperty("Table")
    private Table table;

    @JsonProperty("TransactionItems")
    private List<TransactionItem> transactionItems;

    @JsonProperty("MiscProductItems")
    private List<MiscProductItem> miscProductItems;

    @JsonProperty("OtherItems")
    private List<OtherItem> otherItems;

    @JsonProperty("Tenders")
    private List<Tender> tenders;

    @JsonProperty("OtherTenders")
    private List<OtherTender> otherTenders;

    @JsonProperty("TransactionDetails")
    private List<TransactionDetail> transactionDetails;

    @JsonProperty("Taxes")
    private List<Tax> taxes;

    @JsonProperty("AdjustStock")
    private boolean adjustStock;

    public BigInteger getId() {
        return id;
    }

    public BigInteger getCustomerId() {
        return customerId;
    }

    public BigInteger getStaffId() {
        return staffId;
    }

    public BigInteger getTableId() {
        return tableId;
    }

    public BigInteger getDeviceId() {
        return deviceId;
    }

    public String getDevice() {
        return device;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public BigInteger getStatusId() {
        return statusId;
    }

    public String getBarcode() {
        return barcode;
    }

    public BigInteger getServiceType() {
        return serviceType;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public BigDecimal getServiceCharge() {
        return serviceCharge;
    }

    public BigDecimal getGratuity() {
        return gratuity;
    }

    public BigDecimal getDiscountValue() {
        return discountValue;
    }

    public BigDecimal getNonDiscountable() {
        return nonDiscountable;
    }

    public BigDecimal getNonTax() {
        return nonTax;
    }

    public BigInteger getDiscountReasonId() {
        return discountReasonId;
    }

    public String getReferenceCode() {
        return referenceCode;
    }

    public Table getTable() {
        return table;
    }

    public List<TransactionItem> getTransactionItems() {
        return transactionItems;
    }

    public List<MiscProductItem> getMiscProductItems() {
        return miscProductItems;
    }

    public List<OtherItem> getOtherItems() {
        return otherItems;
    }

    public List<Tender> getTenders() {
        return tenders;
    }

    public List<OtherTender> getOtherTenders() {
        return otherTenders;
    }

    public List<TransactionDetail> getTransactionDetails() {
        return transactionDetails;
    }

    public List<Tax> getTaxes() {
        return taxes;
    }

    public boolean isAdjustStock() {
        return adjustStock;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Table {
        @JsonProperty("TableId")
        private BigInteger tableId;

        @JsonProperty("Covers")
        private BigInteger covers;

        @JsonProperty("Seats")
        private BigInteger seats;

        public BigInteger getTableId() {
            return tableId;
        }

        public BigInteger getCovers() {
            return covers;
        }

        public BigInteger getSeats() {
            return seats;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MeasurementDetails {
        @JsonProperty("TransactionItemId")
        private BigInteger transactionItemId;

        @JsonProperty("MeasurementSchemeItemId")
        private BigInteger measurementSchemeItemId;

        @JsonProperty("UnitPrice")
        private BigDecimal unitPrice;

        @JsonProperty("UnitPriceExcTax")
        private BigDecimal unitPriceExcTax;

        @JsonProperty("UnitPriceFactor")
        private BigDecimal unitPriceFactor;

        @JsonProperty("MeasurementUnit")
        private String measurementUnit;

        @JsonProperty("UnitVolume")
        private BigDecimal unitVolume;

        @JsonProperty("CostPriceMeasurementSchemeItemId")
        private BigInteger costPriceMeasurementSchemeItemId;

        @JsonProperty("CostPrice")
        private BigDecimal costPrice;

        @JsonProperty("CostPriceFactor")
        private BigDecimal costPriceFactor;

        @JsonProperty("CostPriceMeasurementUnit")
        private String costPriceMeasurementUnit;

        @JsonProperty("CostPriceUnitVolume")
        private BigDecimal costPriceUnitVolume;

        @JsonProperty("Quantity")
        private BigInteger quantity;

        public BigInteger getTransactionItemId() {
            return transactionItemId;
        }

        public BigInteger getMeasurementSchemeItemId() {
            return measurementSchemeItemId;
        }

        public BigDecimal getUnitPrice() {
            return unitPrice;
        }

        public BigDecimal getUnitPriceExcTax() {
            return unitPriceExcTax;
        }

        public BigDecimal getUnitPriceFactor() {
            return unitPriceFactor;
        }

        public String getMeasurementUnit() {
            return measurementUnit;
        }

        public BigDecimal getUnitVolume() {
            return unitVolume;
        }

        public BigInteger getCostPriceMeasurementSchemeItemId() {
            return costPriceMeasurementSchemeItemId;
        }

        public BigDecimal getCostPrice() {
            return costPrice;
        }

        public BigDecimal getCostPriceFactor() {
            return costPriceFactor;
        }

        public String getCostPriceMeasurementUnit() {
            return costPriceMeasurementUnit;
        }

        public BigDecimal getCostPriceUnitVolume() {
            return costPriceUnitVolume;
        }

        public BigInteger getQuantity() {
            return quantity;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ContainerFees {
        @JsonProperty("ContainerFeeId")
        private BigInteger containerFeeId;

        @JsonProperty("Name")
        private String name;

        @JsonProperty("Rate")
        private BigDecimal rate;

        @JsonProperty("Amount")
        private BigDecimal amount;

        @JsonProperty("AmountIncTax")
        private BigDecimal amountIncTax;

        public BigInteger getContainerFeeId() {
            return containerFeeId;
        }

        public String getName() {
            return name;
        }

        public BigDecimal getRate() {
            return rate;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public BigDecimal getAmountIncTax() {
            return amountIncTax;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Tax {
        @JsonProperty("TaxRateId")
        private BigInteger taxRateId;

        @JsonProperty("Name")
        private String name;

        @JsonProperty("Rate")
        private BigDecimal rate;

        @JsonProperty("Amount")
        private BigDecimal amount;

        public BigInteger getTaxRateId() {
            return taxRateId;
        }

        public String getName() {
            return name;
        }

        public BigDecimal getRate() {
            return rate;
        }

        public BigDecimal getAmount() {
            return amount;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TransactionItem {
        @JsonProperty("Id")
        private BigInteger id;

        @JsonProperty("TransactionId")
        private BigInteger transactionId;

        @JsonProperty("ProductId")
        private BigInteger productId;

        @JsonProperty("UnitPrice")
        private BigDecimal unitPrice;

        @JsonProperty("UnitPriceExcTax")
        private BigDecimal unitPriceExcTax;

        @JsonProperty("CostPrice")
        private BigDecimal costPrice;

        @JsonProperty("TaxGroupId")
        private BigInteger taxGroupId;

        @JsonProperty("TaxAmount")
        private BigDecimal taxAmount;

        @JsonProperty("Quantity")
        private BigInteger quantity;

        @JsonProperty("DiscountAmount")
        private BigDecimal discountAmount;

        @JsonProperty("DiscountReasonId")
        private BigInteger discountReasonId;

        @JsonProperty("RefundReasonId")
        private BigInteger refundReasonId;

        @JsonProperty("Notes")
        private String notes;

        @JsonProperty("PrBigIntegerOnOrder")
        private boolean prBigIntegerOnOrder;

        @JsonProperty("MultipleChoiceProductId")
        private BigInteger multipleChoiceProductId;

        @JsonProperty("ParentId")
        private BigInteger parentId;

        @JsonProperty("IsTaxExempt")
        private boolean isTaxExempt;

        @JsonProperty("MeasurementDetails")
        private MeasurementDetails measurementDetails;

        @JsonProperty("ContainerFees")
        private ContainerFees containerFees;

        @JsonProperty("Taxes")
        private List<Tax> taxes;

        @JsonProperty("MultipleChoiceItems")
        private List<TransactionItem> multipleChoiceItems;

        public BigInteger getId() {
            return id;
        }

        public BigInteger getTransactionId() {
            return transactionId;
        }

        public BigInteger getProductId() {
            return productId;
        }

        public BigDecimal getUnitPrice() {
            return unitPrice;
        }

        public BigDecimal getUnitPriceExcTax() {
            return unitPriceExcTax;
        }

        public BigDecimal getCostPrice() {
            return costPrice;
        }

        public BigInteger getTaxGroupId() {
            return taxGroupId;
        }

        public BigDecimal getTaxAmount() {
            return taxAmount;
        }

        public BigInteger getQuantity() {
            return quantity;
        }

        public BigDecimal getDiscountAmount() {
            return discountAmount;
        }

        public BigInteger getDiscountReasonId() {
            return discountReasonId;
        }

        public BigInteger getRefundReasonId() {
            return refundReasonId;
        }

        public String getNotes() {
            return notes;
        }

        public boolean isPrBigIntegerOnOrder() {
            return prBigIntegerOnOrder;
        }

        public BigInteger getMultipleChoiceProductId() {
            return multipleChoiceProductId;
        }

        public BigInteger getParentId() {
            return parentId;
        }

        public boolean isTaxExempt() {
            return isTaxExempt;
        }

        public MeasurementDetails getMeasurementDetails() {
            return measurementDetails;
        }

        public ContainerFees getContainerFees() {
            return containerFees;
        }

        public List<Tax> getTaxes() {
            return taxes;
        }

        public List<TransactionItem> getMultipleChoiceItems() {
            return multipleChoiceItems;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MiscProductItem {
        @JsonProperty("Id")
        private BigInteger id;

        @JsonProperty("TransactionId")
        private BigInteger transactionId;

        @JsonProperty("Name")
        private String name;

        @JsonProperty("UnitPrice")
        private BigDecimal unitPrice;

        @JsonProperty("UnitPriceExcTax")
        private BigDecimal unitPriceExcTax;

        @JsonProperty("PrBigIntegerOnOrder")
        private boolean prBigIntegerOnOrder;

        @JsonProperty("RefundReasonId")
        private BigInteger refundReasonId;

        @JsonProperty("TaxAmount")
        private BigDecimal taxAmount;

        @JsonProperty("TaxGroupId")
        private BigInteger taxGroupId;

        @JsonProperty("Taxes")
        private List<Tax> taxes;

        @JsonProperty("Quantity")
        private BigInteger quantity;

        public BigInteger getId() {
            return id;
        }

        public BigInteger getTransactionId() {
            return transactionId;
        }

        public String getName() {
            return name;
        }

        public BigDecimal getUnitPrice() {
            return unitPrice;
        }

        public BigDecimal getUnitPriceExcTax() {
            return unitPriceExcTax;
        }

        public boolean isPrBigIntegerOnOrder() {
            return prBigIntegerOnOrder;
        }

        public BigInteger getRefundReasonId() {
            return refundReasonId;
        }

        public BigDecimal getTaxAmount() {
            return taxAmount;
        }

        public BigInteger getTaxGroupId() {
            return taxGroupId;
        }

        public List<Tax> getTaxes() {
            return taxes;
        }

        public BigInteger getQuantity() {
            return quantity;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OtherItem {
        BigInteger itemTypeId;
        String itemType;
        BigDecimal amount;

        @JsonProperty("ItemTypeId")
        public BigInteger getItemTypeId() {
            return this.itemTypeId;
        }

        public void setItemTypeId(BigInteger itemTypeId) {
            this.itemTypeId = itemTypeId;
        }

        @JsonProperty("ItemType")
        public String getItemType() {
            return this.itemType;
        }

        public void setItemType(String itemType) {
            this.itemType = itemType;
        }

        @JsonProperty("Amount")
        public BigDecimal getAmount() {
            return this.amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Tender {
        @JsonProperty("TenderTypeId")
        private BigInteger tenderTypeId;

        @JsonProperty("Amount")
        private BigDecimal amount;

        @JsonProperty("ChangeGiven")
        private BigDecimal changeGiven;

        @JsonProperty("IsCashback")
        private boolean isCashback;

        public BigInteger getTenderTypeId() {
            return tenderTypeId;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public BigDecimal getChangeGiven() {
            return changeGiven;
        }

        public boolean isCashback() {
            return isCashback;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OtherTender {
        @JsonProperty("ItemTypeId")
        private BigInteger itemTypeId;

        @JsonProperty("ItemType")
        private String itemType;

        @JsonProperty("Amount")
        private BigDecimal amount;

        public BigInteger getItemTypeId() {
            return itemTypeId;
        }

        public String getItemType() {
            return itemType;
        }

        public BigDecimal getAmount() {
            return amount;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TransactionDetail {
        @JsonProperty("Name")
        private String name;

        @JsonProperty("Value")
        private String value;

        @JsonProperty("ShowOnTill")
        private boolean showOnTill;

        @JsonProperty("PrBigIntegerOnOrder")
        private boolean prBigIntegerOnOrder;

        @JsonProperty("PrBigIntegerOnReceipt")
        private boolean prBigIntegerOnReceipt;

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }

        public boolean isShowOnTill() {
            return showOnTill;
        }

        public boolean isPrBigIntegerOnOrder() {
            return prBigIntegerOnOrder;
        }

        public boolean isPrBigIntegerOnReceipt() {
            return prBigIntegerOnReceipt;
        }
    }
}
