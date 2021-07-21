package cricket.merstham.website.accounts.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import cricket.merstham.website.accounts.configuration.ApiConfiguration;
import cricket.merstham.website.accounts.configuration.Configuration;
import cricket.merstham.website.accounts.model.Audit;
import cricket.merstham.website.accounts.model.EposNowTransaction;
import cricket.merstham.website.accounts.sage.ApiClient;
import cricket.merstham.website.accounts.sage.api.ContactPaymentsApi;
import cricket.merstham.website.accounts.sage.api.SalesCreditNotesApi;
import cricket.merstham.website.accounts.sage.api.SalesInvoicesApi;
import cricket.merstham.website.accounts.sage.model.*;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import static java.text.MessageFormat.format;

public class SageAccountingService {
    private static final Logger LOG = LoggerFactory.getLogger(SageAccountingService.class);
    private static final URI AUTH_URL = URI.create("https://www.sageone.com/oauth2/auth/central");
    private static final URI TOKEN_URL = URI.create("https://oauth.accounting.sage.com/token");

    private final ApiConfiguration apiConfiguration;
    private final ConfigurationService configurationService;
    private final TokenManager tokenManager;
    private final SerializationService serializationService;
    private final MappingService mappingService;
    private final DynamoService dynamoService;

    public SageAccountingService(
            Configuration configuration,
            ConfigurationService configurationService,
            TokenManager tokenManager,
            MappingService mappingService,
            DynamoService dynamoService) {
        this.apiConfiguration = configuration.getApiConfiguration();
        this.configurationService = configurationService;
        this.tokenManager = tokenManager;
        this.mappingService = mappingService;
        this.dynamoService = dynamoService;
        this.serializationService = new SerializationService();
    }

    public boolean createEposNowSalesTransaction(EposNowTransaction transaction)
            throws JsonProcessingException {
        ApiClient apiClient = getClient();
        SalesInvoicesApi salesInvoicesApi = new SalesInvoicesApi(apiClient);
        SalesCreditNotesApi creditNotesApi = new SalesCreditNotesApi(apiClient);
        ContactPaymentsApi paymentsApi = new ContactPaymentsApi(apiClient);
        refreshClient(apiClient);
        if (transaction.getTotalAmount().doubleValue() > 0.00
                && containsInvoiceableItems(transaction)) {
            LOG.info(
                    "Creating Sage sales invoice artefacts for transaction {}",
                    transaction.getBarcode());
            return createSalesInvoiceAndPayment(transaction, salesInvoicesApi, paymentsApi);
        } else if (transaction.getTotalAmount().doubleValue() < 0.00
                && containsRefundItems(transaction)) {
            LOG.info(
                    "Creating Sage credit note artefacts for transaction {}",
                    transaction.getBarcode());
            return createCreditNoteAndPayment(transaction, creditNotesApi, paymentsApi);
        } else {
            LOG.warn(
                    "Skipping transaction {} as has a zero value or no transaction items",
                    transaction.getBarcode());
            return false;
        }
    }

