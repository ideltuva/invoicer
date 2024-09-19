package com.api.invoicer.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.IntStream;

@Slf4j
@Service
public class ExcelService {

    public Map<Integer, List<String>> getDataMap(String fileName) {
        Sheet sheet = readFromFirstSheet(Objects.requireNonNull(getExcelWorkbook(fileName)));
        Map<Integer, List<String>> data = new HashMap<>();
        if (Objects.isNull(sheet)) return data;

        IntStream.range(0, sheet.getLastRowNum())
                .forEach(i -> {
                    data.put(i, new ArrayList<>());
                    sheet.getRow(i).forEach(cell -> {
                        switch (cell.getCellType()) {
                            case STRING: data.get(i).add(cell.getRichStringCellValue().getString()); break;
                            case NUMERIC: data.get(i).add(getFromNumericValue(cell)); break;
                            case BOOLEAN: data.get(i).add(cell.getBooleanCellValue() + ""); break;
                            case FORMULA: data.get(i).add(getValueFromFormula(cell)); break;
                            default: data.get(i).add(" ");
                        }
                    });
                });
        return data;
    }

    private static Sheet readFromFirstSheet(final Workbook workbook) {
        return workbook.getSheetAt(0);
    }

    private static Workbook getExcelWorkbook(String fileName) {
        try {
            FileInputStream file = new FileInputStream(ResourceUtils.getFile("classpath:" + fileName));
            return new XSSFWorkbook(file);
        } catch(IOException e) {
            log.error("Error processing file: {}, {}", fileName, e.getMessage());
        }
        return null;
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
}
