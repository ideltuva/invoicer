package com.api.invoicer.mapper.obl;

import com.api.invoicer.model.excelDto.*;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_21.*;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.*;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

import static com.api.invoicer.constant.OblConstant.*;
import static com.api.invoicer.util.StringUtil.extractTelephone;
import static com.api.invoicer.util.StringUtil.removeTelephone;

public class OblMapper {
    public OblMapper() {
    }

    public static InvoiceType mapToObl(Invoice excelInvoice) {
        final String id = Optional.ofNullable(excelInvoice.serialNumber()).orElse("");
        final NoteType noteType = new NoteType(Optional.ofNullable(excelInvoice.remarks()).orElse(""));
        final String orderNumber = Optional.ofNullable(excelInvoice.orderNumber()).orElse("");

        InvoiceType invoice = new InvoiceType();

        invoice.setUBLVersionID(OBL_VERSION);
        invoice.setCustomizationID(CUSTOMIZATION_ID);
        invoice.setProfileID(PROFILE_ID);
        invoice.setID(id);
        invoice.setUUID(generateUUID());
        invoice.setIssueDate(getIssueDateType(excelInvoice));
        invoice.setInvoiceTypeCode(getInvoiceTypeCode());
        invoice.setNote(List.of(noteType));
        invoice.setDocumentCurrencyCode(DOCUMENT_CURRENCY_CODE);
        invoice.setLineCountNumeric(LINE_COUNT_NUMERIC);
        invoice.setOrderReference(getOrderReference(orderNumber));
        invoice.setContractDocumentReference(List.of(getDocumentReference(orderNumber)));
        invoice.setProjectReference(List.of(getProjectReference()));
        invoice.setAccountingSupplierParty(getSupplierParty(excelInvoice));
        invoice.setAccountingCustomerParty(getCustomerParty(excelInvoice));
        invoice.setPaymentMeans(List.of(getPaymentMeansType(excelInvoice)));
        invoice.setTaxTotal(List.of(getTaxTotal(excelInvoice)));
        invoice.setLegalMonetaryTotal(getMonetaryTotal(excelInvoice));
        invoice.setInvoiceLine(getInvoiceLineTypes(excelInvoice));

        return invoice;
    }

    private static MonetaryTotalType getMonetaryTotal(Invoice excelInvoice) {
        final String currencyId = Optional.of(excelInvoice)
                .map(Invoice::total)
                .map(Total::price)
                .map(Price::currency)
                .orElse(DEFAULT_CURRENCY_ID);
        final String amount = Optional.of(excelInvoice)
                .map(Invoice::total)
                .map(Total::price)
                .map(Price::value)
                .orElse("0");
        final String sumCurrencyId = Optional.of(excelInvoice)
                .map(Invoice::total)
                .map(Total::sum)
                .map(Price::currency)
                .orElse(DEFAULT_CURRENCY_ID);
        final String sumAmount = Optional.of(excelInvoice)
                .map(Invoice::total)
                .map(Total::sum)
                .map(Price::value)
                .orElse("0");

        MonetaryTotalType monetaryTotal = new MonetaryTotalType();
        monetaryTotal.setLineExtensionAmount(getLineExtensionAmount(currencyId, amount));
        monetaryTotal.setTaxExclusiveAmount(getTaxExclusiveAmount(currencyId, amount));
        monetaryTotal.setTaxInclusiveAmount(getTaxInclusiveAmount(sumCurrencyId, sumAmount));
        monetaryTotal.setPrepaidAmount(getPrepaidAmount(sumCurrencyId));
        monetaryTotal.setPayableAmount(getPayableAmount(sumCurrencyId, sumAmount));
        return monetaryTotal;
    }

    private static PayableAmountType getPayableAmount(String sumCurrencyId, String sumAmount) {
        PayableAmountType payableAmount = new PayableAmountType();
        payableAmount.setCurrencyID(sumCurrencyId);
        payableAmount.setValue(BigDecimal.valueOf(Double.parseDouble(sumAmount)));
        return payableAmount;
    }

