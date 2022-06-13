package cricket.merstham.website.accounts.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import cricket.merstham.website.accounts.model.*;
import cricket.merstham.website.accounts.model.Error;
import cricket.merstham.website.accounts.sage.ApiClient;
import cricket.merstham.website.accounts.sage.ApiException;
import cricket.merstham.website.accounts.sage.ApiResponse;
import cricket.merstham.website.accounts.sage.Pair;
import cricket.merstham.website.accounts.sage.api.ContactPaymentsApi;
import cricket.merstham.website.accounts.sage.api.SalesCreditNotesApi;
import cricket.merstham.website.accounts.sage.api.SalesInvoicesApi;
import cricket.merstham.website.accounts.sage.api.SalesQuickEntriesApi;
import cricket.merstham.website.accounts.sage.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.GenericType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.text.MessageFormat.format;
import static java.util.Objects.nonNull;

public class SageAccountingService {
    private static final Logger LOG = LoggerFactory.getLogger(SageAccountingService.class);

    private final SerializationService serializationService;
    private final MappingService mappingService;
    private final DynamoService dynamoService;
    private final ApiClient apiClient;

    public SageAccountingService(
            MappingService mappingService,
            DynamoService dynamoService,
            SageApiClient sageApiClient) {
        this.mappingService = mappingService;
        this.dynamoService = dynamoService;
        this.apiClient = sageApiClient;
        this.serializationService = SerializationService.getInstance();
    }

    public boolean createEposNowSalesTransaction(EposNowTransaction transaction)
            throws JsonProcessingException {
        SalesInvoicesApi salesInvoicesApi = new SalesInvoicesApi(apiClient);
        SalesCreditNotesApi creditNotesApi = new SalesCreditNotesApi(apiClient);
        ContactPaymentsApi paymentsApi = new ContactPaymentsApi(apiClient);
        Optional<Audit> previousAuditEntry = dynamoService.getAuditLog(transaction.getBarcode());
        if (previousAuditEntry.isEmpty()) {
            if (transaction.getTotalAmount().doubleValue() > 0.00
                    && containsInvoiceableItems(transaction)
                    && nonNull(transaction.getTenders())) {
                LOG.info(
                        "Creating Sage sales invoice artefacts for transaction {}",
                        transaction.getBarcode());
                return createSalesInvoiceAndPayment(transaction, salesInvoicesApi, paymentsApi);
            } else if (transaction.getTotalAmount().doubleValue() < 0.00
                    && containsRefundItems(transaction)
                    && nonNull(transaction.getTenders())) {
                LOG.info(
                        "Creating Sage credit note artefacts for transaction {}",
                        transaction.getBarcode());
                return createCreditNoteAndPayment(transaction, creditNotesApi, paymentsApi);
            } else {
                LOG.warn(
                        "Skipping transaction {} as has a zero value, no transaction items or tenders",
                        transaction.getBarcode());
                return false;
            }
        } else {
            LOG.warn(
                    "Skipping transaction {} as previously transferred at {}",
                    transaction.getBarcode(),
                    previousAuditEntry.get().getDateTransferred());
            return false;
        }
    }

