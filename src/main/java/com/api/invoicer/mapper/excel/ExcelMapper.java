package com.api.invoicer.mapper.excel;

import com.api.invoicer.model.excelDto.*;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.api.invoicer.constant.ExcelConstant.CONFIG_MAP;
import static com.api.invoicer.constant.ExcelConstant.EMPTY_STRING;
import static com.api.invoicer.model.excelDto.ExcelEnum.*;
import static com.api.invoicer.util.AddressUtil.getPostalCode;
import static com.api.invoicer.util.DateUtil.getLocaleDate;
import static com.api.invoicer.util.ExcelUtil.*;

@Slf4j
public class ExcelMapper {

    private ExcelMapper() {
    }

    private static SellerInfo mapToSellerInfo(final Map<Integer, List<String>> excelMap) {
        return SellerInfo.builder()
                .clientName(excelMap.get(CONFIG_MAP.get(SELLER_NAME)).get(0))
                .clientId(excelMap.get(CONFIG_MAP.get(SELLER_CLIENT_ID)).get(3))
                .taxPayerId(excelMap.get(CONFIG_MAP.get(SELLER_TAX_PAYER_ID)).get(4))
                .address(mapToAddress(excelMap.get(CONFIG_MAP.get(SELLER_ADDRESS)).get(2)))
                .build();
    }

    private static BuyerInfo mapToBuyerInfo(final Map<Integer, List<String>> excelMap) {
        return BuyerInfo.builder()
                .clientName(excelMap.get(CONFIG_MAP.get(BUYER_NAME)).get(12))
                .clientId(excelMap.get(CONFIG_MAP.get(BUYER_CLIENT_ID)).get(16))
                .address(mapToAddress(excelMap.get(CONFIG_MAP.get(BUYER_ADDRESS)).get(12)))
                .taxPayerId(excelMap.get(CONFIG_MAP.get(BUYER_TAX_PAYER_ID)).get(17))
                .build();
    }

    private static Address mapToAddress(String address) {
        if(StringUtils.isEmpty(address)) return Address.builder().build();

        final String[] addressList = address.split(",");
        final String street = addressList[0];
        final String postalCode = getPostalCode(address);
        final String city = getCity(postalCode, addressList);
        return Address.builder()
                .fullAddress(address)
                .streetName(street)
                .city(city)
                .postalCode(postalCode)
                .build();
    }

    private static String getCity(String postalCode, String[] addressList) {
        final String city = addressList.length > 1 ? addressList[addressList.length - 1] : addressList[0];
        return city
                .replaceAll(postalCode, "")
                .trim();


    }

    private static Item mapToItem(final List<String> itemDetails) {
        return Item.builder()
                .service(itemDetails.get(1))
                .description(itemDetails.get(12))
                .quantity(itemDetails.get(15))
                .price(Price.builder().value(itemDetails.get(16)).build())
                .sum(Price.builder().value(itemDetails.get(19)).build())
                .build();
    }

    private static Total mapToTotal(final Integer totalPosition, final Map<Integer, List<String>> excelMap) {
        return totalPosition == 0 ? Total.builder().build() :
                Total.builder()
                    .price(Price.builder()
                            .value(excelMap.get(totalPosition).get(19))
                            .build())
                    .standardTax(excelMap.get(totalPosition + 2).get(19))
                    .sum(Price.builder()
                            .value(excelMap.get(totalPosition + 4).get(19))
                            .build())
                    .build();
    }

    private static String mapToResponsible(final Integer position, final Map<Integer, List<String>> excelMap) {
        return position == 0 ? EMPTY_STRING : excelMap.get(position - 1).get(0);
    }

    private static AdditionalInfo mapToAdditionalInfo(final Integer position, final Map<Integer, List<String>> excelMap) {
        Integer index = position + 2;
        return index == 2 ? AdditionalInfo.builder().build() :
                AdditionalInfo.builder()
                        .bankName(excelMap.get(index).get(0))
                        .accountNumber(excelMap.get(index).get(5))
                        .build();
    }

    private static String mapToSerialNumber(final Map<Integer, List<String>> excelMap) {
        return excelMap.get(CONFIG_MAP.get(SERIAL)).get(10);
    }

    private static String mapToOrderNumber(final Map<Integer, List<String>> excelMap) {
        return excelMap.get(CONFIG_MAP.get(ORDER)).get(3);
    }

    private static LocalDate mapToCreatedDate(final Map<Integer, List<String>> excelMap) {
        String created = excelMap.get(CONFIG_MAP.get(CREATED)).get(0);
        Locale locale = new Locale.Builder()
                .setLanguage("lt")
                .setRegion("LT")
                .build();
        DateFormat dateFormat = new SimpleDateFormat("y 'm'. MMMM d 'd'.", locale);
        Date date = new Date();
        try { date = dateFormat.parse(created); }
        catch(Exception e) {
            log.error("Unable to parse the date: ", e);
        }
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        return getLocaleDate(dateFormatter.format(date));
    }

    private static String mapToRemarks(final Map<Integer, List<String>> excelMap) {
        return excelMap.get(getRemarksPosition(excelMap)).get(5);
    }

    private static LocalDate mapToDueDate(final Map<Integer, List<String>> excelMap) {
        return getLocaleDate(excelMap.get(getDueDatePosition(excelMap)).get(5));
    }

    public static Invoice mapToExcelInvoice(Map<Integer, List<String>> excelMap) {
        return Invoice.builder()
                .serialNumber(mapToSerialNumber(excelMap))
                .orderNumber(mapToOrderNumber(excelMap))
                .created(mapToCreatedDate(excelMap))
                .sellerInfo(mapToSellerInfo(excelMap))
                .buyerInfo(mapToBuyerInfo(excelMap))
                .items(getItems(getItemMap(excelMap)))
                .dueDate(mapToDueDate(excelMap))
                .total(mapToTotal(getTotalPosition(excelMap), excelMap))
                .additionalInfo(mapToAdditionalInfo(getAdditionalInfoPosition(excelMap), excelMap))
                .remarks(mapToRemarks(excelMap))
                .responsible(mapToResponsible(getResponsiblePosition(excelMap), excelMap))
                .build();
    }
    private static List<Item> getItems(Map<Integer, List<String>> itemMap) {
        return itemMap.values().stream().map(ExcelMapper::mapToItem).collect(Collectors.toList());
    }
}