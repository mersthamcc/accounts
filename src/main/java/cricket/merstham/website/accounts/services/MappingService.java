package cricket.merstham.website.accounts.services;

import cricket.merstham.website.accounts.configuration.MappingConfiguration;
import cricket.merstham.website.accounts.model.EposNowTransaction;
import cricket.merstham.website.accounts.sage.model.*;

import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MappingService {

    private final ConfigurationService configurationService;
    private final DynamoService dynamoService;
    private final EposNowService eposNowService;
    private final MappingConfiguration mappingConfiguration;

    public MappingService(
            ConfigurationService configurationService,
            EposNowService eposNowService,
            DynamoService dynamoService) {
        this.configurationService = configurationService;
        this.eposNowService = eposNowService;
        this.dynamoService = dynamoService;
        this.mappingConfiguration = dynamoService.getConfig().getMappingConfiguration();
    }

    public PostSalesInvoicesSalesInvoice salesInvoiceFromEposTransaction(
            EposNowTransaction transaction) {
        var invoice =
                new PostSalesInvoicesSalesInvoice()
                        .reference(transaction.getBarcode())
                        .contactId(mapCustomerId(transaction.getCustomerId()))
                        .date(transaction.getDateTime().toLocalDate())
                        .dueDate(transaction.getDateTime().toLocalDate())
                        .mainAddress(
                                new PostSalesCorrectiveInvoicesSalesCorrectiveInvoiceMainAddress()
                                        .addressLine1("Bar Sales"));
        List<PostSalesCreditNotesSalesCreditNoteCreditNoteLines> lines = new ArrayList<>();
        if (transaction.getTransactionItems() != null) {
            lines.addAll(
                    transaction.getTransactionItems().stream()
                            .filter(t -> t.getQuantity().longValue() > 0)
                            .map(i -> createLine(i))
                            .collect(Collectors.toList()));
        }

        if (transaction.getMiscProductItems() != null) {
            lines.addAll(
                    transaction.getMiscProductItems().stream()
                            .filter(i -> i.getUnitPrice().doubleValue() > 0.00)
                            .map(i -> createLine(i))
                            .collect(Collectors.toList()));
        }
        invoice.invoiceLines(lines);
        return invoice;
    }

    public PostContactPaymentsContactPayment paymentForEposTransaction(
            EposNowTransaction transaction, SalesInvoice salesInvoice) {
        return new PostContactPaymentsContactPayment()
                .bankAccountId(
                        mappingConfiguration.getTenderMapping(
                                transaction.getTenders().get(0).getTenderTypeId().intValue()))
                .transactionTypeId("CUSTOMER_RECEIPT")
                .contactId(salesInvoice.getContact().getId())
                .totalAmount(transaction.getTotalAmount().doubleValue())
                .date(transaction.getDateTime().toLocalDate())
                .allocatedArtefacts(
                        List.of(
                                new PostContactPaymentsContactPaymentAllocatedArtefacts()
                                        .amount(transaction.getTotalAmount().doubleValue())
                                        .artefactId(salesInvoice.getId())))
                .reference(transaction.getBarcode());
    }

    public PostSalesCreditNotesSalesCreditNote creditNoteFromEposTransaction(
            EposNowTransaction transaction) {
        var creditNote =
                new PostSalesCreditNotesSalesCreditNote()
                        .reference(transaction.getBarcode())
                        .contactId(mapCustomerId(transaction.getCustomerId()))
                        .date(transaction.getDateTime().toLocalDate())
                        .mainAddress(
                                new PostSalesCorrectiveInvoicesSalesCorrectiveInvoiceMainAddress()
                                        .addressLine1("Bar Sales"));
        List<PostSalesCreditNotesSalesCreditNoteCreditNoteLines> lines = new ArrayList<>();
        if (transaction.getTransactionItems() != null) {
            lines.addAll(
                    transaction.getTransactionItems().stream()
                            .filter(t -> t.getQuantity().longValue() < 0)
                            .map(i -> createLine(i))
                            .collect(Collectors.toList()));
        }

        if (transaction.getMiscProductItems() != null) {
            lines.addAll(
                    transaction.getMiscProductItems().stream()
                            .filter(i -> i.getUnitPrice().doubleValue() < 0.00)
                            .map(i -> createLine(i))
                            .collect(Collectors.toList()));
        }
        creditNote.creditNoteLines(lines);
        return creditNote;
    }

    public PostContactPaymentsContactPayment refundForEposTransaction(
            EposNowTransaction transaction, SalesCreditNote creditNote) {
        return new PostContactPaymentsContactPayment()
                .bankAccountId(
                        mappingConfiguration.getTenderMapping(
                                transaction.getTenders().get(0).getTenderTypeId().intValue()))
                .transactionTypeId("CUSTOMER_REFUND")
                .contactId(creditNote.getContact().getId())
                .totalAmount(transaction.getTotalAmount().abs().doubleValue())
                .date(transaction.getDateTime().toLocalDate())
                .allocatedArtefacts(
                        List.of(
                                new PostContactPaymentsContactPaymentAllocatedArtefacts()
                                        .amount(transaction.getTotalAmount().abs().doubleValue())
                                        .artefactId(creditNote.getId())))
                .reference(transaction.getBarcode());
    }

    private PostSalesCreditNotesSalesCreditNoteCreditNoteLines createLine(
            EposNowTransaction.MiscProductItem i) {
        return new PostSalesCreditNotesSalesCreditNoteCreditNoteLines()
                .description(i.getName())
                .quantity(i.getQuantity().abs().doubleValue())
                .unitPrice(
                        i.getUnitPriceExcTax()
                                .abs()
                                .setScale(2, RoundingMode.HALF_DOWN)
                                .doubleValue())
                .taxAmount(
                        (i.getQuantity().abs().longValue()
                                        * i.getUnitPrice()
                                                .abs()
                                                .setScale(2, RoundingMode.HALF_DOWN)
                                                .doubleValue())
                                - (i.getQuantity().abs().longValue()
                                        * i.getUnitPriceExcTax()
                                                .abs()
                                                .setScale(2, RoundingMode.HALF_DOWN)
                                                .doubleValue()))
                .ledgerAccountId(mappingConfiguration.getDefaultLedgerAccountId())
                .taxRateId(mappingConfiguration.getDefaultTaxRateId());
    }

    private PostSalesCreditNotesSalesCreditNoteCreditNoteLines createLine(
            EposNowTransaction.TransactionItem i) {
        return new PostSalesCreditNotesSalesCreditNoteCreditNoteLines()
                .description(
                        eposNowService.getProduct(i.getProductId().intValue()).getDescription())
                .quantity(i.getQuantity().abs().doubleValue())
                .unitPrice(
                        i.getUnitPriceExcTax()
                                .abs()
                                .setScale(2, RoundingMode.HALF_DOWN)
                                .doubleValue())
                .taxAmount(
                        (i.getQuantity().abs().longValue()
                                        * i.getUnitPrice()
                                                .abs()
                                                .setScale(2, RoundingMode.HALF_DOWN)
                                                .doubleValue())
                                - (i.getQuantity().abs().longValue()
                                        * i.getUnitPriceExcTax()
                                                .abs()
                                                .setScale(2, RoundingMode.HALF_DOWN)
                                                .doubleValue()))
                .ledgerAccountId(mappingConfiguration.getDefaultLedgerAccountId())
                .taxRateId(mappingConfiguration.getDefaultTaxRateId());
    }

    private String mapCustomerId(BigInteger customerId) {
        if (customerId == null) {
            return mappingConfiguration.getDefaultCustomerId();
        }
        return mappingConfiguration.getDefaultCustomerId();
    }
}