    public boolean createQuickEntriesForMatchFees(
            PlayCricketMatch match, List<PlayCricketPlayer> players) {
        SalesQuickEntriesApi api = new SalesQuickEntriesApi(apiClient);

        try {
            players.forEach(
                    p -> {
                        PostSalesQuickEntriesSalesQuickEntry request =
                                mappingService.createQuickEntryForMatchFee(match, p);
                        try {
                            String auditKey =
                                    format(
                                            "PC-{0,number,#}-{1,number,#}",
                                            match.getId(),
                                            p.getPlayerId());

                            Optional<Audit> previousAuditEntry =
                                    dynamoService.getAuditLog(auditKey);
                            if (previousAuditEntry.isEmpty()) {
                                LOG.info(
                                        "Creating QE for team = {}/{}, player = {}, reference = {}, cost = £{}",
                                        match.getHomeTeamName(),
                                        match.getAwayTeamName(),
                                        p.getPlayerName(),
                                        request.getReference(),
                                        request.getTotalAmount().doubleValue());
                                var response =
                                        api.postSalesQuickEntries(
                                                new PostSalesQuickEntries()
                                                        .salesQuickEntry(request));
                                Audit audit =
                                        new Audit()
                                                .setBarcode(auditKey)
                                                .setSageCustomerId(request.getContactId())
                                                .setSageCustomerName(request.getContactName())
                                                .setSageDocumentType("QE Sales Invoice")
                                                .setSagePaymentId(List.of())
                                                .setDateTransferred(LocalDateTime.now())
                                                .setSageReference(response.getReference());
                                dynamoService.writeAuditLog(audit);
                            } else {
                                LOG.warn(
                                        "Skipping QE for team = {}/{}, player = {}, as previously transferred at {}",
                                        match.getHomeTeamName(),
                                        match.getAwayTeamName(),
                                        p.getPlayerName(),
                                        previousAuditEntry.get().getDateTransferred());
                            }
                        } catch (ApiException e) {
                            throw new RuntimeException(e);
                        }
                    });
            return true;
        } catch (RuntimeException e) {
            LOG.error("Error creating quick entry", e);
            return false;
        }
    }

    public List<String> getUnpaidIds(LocalDate startDate, LocalDate endDate)
            throws ApiException, JsonProcessingException {
        SalesQuickEntriesApi api = new SalesQuickEntriesApi(apiClient);
        List<String> ids = new ArrayList<>();
        boolean done = false;
        int page = 1;
        int pageSize = 200;

        while (!done) {
            final String accepts = apiClient.selectHeaderAccept(new String[] {"application/json"});

            final String contentType = apiClient.selectHeaderContentType(new String[] {});

            List<Pair> queryParams = new ArrayList<>();
            queryParams.add(new Pair("from_date", startDate.toString()));
            queryParams.add(new Pair("to_date", endDate.toString()));
            queryParams.add(new Pair("status_id", "UNPAID"));
            queryParams.add(new Pair("items_per_page", Integer.toString(pageSize)));
            queryParams.add(new Pair("page", Integer.toString(page)));
            queryParams.add(new Pair("attributes", "status"));

            Map<String, String> headers = new HashMap<>();

            ApiResponse<SagePager<List<QuickEntry>>> response =
                    apiClient.invokeAPI(
                            "/sales_quick_entries",
                            "GET",
                            queryParams,
                            null,
                            headers,
                            new HashMap<String, Object>(),
                            accepts,
                            contentType,
                            new String[] {"oauth"},
                            new GenericType<>() {});

            LOG.info("Response status code = {}", response.getStatusCode());
            LOG.info("Response body = {}", response.getData());
            ids.addAll(
                    response.getData().getItems().stream()
                            .map(e -> e.getId())
                            .collect(Collectors.toList()));
            if (response.getData().getNext() == null) {
                done = true;
            } else {
                page = page + 1;
            }
        }
        return ids;
    }

    public void deleteEntries(List<String> idsToDelete) {
        SalesQuickEntriesApi api = new SalesQuickEntriesApi(apiClient);

        idsToDelete.forEach(
                id -> {
                    LOG.info("Removing QE Entry {}", id);

                    try {
                        api.deleteSalesQuickEntriesKey(id);
                    } catch (ApiException e) {
                        LOG.error("Error deleting item", e);
                        throw new RuntimeException(e);
                    }
                });
    }