    private static PrepaidAmountType getPrepaidAmount(String sumCurrencyId) {
        PrepaidAmountType prepaidAmount = new PrepaidAmountType();
        prepaidAmount.setCurrencyID(sumCurrencyId);
        prepaidAmount.setValue(BigDecimal.valueOf(0));
        return prepaidAmount;
    }

    private static TaxInclusiveAmountType getTaxInclusiveAmount(String sumCurrencyId, String sumAmount) {
        TaxInclusiveAmountType taxInclusiveAmountType = new TaxInclusiveAmountType();
        taxInclusiveAmountType.setCurrencyID(sumCurrencyId);
        taxInclusiveAmountType.setValue(BigDecimal.valueOf(Double.parseDouble(sumAmount)));
        return taxInclusiveAmountType;
    }

    private static TaxExclusiveAmountType getTaxExclusiveAmount(String currencyId, String amount) {
        TaxExclusiveAmountType taxExclusiveAmountType = new TaxExclusiveAmountType();
        taxExclusiveAmountType.setCurrencyID(currencyId);
        taxExclusiveAmountType.setValue(BigDecimal.valueOf(Double.parseDouble(amount)));
        return taxExclusiveAmountType;
    }

    private static LineExtensionAmountType getLineExtensionAmount(String currencyId, String amount) {
        LineExtensionAmountType lineExtensionAmount = new LineExtensionAmountType();
        lineExtensionAmount.setCurrencyID(currencyId);
        lineExtensionAmount.setValue(BigDecimal.valueOf(Double.parseDouble(amount)));
        return lineExtensionAmount;
    }

    private static TaxTotalType getTaxTotal(Invoice excelInvoice) {
        final Total total = Optional.of(excelInvoice)
                .map(Invoice::total)
                .orElse(Total.builder().build());
        final String currencyId = Optional.of(total)
                .map(Total::price)
                .map(Price::currency)
                .orElse(DEFAULT_CURRENCY_ID);

        TaxTotalType taxTotalType =  new TaxTotalType();
        taxTotalType.setTaxAmount(getTaxAmount(total, currencyId));
        taxTotalType.setTaxSubtotal(List.of(getTaxSubtotal(total, currencyId)));
        return taxTotalType;
    }

    private static TaxSubtotalType getTaxSubtotal(Total total, String currencyId) {
        TaxSubtotalType taxSubtotal = new TaxSubtotalType();
        taxSubtotal.setTaxableAmount(getTaxableAmount(total, currencyId));
        taxSubtotal.setTaxAmount(getTaxAmount(total, currencyId));
        taxSubtotal.setPercent(DEFAULT_TAX_PERCENT);
        taxSubtotal.setTaxCategory(getTaxCategory(true));
        return taxSubtotal;
    }

    private static TaxCategoryType getTaxCategory(boolean setName) {
        TaxCategoryType taxCategoryType = new TaxCategoryType();
        taxCategoryType.setID(getTaxCategoryId());
        if (setName) taxCategoryType.setName(TAX_CATEGORY_NAME);
        taxCategoryType.setPercent(DEFAULT_TAX_PERCENT);
        taxCategoryType.setTaxScheme(getTaxScheme());
        return taxCategoryType;
    }

    private static TaxSchemeType getTaxScheme() {
        TaxSchemeType taxScheme = new TaxSchemeType();
        taxScheme.setID(VAT);
        return taxScheme;
    }

    private static IDType getTaxCategoryId() {
        IDType idType = new IDType();
        idType.setSchemeID(TAX_CATEGORY_SCHEME_ID);
        idType.setValue(TAX_CATEGORY_VALUE);
        return idType;
    }

