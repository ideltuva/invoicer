package com.api.invoicer.constant;

import com.api.invoicer.model.excelDto.ExcelEnum;

import java.util.Map;

import static com.api.invoicer.model.excelDto.ExcelEnum.*;

public class ExcelConstant {
    public final static String ADDITIONAL_INFO = "Papildoma informacija";
    public final static String DUE_DATE = "Apmokėti iki";
    public final static String TOTAL = "Iš viso";
    public final static String RESPONSIBLE = "sąskaitą išrašė";
    public final static String REMARKS = "Pastabos:";
    public final static String EMPTY_STRING = " ";

    public static final Map<ExcelEnum, Integer> CONFIG_MAP = Map.ofEntries(
            Map.entry(SERIAL, 3),
            Map.entry(ORDER, 16),
            Map.entry(CREATED, 4),
            Map.entry(SELLER_NAME, 8),
            Map.entry(BUYER_NAME, 8),
            Map.entry(SELLER_CLIENT_ID, 10),
            Map.entry(BUYER_ADDRESS, 10),
            Map.entry(SELLER_TAX_PAYER_ID, 11),
            Map.entry(BUYER_TAX_PAYER_ID, 11),
            Map.entry(SELLER_ADDRESS, 12),
            Map.entry(BUYER_CLIENT_ID, 12),
            Map.entry(ITEMS, 17)
    );
}