    private boolean createCreditNoteAndPayment(
            EposNowTransaction transaction,
            SalesCreditNotesApi creditNotesApi,
            ContactPaymentsApi paymentsApi) {
        var now = LocalDateTime.now();
        Error error =
                new Error()
                        .setId(format("{0}-{1}", transaction.getBarcode(), now.toString()))
                        .setTransferDate(now);
        try {
            PostSalesCreditNotesSalesCreditNote creditNoteRequest =
                    mappingService.creditNoteFromEposTransaction(transaction);

            Audit audit = new Audit().setBarcode(transaction.getBarcode()).setDateTransferred(now);

            var creditNote =
                    creditNotesApi.postSalesCreditNotes(
                            new PostSalesCreditNotes().salesCreditNote(creditNoteRequest));

            dynamoService.writeAuditLog(
                    audit.setSageReference(creditNote.getDisplayedAs())
                            .setSageCustomerId(creditNote.getContact().getId())
                            .setSageCustomerName(creditNote.getContactName())
                            .setSageDocumentType("Credit Notes"));
            error.getProgress().put("CREDIT NOTE CREATED", creditNote.getId());

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
            error.getProgress().put("REFUND CREATED", contactPayment.getId());

            LOG.info(
                    "Created {} for transaction {} with payment ({}) of £{} in {} on {}",
                    creditNote.getDisplayedAs(),
                    transaction.getBarcode(),
                    contactPayment.getId(),
                    transaction.getTotalAmount().setScale(2),
                    payment.getBankAccountId(),
                    creditNote.getDate());

            dynamoService.writeAuditLog(audit.addSagePaymentId(contactPayment.getId()));
            return true;
        } catch (cricket.merstham.website.accounts.sage.ApiException e) {
            LOG.error(
                    "Error creating Sage credit note for transaction {}", transaction.getBarcode());
            error.setSource(serializationService.serialise(transaction));
            return createErrorLog(error, e);
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
        var now = LocalDateTime.now();
        Error error =
                new Error()
                        .setId(format("{0}-{1}", transaction.getBarcode(), now.toString()))
                        .setTransferDate(now);
        try {
            PostSalesInvoicesSalesInvoice salesInvoices =
                    mappingService.salesInvoiceFromEposTransaction(transaction);
            Audit audit = new Audit().setBarcode(transaction.getBarcode()).setDateTransferred(now);

            var salesInvoice =
                    salesInvoicesApi.postSalesInvoices(
                            new PostSalesInvoices().salesInvoice(salesInvoices));

            dynamoService.writeAuditLog(
                    audit.setSageReference(salesInvoice.getDisplayedAs())
                            .setSageCustomerId(salesInvoice.getContact().getId())
                            .setSageCustomerName(salesInvoice.getContactName())
                            .setSageDocumentType("Sales Invoice"));
            error.getProgress().put("SALES ORDER CREATED", salesInvoice.getId());
            if (transaction.getTenders() == null) {
                LOG.info(
                        "Invoice {} for transaction {} on {} for £{} but no tender found.",
                        salesInvoice.getDisplayedAs(),
                        transaction.getBarcode(),
                        salesInvoice.getDate(),
                        transaction.getTotalAmount().setScale(2));
                return false;
            }
            int i = 0;
            for (var tender : transaction.getTenders()) {
                i = i + 1;
                PostContactPaymentsContactPayment payment =
                        mappingService.paymentForEposTransaction(tender, salesInvoice);

                ContactPayment contactPayment =
                        paymentsApi.postContactPayments(
                                new PostContactPayments().contactPayment(payment));

                dynamoService.writeAuditLog(audit.addSagePaymentId(contactPayment.getId()));
                error.getProgress().put(format("PAYMENT CREATED {0}", i), contactPayment.getId());
            }
            LOG.info(
                    "Created {} for transaction {} with payments ({}) of £{} on {}",
                    salesInvoice.getDisplayedAs(),
                    transaction.getBarcode(),
                    String.join(", ", audit.getSagePaymentId()),
                    transaction.getTotalAmount().setScale(2),
                    salesInvoice.getDate());
            return true;
        } catch (cricket.merstham.website.accounts.sage.ApiException e) {
            LOG.error("Error creating Sage invoice for transaction {}", transaction.getBarcode());
            error.setSource(serializationService.serialise(transaction));
            return createErrorLog(error, e);
        }
    }

    private boolean createErrorLog(Error error, ApiException e) {
        LOG.error("Sage API Exception", e);
        error.setExceptionName(e.getClass().getCanonicalName())
                .setStatusCode(e.getCode())
                .setExceptionMessage(e.getMessage())
                .setStackTrace(
                        String.join(
                                "\n",
                                Arrays.stream(e.getStackTrace())
                                        .map(stackTraceElement -> stackTraceElement.toString())
                                        .collect(Collectors.toList())));
        dynamoService.writeErrorLog(error);
        return false;
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
}