    private static TaxableAmountType getTaxableAmount(Total total, String currencyId) {
        final String amount = Optional.of(total)
                .map(Total::price)
                .map(Price::value)
                .orElse("0");

        TaxableAmountType taxableAmountType = new TaxableAmountType();
        taxableAmountType.setCurrencyID(currencyId);
        taxableAmountType.setValue(BigDecimal.valueOf(Double.parseDouble(amount)));
        return taxableAmountType;
    }

    private static TaxAmountType getTaxAmount(Total total, String currencyId) {
        final String amount = Optional.of(total)
                .map(Total::standardTax)
                .orElse("0");

        TaxAmountType taxAmountType = new TaxAmountType();
        taxAmountType.setCurrencyID(currencyId);
        taxAmountType.setValue(BigDecimal.valueOf(Double.parseDouble(amount)));
        return taxAmountType;
    }

    private static PaymentMeansType getPaymentMeansType(Invoice excelInvoice) {
        final LocalDate dueDate = Optional.ofNullable(excelInvoice)
                .map(Invoice::dueDate)
                .orElse(LocalDate.now());

        PaymentMeansType paymentMeansType = new PaymentMeansType();
        paymentMeansType.setPaymentMeansCode(getPaymentMeansCodeType());
        paymentMeansType.setPaymentDueDate(dueDate);
        paymentMeansType.setPaymentChannelCode(PAYMENT_MEANS_CHANNEL_CODE);
        paymentMeansType.setPayeeFinancialAccount(getFinancialAccount(excelInvoice));
        return paymentMeansType;
    }

    private static FinancialAccountType getFinancialAccount(Invoice excelInvoice) {
        FinancialAccountType financialAccount =  new FinancialAccountType();
        financialAccount.setID(getFinancialAccountIdType(excelInvoice));
        financialAccount.setFinancialInstitutionBranch(getBranch());
        return financialAccount;
    }

    private static BranchType getBranch() {
        BranchType branch = new BranchType();
        branch.setFinancialInstitution(getFinancialInstitution());
        return branch;
    }

    private static FinancialInstitutionType getFinancialInstitution() {
        FinancialInstitutionType financialInstitution = new FinancialInstitutionType();
        financialInstitution.setID(getFinancialInstitutionIdType());
        return financialInstitution;
    }

    private static IDType getFinancialInstitutionIdType() {
        IDType idType = new IDType();
        idType.setSchemeID(PAYMENT_MEANS_FI_SCHEME_ID);
        idType.setValue(PAYMENT_MEANS_FI);
        return idType;
    }

    private static IDType getFinancialAccountIdType(Invoice excelInvoice) {
        final String accountNumber = Optional.ofNullable(excelInvoice)
                .map(Invoice::additionalInfo)
                .map(AdditionalInfo::accountNumber)
                .orElse("");

        IDType idType = new IDType();
        idType.setSchemeID(PAYMENT_MEANS_CHANNEL_CODE);
        idType.setValue(accountNumber);
        return idType;
    }

    private static PaymentMeansCodeType getPaymentMeansCodeType() {
        PaymentMeansCodeType paymentMeansCodeType = new PaymentMeansCodeType();
        paymentMeansCodeType.setListID(PAYMENT_MEANS_LIST_ID);
        paymentMeansCodeType.setValue(PAYMENT_MEANS_CODE);
        return paymentMeansCodeType;
    }

    private static CustomerPartyType getCustomerParty(Invoice excelInvoice) {
        CustomerPartyType customerPartyType = new CustomerPartyType();
        customerPartyType.setParty(getBuyerParty(excelInvoice));
        return customerPartyType;
    }

    private static PartyType getBuyerParty(Invoice excelInvoice) {
        final BuyerInfo customer = Optional.ofNullable(excelInvoice)
                .map(Invoice::buyerInfo)
                .orElse(BuyerInfo.builder().build());

        PartyType party = new PartyType();
        party.setPartyIdentification(List.of(getCustomerIdentificationType(customer)));
        party.setPartyName(List.of(getPartyNameType(customer)));
        party.setLanguage(getLanguageType());
        party.setPostalAddress(getCustomerAddressType(customer));
        party.setPartyTaxScheme(List.of(getCustomerPartyTaxSchemeType(customer)));
        party.setPartyLegalEntity(List.of(getCustomerLegalEntity(customer)));
        return party;
    }

