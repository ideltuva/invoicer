package com.api.invoicer.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.DateUtil;

import org.springframework.util.ResourceUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.api.invoicer.constant.ExcelConstant.*;
import static com.api.invoicer.constant.ExcelConstant.RESPONSIBLE;
import static com.api.invoicer.model.excel.ExcelEnum.ITEMS;

@Slf4j
@UtilityClass
public final class ExcelUtil {

    private final static String FILE = "Uzpildytos saskaitos pavyzdys.xlsx";

    public static Map<Integer, List<String>> getDataMap() throws IOException {
        Sheet sheet = readFromFirstSheet(getExcelWorkbook());
        Map<Integer, List<String>> data = new HashMap<>();
        int i = 0;
        for (Row row : sheet) {
            data.put(i, new ArrayList<>());
            for (Cell cell : row) {
                switch (cell.getCellType()) {
                    case STRING: data.get(i).add(cell.getRichStringCellValue().getString()); break;
                    case NUMERIC: data.get(i).add(getFromNumericValue(cell)); break;
                    case BOOLEAN: data.get(i).add(cell.getBooleanCellValue() + ""); break;
                    case FORMULA: data.get(i).add(getValueFromFormula(cell)); break;
                    default: data.get(i).add(" ");
                }
            }
            i++;
        }
        return data;
    }

    private static Workbook getExcelWorkbook() throws IOException {
        FileInputStream file = new FileInputStream(ResourceUtils.getFile("classpath:" + FILE));
        return new XSSFWorkbook(file);
    }

    private static Sheet readFromFirstSheet(final Workbook workbook) {
        return workbook.getSheetAt(0);
    }

    private static String getFromNumericValue(Cell cell) {
        if(DateUtil.isCellDateFormatted(cell)) {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-d");
            return dateFormatter.format(cell.getDateCellValue());
        }
        DataFormatter dataFormatter = new DataFormatter();
        return dataFormatter.formatCellValue(cell);
    }

    private static String getValueFromFormula(Cell cell) {
        DataFormatter dataFormatter = new DataFormatter();
        dataFormatter.setUseCachedValuesForFormulaCells(true);
        return dataFormatter.formatCellValue(cell);
    }


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
