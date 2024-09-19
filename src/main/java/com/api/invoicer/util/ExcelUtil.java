package com.api.invoicer.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.api.invoicer.constant.ExcelConstant.*;
import static com.api.invoicer.model.excelDto.ExcelEnum.ITEMS;

@Slf4j
@UtilityClass
public final class ExcelUtil {
    public static Map<Integer, List<String>> getItemMap(Map<Integer, List<String>> excelMap) {
        Map<Integer, List<String>> itemMap = new HashMap<>();
        int counter = CONFIG_MAP.get(ITEMS);
        while(!EMPTY_STRING.equals(excelMap.get(counter).get(0)) || TOTAL.equals(excelMap.get(counter).get(16))) {
            itemMap.put(counter, excelMap.get(counter));
            counter++;
        }
        return itemMap;
    }

    public static Integer getTotalPosition(Map<Integer, List<String>> excelMap) {
        return excelMap.entrySet()
                .stream()
                .filter(entry -> TOTAL.equals(entry.getValue().get(16)))
                .findFirst()
                .map(Map.Entry::getKey)
                .orElse(0);
    }

    public static Integer getRemarksPosition(Map<Integer, List<String>> excelMap) {
        return excelMap.entrySet()
                .stream()
                .filter(entry -> REMARKS.equals(entry.getValue().get(0)))
                .findFirst()
                .map(Map.Entry::getKey)
                .orElse(0);
    }

    public static Integer getDueDatePosition(Map<Integer, List<String>> excelMap) {
        return excelMap.entrySet()
                .stream()
                .filter(entry -> DUE_DATE.equals(entry.getValue().get(0)))
                .findFirst()
                .map(Map.Entry::getKey)
                .orElse(0);
    }

    public static Integer getAdditionalInfoPosition(Map<Integer, List<String>> excelMap) {
        return excelMap.entrySet()
                .stream()
                .filter(entry -> ADDITIONAL_INFO.equals(entry.getValue().get(0)))
                .findFirst()
                .map(Map.Entry::getKey)
                .orElse(0);
    }

    public static Integer getResponsiblePosition(Map<Integer, List<String>> excelMap) {
        return excelMap.entrySet()
                .stream()
                .filter(entry -> RESPONSIBLE.equals(entry.getValue().get(0)))
                .findFirst()
                .map(Map.Entry::getKey)
                .orElse(0);
    }
}