    private static PartyLegalEntityType getCustomerLegalEntity(BuyerInfo customer) {
        PartyLegalEntityType legalEntityType = new PartyLegalEntityType();
        legalEntityType.setCompanyID(getCustomerCompanyIDType(customer));
        return legalEntityType;
    }

    private static CompanyIDType getCustomerCompanyIDType(BuyerInfo customer) {
        final String customerId = Optional.ofNullable(customer)
                .map(BuyerInfo::clientId)
                .orElse("");

        CompanyIDType companyIDType = new CompanyIDType();
        companyIDType.setSchemeID(LEGAL_ENTITY_COMPANY_SCHEME_ID);
        companyIDType.setValue(customerId);
        return companyIDType;
    }

    private static PartyTaxSchemeType getCustomerPartyTaxSchemeType(BuyerInfo customer) {
        final String customerId = Optional.ofNullable(customer)
                .map(BuyerInfo::taxPayerId)
                .orElse("");

        PartyTaxSchemeType partyTaxSchemeType = new PartyTaxSchemeType();
        partyTaxSchemeType.setCompanyID(getCustomerCompanyIDType(customerId));
        partyTaxSchemeType.setTaxScheme(getTaxSchemeType());
        return partyTaxSchemeType;
    }

    private static CompanyIDType getCustomerCompanyIDType(String customerId) {
        CompanyIDType companyIDType = new CompanyIDType();
        companyIDType.setSchemeID(PARTY_IDENTIFICATION_SCHEME_ID);
        companyIDType.setValue(customerId);
        return companyIDType;
    }

    private static TaxSchemeType getTaxSchemeType() {
        TaxSchemeType taxSchemeType = new TaxSchemeType();
        taxSchemeType.setID(VAT);
        return taxSchemeType;
    }

    private static AddressType getCustomerAddressType(BuyerInfo customer) {
        final String streetName = Optional.ofNullable(customer)
                .map(BuyerInfo::address)
                .map(Address::streetName)
                .orElse("");
        final String cityName = Optional.ofNullable(customer)
                .map(BuyerInfo::address)
                .map(Address::city)
                .orElse("");
        final String postalCode = Optional.ofNullable(customer)
                .map(BuyerInfo::address)
                .map(Address::postalCode)
                .orElse("");

        AddressType address = new AddressType();
        address.setStreetName(streetName);
        address.setCityName(cityName);
        address.setPostalZone(postalCode);
        address.setCountry(getCountryType());
        return address;
    }

    private static PartyNameType getPartyNameType(BuyerInfo customer) {
        final String customerName = Optional.ofNullable(customer)
                .map(BuyerInfo::clientName)
                .orElse("");

        PartyNameType partyNameType = new PartyNameType();
        partyNameType.setName(customerName);
        return partyNameType;
    }

    private static PartyIdentificationType getCustomerIdentificationType(BuyerInfo customer) {
        final String clientId = Optional.ofNullable(customer)
                .map(BuyerInfo::taxPayerId)
                .orElse("");

        PartyIdentificationType partyIdentificationType = new PartyIdentificationType();
        partyIdentificationType.setID(getIdType(clientId));
        return partyIdentificationType;
    }

    private static IDType getIdType(String clientId) {
        IDType idType = new IDType();
        idType.setSchemeID(PARTY_IDENTIFICATION_SCHEME_ID);
        idType.setValue(clientId);
        return idType;
    }

    private static SupplierPartyType getSupplierParty(Invoice excelInvoice) {
        SupplierPartyType accountingSupplierParty = new SupplierPartyType();
        accountingSupplierParty.setParty(getSellerParty(excelInvoice));
        return accountingSupplierParty;
    }