    private boolean createCreditNoteAndPayment(
            EposNowTransaction transaction,
            SalesCreditNotesApi creditNotesApi,
            ContactPaymentsApi paymentsApi) {
        try {
            PostSalesCreditNotesSalesCreditNote creditNoteRequest =
                    mappingService.creditNoteFromEposTransaction(transaction);

            Audit audit =
                    new Audit()
                            .setBarcode(transaction.getBarcode())
                            .setDateTransferred(LocalDateTime.now());

            var creditNote =
                    creditNotesApi.postSalesCreditNotes(
                            new PostSalesCreditNotes().salesCreditNote(creditNoteRequest));

            dynamoService.writeAuditLog(
                    audit.setSageReference(creditNote.getDisplayedAs())
                            .setSageCustomerId(creditNote.getContact().getId())
                            .setSageCustomerName(creditNote.getContactName())
                            .setSageDocumentType("Credit Notes"));

            if (transaction.getTenders() == null) {
                LOG.info(
                        "Credit note {} for transaction {} on {} for £{} but no tender found.",
                        creditNote.getDisplayedAs(),
                        transaction.getBarcode(),
                        creditNote.getDate(),
                        transaction.getTotalAmount().setScale(2));
                return false;
            }
            PostContactPaymentsContactPayment payment =
                    mappingService.refundForEposTransaction(transaction, creditNote);

            ContactPayment contactPayment =
                    paymentsApi.postContactPayments(
                            new PostContactPayments().contactPayment(payment));
            LOG.info(
                    "Created {} for transaction {} with payment ({}) of £{} in {} on {}",
                    creditNote.getDisplayedAs(),
                    transaction.getBarcode(),
                    contactPayment.getId(),
                    transaction.getTotalAmount().setScale(2),
                    payment.getBankAccountId(),
                    creditNote.getDate());

            dynamoService.writeAuditLog(audit.setSagePaymentId(contactPayment.getId()));
            return true;
        } catch (cricket.merstham.website.accounts.sage.ApiException e) {
            LOG.error(
                    "Error creating Sage credit note for transaction {}", transaction.getBarcode());
            LOG.error("Sage API Exception", e);
            return false;
        }
    }

    private boolean containsRefundItems(EposNowTransaction transaction) {
        return (transaction.getTransactionItems() != null
                        && transaction.getTransactionItems().stream()
                                        .filter(t -> t.getQuantity().longValue() < 0)
                                        .count()
                                > 0)
                || (transaction.getMiscProductItems() != null
                        && transaction.getMiscProductItems().stream()
                                        .filter(t -> t.getUnitPrice().doubleValue() < 0.00)
                                        .count()
                                > 0);
    }

    private boolean createSalesInvoiceAndPayment(
            EposNowTransaction transaction,
            SalesInvoicesApi salesInvoicesApi,
            ContactPaymentsApi paymentsApi) {
        try {
            PostSalesInvoicesSalesInvoice salesInvoices =
                    mappingService.salesInvoiceFromEposTransaction(transaction);
            Audit audit =
                    new Audit()
                            .setBarcode(transaction.getBarcode())
                            .setDateTransferred(LocalDateTime.now());

            var salesInvoice =
                    salesInvoicesApi.postSalesInvoices(
                            new PostSalesInvoices().salesInvoice(salesInvoices));
            dynamoService.writeAuditLog(
                    audit.setSageReference(salesInvoice.getDisplayedAs())
                            .setSageCustomerId(salesInvoice.getContact().getId())
                            .setSageCustomerName(salesInvoice.getContactName())
                            .setSageDocumentType("Sales Invoice"));

            if (transaction.getTenders() == null) {
                LOG.info(
                        "Invoice {} for transaction {} on {} for £{} but no tender found.",
                        salesInvoice.getDisplayedAs(),
                        transaction.getBarcode(),
                        salesInvoice.getDate(),
                        transaction.getTotalAmount().setScale(2));
                return false;
            }
            PostContactPaymentsContactPayment payment =
                    mappingService.paymentForEposTransaction(transaction, salesInvoice);

            ContactPayment contactPayment =
                    paymentsApi.postContactPayments(
                            new PostContactPayments().contactPayment(payment));
            LOG.info(
                    "Created {} for transaction {} with payment ({}) of £{} in {} on {}",
                    salesInvoice.getDisplayedAs(),
                    transaction.getBarcode(),
                    contactPayment.getId(),
                    transaction.getTotalAmount().setScale(2),
                    payment.getBankAccountId(),
                    salesInvoice.getDate());
            dynamoService.writeAuditLog(audit.setSagePaymentId(contactPayment.getId()));
            return true;
        } catch (cricket.merstham.website.accounts.sage.ApiException e) {
            LOG.error("Error creating Sage invoice for transaction {}", transaction.getBarcode());
            LOG.error("Sage API Exception", e);
            return false;
        }
    }

