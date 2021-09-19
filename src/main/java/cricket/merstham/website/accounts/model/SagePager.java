package cricket.merstham.website.accounts.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
public class SagePager<T> {
    @JsonProperty("$total")
    private int total;

    @JsonProperty("$itemsPerPage")
    private int itemsPerPage;

    @JsonProperty("$next")
    private String next;

    @JsonProperty("$back")
    private String back;

    @JsonProperty("$items")
    private T items;

    public int getTotal() {
        return total;
    }

    public int getItemsPerPage() {
        return itemsPerPage;
    }

    public String getNext() {
        return next;
    }

    public String getBack() {
        return back;
    }

    public T getItems() {
        return items;
    }
}