    private static PartyType getSellerParty(Invoice excelInvoice) {
        final SellerInfo supplier = Optional.ofNullable(excelInvoice)
                .map(Invoice::sellerInfo)
                .orElse(SellerInfo.builder().build());

        PartyType party = new PartyType();
        party.setWebsiteURI(WEBSITE_URI);
        party.setPartyIdentification(List.of(getSellerPartyIdentification(supplier)));
        party.setPartyName(List.of(getSupplierName(supplier)));
        party.setLanguage(getLanguageType());
        party.setPostalAddress(getSupplierAddressType(supplier));
        party.setPartyTaxScheme(List.of(getPartyTaxSchemeType(supplier)));
        party.setPartyLegalEntity(List.of(getLegalEntity(supplier)));
        party.setContact(getContactType(excelInvoice));
        return party;
    }

    private static ContactType getContactType(Invoice excelInvoice) {
        final String contact = Optional.of(excelInvoice)
                .map(Invoice::responsible)
                .orElse("");

        ContactType contactType = new ContactType();
        contactType.setTelephone(extractTelephone(contact));
        contactType.setID(removeTelephone(contact));
        return contactType;
    }

    private static PartyLegalEntityType getLegalEntity(SellerInfo supplier) {
        final String clientId = Optional.ofNullable(supplier)
                .map(SellerInfo::clientId)
                .orElse("");

        PartyLegalEntityType partyLegalEntityType = new PartyLegalEntityType();
        partyLegalEntityType.setCompanyID(getCompanyIdType(clientId));
        return partyLegalEntityType;
    }

    private static CompanyIDType getCompanyIdType(String clientId) {
        CompanyIDType companyIdType = new CompanyIDType();
        companyIdType.setSchemeID(LEGAL_ENTITY_COMPANY_SCHEME_ID);
        companyIdType.setValue(clientId);
        return companyIdType;
    }

    private static PartyTaxSchemeType getPartyTaxSchemeType(SellerInfo supplier) {
        PartyTaxSchemeType partyTaxSchemeType = new PartyTaxSchemeType();
        partyTaxSchemeType.setCompanyID(getSupplierCompanyIDType(supplier));
        partyTaxSchemeType.setTaxScheme(getTaxSchemeType());
        return partyTaxSchemeType;
    }

    private static CompanyIDType getSupplierCompanyIDType(SellerInfo supplier) {
        final String taxPayerId = Optional.ofNullable(supplier)
                .map(SellerInfo::taxPayerId)
                .orElse("");

        CompanyIDType companyIdType = new CompanyIDType();
        companyIdType.setSchemeID(PARTY_IDENTIFICATION_SCHEME_ID);
        companyIdType.setValue(taxPayerId);
        return companyIdType;
    }

    private static AddressType getSupplierAddressType(SellerInfo supplier) {
        final Address supplierAddress = Optional.ofNullable(supplier)
                .map(SellerInfo::address)
                .orElse(Address.builder().build());
        final String streetName = Optional.ofNullable(supplierAddress)
                .map(Address::streetName)
                .orElse("");
        final String city = Optional.ofNullable(supplierAddress)
                .map(Address::city)
                .orElse("");
        final String postalCode = Optional.ofNullable(supplierAddress)
                .map(Address::postalCode)
                .orElse("");

        AddressType address = new AddressType();
        address.setStreetName(streetName);
        address.setCityName(city);
        address.setPostalZone(postalCode);
        address.setCountry(getCountryType());
        return address;
    }

    private static CountryType getCountryType() {
        CountryType countryType = new CountryType();
        IdentificationCodeType identificationCodeType = new IdentificationCodeType();
        identificationCodeType.setListID(SUPPLIER_ADDRESS_LIST_ID);
        identificationCodeType.setValue(SUPPLIER_ADDRESS_VALUE);
        countryType.setIdentificationCode(identificationCodeType);
        return countryType;
    }