    private boolean containsInvoiceableItems(EposNowTransaction transaction) {
        return (transaction.getTransactionItems() != null
                        && transaction.getTransactionItems().stream()
                                        .filter(t -> t.getQuantity().longValue() > 0)
                                        .count()
                                > 0)
                || (transaction.getMiscProductItems() != null
                        && transaction.getMiscProductItems().stream()
                                        .filter(t -> t.getUnitPrice().doubleValue() > 0.00)
                                        .count()
                                > 0);
    }

    private ApiClient getClient() {
        ApiClient apiClient = new ApiClient();
        apiClient.setUserAgent("mersthamcc.co.uk Accounts Interface");
        apiClient.setAccessToken(tokenManager.getTokenStore().getAccessToken());
        return apiClient;
    }

    private ApiClient refreshClient(ApiClient apiClient) throws JsonProcessingException {
        if (tokenManager.isAccessTokenExpired()) {
            apiClient.setAccessToken(refreshToken());
        }
        return apiClient;
    }

    public URI getAuthUrl(String state) {
        return UriBuilder.fromUri(AUTH_URL)
                .queryParam("country", "gb")
                .queryParam("locale", "en-GB")
                .queryParam("client_id", apiConfiguration.getSageApiKey())
                .queryParam("response_type", "code")
                .queryParam(
                        "redirect_uri",
                        format("{0}/sage-callback", configurationService.getBaseUrl()))
                .queryParam("state", state)
                .queryParam("scopes", "full_access")
                .build();
    }

    public String exchangeForToken(String code) {
        var form = new Form();
        form.param("grant_type", "authorization_code");
        form.param("code", code);
        form.param("redirect_uri", format("{0}/sage-callback", configurationService.getBaseUrl()));

        return tokenRequest(form);
    }

    public String refreshToken() {
        var form = new Form();
        form.param("grant_type", "refresh_token");
        form.param("refresh_token", tokenManager.getTokenStore().getRefreshToken());

        return tokenRequest(form);
    }

    private String tokenRequest(Form form) {
        Client client = JerseyClientBuilder.createClient();
        form.param("client_id", apiConfiguration.getSageApiKey());
        form.param("client_secret", apiConfiguration.getSageApiSecret());
        LOG.info(
                "Performing token request with parameters = {}",
                String.join(
                        "&",
                        form.asMap().entrySet().stream()
                                .map(
                                        e ->
                                                String.join(
                                                        ",",
                                                        e.getValue().stream()
                                                                .map(
                                                                        v ->
                                                                                format(
                                                                                        "{0}={1}",
                                                                                        e.getKey(),
                                                                                        v))
                                                                .collect(Collectors.toList())))
                                .collect(Collectors.toList())));
        Response response = client.target(TOKEN_URL).request().post(Entity.form(form));
        String body = response.readEntity(String.class);
        LOG.info("Received token response = {}", body);
        JsonNode json = serializationService.deserialise(body);
        if (json.has("error_description")) {
            throw new RuntimeException(json.get("error_description").asText());
        }
        long expiresIn = json.get("expires_in").asLong(0);
        long refreshExpiresIn = json.get("refresh_token_expires_in").asLong(0);
        LOG.info(
                "Access token expires in {} seconds - Refresh Token Expires in {} seconds",
                expiresIn,
                refreshExpiresIn);
        LocalDateTime accessTokenExpiry = LocalDateTime.now().plusSeconds(expiresIn);
        LocalDateTime refreshTokenExpiry = LocalDateTime.now().plusSeconds(refreshExpiresIn);
        tokenManager.update(
                json.get("access_token").asText(),
                accessTokenExpiry,
                json.get("refresh_token").asText(),
                refreshTokenExpiry);
        return tokenManager.getTokenStore().getAccessToken();
    }
}
