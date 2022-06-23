package cricket.merstham.website.accounts.services;

import cricket.merstham.website.accounts.configuration.Configuration;
import cricket.merstham.website.accounts.configuration.PlayCricketTeamMapping;
import cricket.merstham.website.accounts.model.EposNowTransaction;
import cricket.merstham.website.accounts.model.PlayCricketMatch;
import cricket.merstham.website.accounts.model.PlayCricketPlayer;
import cricket.merstham.website.accounts.sage.model.PostContactPaymentsContactPayment;
import cricket.merstham.website.accounts.sage.model.PostContactPaymentsContactPaymentAllocatedArtefacts;
import cricket.merstham.website.accounts.sage.model.PostSalesCorrectiveInvoicesSalesCorrectiveInvoiceMainAddress;
import cricket.merstham.website.accounts.sage.model.PostSalesCreditNotesSalesCreditNote;
import cricket.merstham.website.accounts.sage.model.PostSalesCreditNotesSalesCreditNoteCreditNoteLines;
import cricket.merstham.website.accounts.sage.model.PostSalesInvoicesSalesInvoice;
import cricket.merstham.website.accounts.sage.model.PostSalesQuickEntriesSalesQuickEntry;
import cricket.merstham.website.accounts.sage.model.SalesCreditNote;
import cricket.merstham.website.accounts.sage.model.SalesInvoice;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.text.MessageFormat.format;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class MappingService {
    private final EposNowService eposNowService;
    private final Configuration configuration;
    private final PlayCricketService playCricketService;

    public MappingService(
            EposNowService eposNowService,
            DynamoService dynamoService,
            PlayCricketService playCricketService) {
        this.eposNowService = eposNowService;
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
                        lines.add(createLine(item, 1));
                    } else {
                        for (var subItem : item.getMultipleChoiceItems()) {
                            lines.add(createLine(subItem, item.getQuantity().intValue()));
                        }
                    }
                }
            }
        }

        if (nonNull(transaction.getMiscProductItems())) {
            lines.addAll(
                    transaction.getMiscProductItems().stream()
                            .filter(i -> i.getUnitPrice().doubleValue() > 0.00)
                            .map(i -> createLine(i))
                            .collect(Collectors.toList()));
        }

        if (nonNull(transaction.getOtherItems())) {
            lines.addAll(
                    transaction.getOtherItems().stream()
                            .map(this::createDiscountLine)
                            .collect(Collectors.toList()));
        }

        var total =
                lines.stream()
                        .mapToDouble(l -> (l.getQuantity() * l.getUnitPrice()) + l.getTaxAmount())
                        .sum();
        if (total != transaction.getTotalAmount().setScale(2).doubleValue()) {
            lines.add(
                    createRoundingLine(
                            transaction.getTotalAmount().setScale(2).doubleValue() - total));
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
                .totalAmount(
                        tender.getAmount().doubleValue() - tender.getChangeGiven().doubleValue())
                .date(salesInvoice.getDate())
                .allocatedArtefacts(
                        List.of(
                                new PostContactPaymentsContactPaymentAllocatedArtefacts()
                                        .amount(
                                                tender.getAmount().doubleValue()
                                                        - tender.getChangeGiven().doubleValue())
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
                        lines.add(createLine(item, 1));
                    } else {
                        for (var subItem : item.getMultipleChoiceItems()) {
                            lines.add(createLine(subItem, item.getQuantity().intValue()));
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

    private PostSalesCreditNotesSalesCreditNoteCreditNoteLines createDiscountLine(
            EposNowTransaction.OtherItem item) {
        return new PostSalesCreditNotesSalesCreditNoteCreditNoteLines()
                .description(item.getItemType())
                .quantity(BigDecimal.ONE.doubleValue())
                .ledgerAccountId(
                        configuration.getMappingConfiguration().getDefaultLedgerAccountId())
                .unitPrice(-item.getAmount().doubleValue())
                .taxAmount(BigDecimal.ZERO.doubleValue())
                .taxRateId(configuration.getMappingConfiguration().getNoTaxRateId());
    }

    private PostSalesCreditNotesSalesCreditNoteCreditNoteLines createRoundingLine(
            double difference) {
        return new PostSalesCreditNotesSalesCreditNoteCreditNoteLines()
                .description("Rounding")
                .quantity(BigDecimal.ONE.doubleValue())
                .ledgerAccountId(
                        configuration.getMappingConfiguration().getDefaultLedgerAccountId())
                .unitPrice(difference)
                .taxAmount(BigDecimal.ZERO.doubleValue())
                .taxRateId(configuration.getMappingConfiguration().getNoTaxRateId());
    }

    private PostSalesCreditNotesSalesCreditNoteCreditNoteLines createLine(
            EposNowTransaction.MiscProductItem i) {
        Optional<EposNowTransaction.Tax> tax = getItemTax(i.getTaxes());
        return new PostSalesCreditNotesSalesCreditNoteCreditNoteLines()
                .description(i.getName())
                .quantity(i.getQuantity().abs().doubleValue())
                .unitPrice(
                        i.getUnitPriceExcTax()
                                .abs()
                                .setScale(2, RoundingMode.HALF_DOWN)
                                .doubleValue())
                .taxAmount(
                        tax.map(EposNowTransaction.Tax::getAmount)
                                .orElse(BigDecimal.ZERO)
                                .abs()
                                .setScale(2, RoundingMode.HALF_UP)
                                .doubleValue())
                .ledgerAccountId(
                        configuration.getMappingConfiguration().getDefaultLedgerAccountId())
                .taxRateId(
                        tax.map(EposNowTransaction.Tax::getName)
                                .map(this::mapTaxRate)
                                .orElse(
                                        configuration
                                                .getMappingConfiguration()
                                                .getDefaultTaxRateId()));
    }

    private PostSalesCreditNotesSalesCreditNoteCreditNoteLines createLine(
            EposNowTransaction.TransactionItem i, int parentQuantity) {
        Optional<EposNowTransaction.Tax> tax = getItemTax(i.getTaxes());

        return new PostSalesCreditNotesSalesCreditNoteCreditNoteLines()
                .description(eposNowService.getProductDescription(i.getProductId()))
                .quantity(parentQuantity * i.getQuantity().abs().doubleValue())
                .unitPrice(
                        i.getUnitPriceExcTax()
                                .abs()
                                .setScale(2, RoundingMode.HALF_DOWN)
                                .doubleValue())
                .taxAmount(
                        tax.map(EposNowTransaction.Tax::getAmount)
                                .orElse(BigDecimal.ZERO)
                                .abs()
                                .setScale(2, RoundingMode.HALF_UP)
                                .doubleValue())
                .ledgerAccountId(
                        configuration
                                .getMappingConfiguration()
                                .getLedgerMapping(i.getProductId().longValue()))
                .taxRateId(
                        tax.map(EposNowTransaction.Tax::getName)
                                .map(this::mapTaxRate)
                                .orElse(
                                        configuration
                                                .getMappingConfiguration()
                                                .getDefaultTaxRateId()));
    }

    private Optional<EposNowTransaction.Tax> getItemTax(List<EposNowTransaction.Tax> taxes) {
        if (isNull(taxes) || taxes.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(taxes.get(0));
    }

    private String mapTaxRate(String taxRateName) {
        return configuration.getMappingConfiguration().getTaxRateMapping(taxRateName);
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
                        .orElseThrow();
        return new PostSalesQuickEntriesSalesQuickEntry()
                .quickEntryTypeId("invoice")
                .contactId(mapping.getCustomerId())
                .ledgerAccountId(mapping.getLedgerId())
                .date(match.getMatchDate())
                .reference(normalisePlayerName(p))
                .details(p.getPlayerName())
                .netAmount(10.00)
                .totalAmount(10.00)
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
