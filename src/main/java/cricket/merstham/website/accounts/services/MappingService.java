package cricket.merstham.website.accounts.services;

import cricket.merstham.website.accounts.configuration.Configuration;
import cricket.merstham.website.accounts.configuration.PlayCricketTeamMapping;
import cricket.merstham.website.accounts.model.EposNowTransaction;
import cricket.merstham.website.accounts.model.PlayCricketMatch;
import cricket.merstham.website.accounts.model.PlayCricketPlayer;
import cricket.merstham.website.accounts.sage.model.*;

import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.text.MessageFormat.format;

public class MappingService {

    private final ConfigurationService configurationService;
    private final DynamoService dynamoService;
    private final EposNowService eposNowService;
    private final Configuration configuration;
    private final PlayCricketService playCricketService;

    public MappingService(
            ConfigurationService configurationService,
            EposNowService eposNowService,
            DynamoService dynamoService,
            PlayCricketService playCricketService) {
        this.configurationService = configurationService;
        this.eposNowService = eposNowService;
        this.dynamoService = dynamoService;
        this.configuration = dynamoService.getConfig();
        this.playCricketService = playCricketService;
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
            for (var item : transaction.getTransactionItems()) {
                if (item.getQuantity().longValue() > 0) {
                    if (item.getMultipleChoiceItems() == null
                            || item.getMultipleChoiceItems().isEmpty()) {
                        lines.add(createLine(item));
                    } else {
                        for (var subItem : item.getMultipleChoiceItems()) {
                            lines.add(createLine(subItem));
                        }
                    }
                }
            }
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
            EposNowTransaction.Tender tender, SalesInvoice salesInvoice) {
        return new PostContactPaymentsContactPayment()
                .bankAccountId(
                        configuration
                                .getMappingConfiguration()
                                .getTenderMapping(tender.getTenderTypeId().intValue()))
                .transactionTypeId("CUSTOMER_RECEIPT")
                .contactId(salesInvoice.getContact().getId())
                .totalAmount(tender.getAmount().doubleValue() - tender.getChangeGiven().doubleValue())
                .date(salesInvoice.getDate())
                .allocatedArtefacts(
                        List.of(
                                new PostContactPaymentsContactPaymentAllocatedArtefacts()
                                        .amount(tender.getAmount().doubleValue())
                                        .artefactId(salesInvoice.getId())))
                .reference(salesInvoice.getReference());
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
            for (var item : transaction.getTransactionItems()) {
                if (item.getQuantity().longValue() < 0) {
                    if (item.getMultipleChoiceItems() == null
                            || item.getMultipleChoiceItems().isEmpty()) {
                        lines.add(createLine(item));
                    } else {
                        for (var subItem : item.getMultipleChoiceItems()) {
                            lines.add(createLine(subItem));
                        }
                    }
                }
            }
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
                        configuration
                                .getMappingConfiguration()
                                .getTenderMapping(
                                        transaction
                                                .getTenders()
                                                .get(0)
                                                .getTenderTypeId()
                                                .intValue()))
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
                .ledgerAccountId(
                        configuration.getMappingConfiguration().getDefaultLedgerAccountId())
                .taxRateId(configuration.getMappingConfiguration().getDefaultTaxRateId());
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
                .ledgerAccountId(
                        configuration.getMappingConfiguration().getDefaultLedgerAccountId())
                .taxRateId(configuration.getMappingConfiguration().getDefaultTaxRateId());
    }

    private String mapCustomerId(BigInteger customerId) {
        if (customerId == null) {
            return configuration.getMappingConfiguration().getDefaultCustomerId();
        }
        return configuration.getMappingConfiguration().getDefaultCustomerId();
    }

    public PostSalesQuickEntriesSalesQuickEntry createQuickEntryForMatchFee(
            PlayCricketMatch match, PlayCricketPlayer p) {
        PlayCricketTeamMapping mapping =
                configuration.getPlayCricketTeamMapping().stream()
                        .filter(
                                m ->
                                        m.getPlayCricketName()
                                                .equals(playCricketService.getOurTeamName(match)))
                        .findFirst()
                        .get();
        return new PostSalesQuickEntriesSalesQuickEntry()
                .quickEntryTypeId("invoice")
                .contactId(mapping.getCustomerId())
                .ledgerAccountId(mapping.getLedgerId())
                .date(match.getMatchDate())
                .reference(normalisePlayerName(p))
                .details(p.getPlayerName())
                .netAmount(5.00)
                .totalAmount(5.00)
                .taxRateId("GB_EXEMPT");
    }

    private String normalisePlayerName(PlayCricketPlayer p) {
        String[] split = p.getPlayerName().toUpperCase().split("\\s+", 2);

        if (split.length == 1) {
            return p.getPlayerName().toUpperCase();
        }
        String formatted = format("{0}, {1}", split[1], split[0]);

        return formatted.substring(0, Math.min(formatted.length(), 25));
    }
}