    private static LanguageType getLanguageType() {
        LanguageType language = new LanguageType();
        language.setID(LANGUAGE_ID);
        language.setName(LANGUAGE_NAME);
        language.setLocaleCode(LANGUAGE_LOCALE_CODE);
        return language;
    }

    private static PartyNameType getSupplierName(SellerInfo supplier) {
        final String name = Optional.ofNullable(supplier)
                .map(SellerInfo::clientName)
                .orElse("");

        PartyNameType partyNameType = new PartyNameType();
        partyNameType.setName(name);
        return partyNameType;
    }

    private static PartyIdentificationType getSellerPartyIdentification(SellerInfo supplier) {
        final String partyId = Optional.ofNullable(supplier)
                .map(SellerInfo::taxPayerId)
                .orElse("");

        PartyIdentificationType partyIdentificationType = new PartyIdentificationType();
        partyIdentificationType.setID(getType(partyId));
        return partyIdentificationType;
    }

    private static IDType getType(String partyId) {
        IDType idType = new IDType();
        idType.setSchemeID(PARTY_IDENTIFICATION_SCHEME_ID);
        idType.setValue(partyId);
        return idType;
    }

    private static ProjectReferenceType getProjectReference() {
        ProjectReferenceType projectReferenceType = new ProjectReferenceType();
        projectReferenceType.setID("");
        return projectReferenceType;
    }

    private static DocumentReferenceType getDocumentReference(String orderNumber) {
        DocumentReferenceType documentReferenceType = new DocumentReferenceType();
        IDType idType = new IDType();
        idType.setValue(orderNumber);
        documentReferenceType.setID(idType);
        return documentReferenceType;
    }

    private static OrderReferenceType getOrderReference(String orderNumber) {
        OrderReferenceType orderReferenceType = new OrderReferenceType();
        orderReferenceType.setID(orderNumber);
        return orderReferenceType;
    }

    private static IssueDateType getIssueDateType(Invoice excelInvoice) {
        final LocalDate issueDate = excelInvoice.created();;
        return new IssueDateType(issueDate);
    }

    private static List<InvoiceLineType> getInvoiceLineTypes(Invoice excelInvoice) {
        final List<Item> excelItems = Optional.ofNullable(excelInvoice)
                .map(Invoice::items)
                .orElse(List.of());

        List<InvoiceLineType> invoiceLines = new ArrayList<>();
        IntStream.range(0, excelItems.size())
                .forEach(i -> invoiceLines.add(getInvoiceLineType(i + 1, excelItems.get(i))));
        return invoiceLines;
    }

    private static InvoiceLineType getInvoiceLineType(int itemId, Item item) {
        final String quantity = Optional.of(item)
                .map(Item::quantity)
                .orElse("0");
        final String currencyId = Optional.of(item)
                .map(Item::sum)
                .map(Price::currency)
                .orElse(DEFAULT_CURRENCY_ID);
        final String amount = Optional.of(item)
                .map(Item::sum)
                .map(Price::value)
                .orElse("0");

        InvoiceLineType invoiceLine = new InvoiceLineType();
        invoiceLine.setID(String.valueOf(itemId));
        invoiceLine.setInvoicedQuantity(getInvoicedQuantity(quantity));
        invoiceLine.setLineExtensionAmount(getLineExtensionAmount(currencyId, amount));
        invoiceLine.setTaxTotal(List.of(getItemTaxTotalType(currencyId, amount)));
        invoiceLine.setItem(getItemType(item));
        invoiceLine.setPrice(getPriceType(item));
        return invoiceLine;
    }

    private static PriceType getPriceType(Item item) {
        PriceType price = new PriceType();
        price.setPriceAmount(getPriceAmountType(item));
        return price;
    }

