package cricket.merstham.website.accounts.services;

import cricket.merstham.website.accounts.model.EposNowEndOfDay;
import cricket.merstham.website.accounts.model.EposNowProduct;
import cricket.merstham.website.accounts.model.EposNowTransaction;

import javax.ws.rs.core.GenericType;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.text.MessageFormat.format;
import static java.util.Objects.isNull;

public class EposNowService {

    private static final String TRANSACTION_PATH = "/api/V4/Transaction/GetByDate";
    private static final String END_OF_DAY_PATH = "/api/V4/EndOfDay";
    private static final String PRODUCT_PATH = "/api/V4/Product";
    private static final int PAGE_SIZE = 200;

    private final EposNowApiClient apiClient;
    private final Map<Integer, EposNowProduct> productCache = new HashMap<>();

    public EposNowService(EposNowApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public boolean validateEndOfDay(EposNowEndOfDay webhook) {
        try {
            EposNowEndOfDay endOfDay =
                    apiClient.getRequest(
                            END_OF_DAY_PATH.concat(format("/{0,number,#}", webhook.getId())),
                            Map.of(),
                            EposNowEndOfDay.class);
            return (endOfDay.getId() == webhook.getId())
                    && endOfDay.getTotal().equals(webhook.getTotal())
                    && endOfDay.getStartTime().equals(webhook.getStartTime())
                    && endOfDay.getEndTime().equals(webhook.getEndTime());
        } catch (Exception ex) {
            return false;
        }
    }

    public List<EposNowTransaction> getTransactionsForDay(EposNowEndOfDay webhook) {
        List<EposNowTransaction> transactions = new ArrayList<>();
        int page = 1;
        boolean changed;
        do {
            changed =
                    transactions.addAll(
                            apiClient.getRequest(
                                    TRANSACTION_PATH,
                                    Map.of(
                                            "startDate", webhook.getStartTime(),
                                            "endDate", webhook.getEndTime(),
                                            "page", page),
                                    new GenericType<List<EposNowTransaction>>() {}));
            page = page + 1;
        } while (changed && (transactions.size() % PAGE_SIZE == 0));
        return transactions;
    }

    public EposNowProduct getProduct(int productId) {
        if (!productCache.keySet().contains(productId)) {
            refreshProductList();
        }
        return productCache.get(productId);
    }

    public String getProductDescription(BigInteger productId) {
        if (isNull(productId)) return "** unknown **";
        var product = getProduct(productId.intValue());
        if (isNull(product)) return "** product not found **";
        return product.getDescription();
    }

    private void refreshProductList() {
        productCache.clear();
        int page = 1;
        int added;
        do {
            List<EposNowProduct> products =
                    apiClient.getRequest(
                            PRODUCT_PATH,
                            Map.of("page", page),
                            new GenericType<List<EposNowProduct>>() {});
            added = products.size();
            for (var product : products) {
                productCache.put(product.getId(), product);
            }
            page = page + 1;
        } while (added == 0);
    }

    public void getCustomerSageId(BigInteger customerId) {}
}
