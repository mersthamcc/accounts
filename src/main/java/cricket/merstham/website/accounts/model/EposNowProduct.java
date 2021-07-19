package cricket.merstham.website.accounts.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EposNowProduct {

    @JsonProperty("Id")
    private int id;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Description")
    private String description;

    @JsonProperty("CategoryId")
    private int categoryId;

    @JsonProperty("Barcode")
    private String barcode;

    @JsonProperty("SalePrice")
    private BigDecimal salePrice;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getBarcode() {
        return barcode;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }
}