    private static PriceAmountType getPriceAmountType(Item item) {
        final String amount = Optional.of(item)
                .map(Item::price)
                .map(Price::value)
                .orElse("0");
        final String currencyId = Optional.of(item)
                .map(Item::price)
                .map(Price::currency)
                .orElse(DEFAULT_CURRENCY_ID);

        PriceAmountType priceAmountType = new PriceAmountType();
        priceAmountType.setValue(convertFromString(amount));
        priceAmountType.setCurrencyID(currencyId);
        return priceAmountType;
    }

    private static ItemType getItemType(Item item) {
        ItemType itemType = new ItemType();
        itemType.setDescription(List.of(getDescription(item)));
        itemType.setClassifiedTaxCategory(List.of(getTaxCategory(false)));
        return itemType;
    }

    private static DescriptionType getDescription(Item item) {
        DescriptionType descriptionType = new DescriptionType();
        descriptionType.setValue(Optional.of(item.service()).orElse(""));
        return descriptionType;
    }

    private static TaxTotalType getItemTaxTotalType(String currencyId, String amount) {
        TaxTotalType taxTotal = new TaxTotalType();
        taxTotal.setTaxAmount(getTaxAmount(currencyId, amount));
        taxTotal.setTaxSubtotal(List.of(getItemTaxSubtotal(currencyId, amount)));
        return taxTotal;
    }

    private static TaxSubtotalType getItemTaxSubtotal(String currencyId, String amount) {
        TaxSubtotalType taxSubtotal = new TaxSubtotalType();
        taxSubtotal.setTaxableAmount(getItemTaxableAmount(currencyId, amount));
        taxSubtotal.setTaxAmount(getTaxAmount(currencyId, amount));
        taxSubtotal.setPercent(DEFAULT_TAX_PERCENT);
        taxSubtotal.setTaxCategory(getTaxCategory(true));
        return taxSubtotal;
    }

    private static TaxableAmountType getItemTaxableAmount(String currencyId, String amount) {
        TaxableAmountType taxableAmountType = new TaxableAmountType();
        taxableAmountType.setCurrencyID(currencyId);
        taxableAmountType.setValue(convertFromString(amount));
        return taxableAmountType;
    }

    private static TaxAmountType getTaxAmount(String currencyId, String amount) {
        TaxAmountType taxAmountType = new TaxAmountType();
        taxAmountType.setCurrencyID(currencyId);
        taxAmountType.setValue(calculateTaxAmount(amount));
        return taxAmountType;
    }

    private static BigDecimal calculateTaxAmount(String amount) {
        return convertFromString(amount).multiply(BigDecimal.valueOf(0.21)).setScale(2, RoundingMode.HALF_EVEN);
    }

    private static BigDecimal convertFromString(String amount) {
        return Optional.of(amount)
                .map(Double::parseDouble)
                .map(BigDecimal::valueOf)
                .orElse(BigDecimal.valueOf(0));
    }

    private static InvoicedQuantityType getInvoicedQuantity(String quantity) {
        InvoicedQuantityType invoicedQuantity = new InvoicedQuantityType();
        invoicedQuantity.setUnitCodeListID(INVOICED_QUANTITY_LIST_ID);
        invoicedQuantity.setUnitCode(INVOICED_QUANTITY_UNIT_CODE);
        invoicedQuantity.setValue(BigDecimal.valueOf(Double.parseDouble(quantity)));
        return invoicedQuantity;
    }

    private static String generateUUID() {
        return Optional.of(UUID.randomUUID())
                .map(UUID::toString)
                .orElse("");
    }

    private static InvoiceTypeCodeType getInvoiceTypeCode() {
        InvoiceTypeCodeType invoiceTypeCode = new InvoiceTypeCodeType();
        invoiceTypeCode.setListID(INVOICE_TYPE_CODE_ID);
        invoiceTypeCode.setValue(INVOICE_TYPE_CODE);
        return invoiceTypeCode;
    }
}