package com.api.invoicer.facade;

import com.api.invoicer.model.excelDto.Invoice;
import com.api.invoicer.service.ConvertService;
import com.api.invoicer.service.ExcelService;
import com.api.invoicer.service.FileService;
import lombok.extern.slf4j.Slf4j;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_21.UUIDType;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.api.invoicer.mapper.excel.ExcelMapper.mapToExcelInvoice;
import static com.api.invoicer.mapper.obl.OblMapper.mapToObl;
import static com.api.invoicer.util.StringUtil.trimFileSuffix;

@Slf4j
@Service
public class FileHandlerFacade {

    private final ExcelService excelService;
    private final ConvertService convertService;
    private final FileService fileService;

    public FileHandlerFacade(ExcelService excelService, ConvertService convertService, FileService fileService) {
        this.excelService = excelService;
        this.convertService = convertService;
        this.fileService = fileService;
    }

    public void handleFiles() {
        List<String> fileNames = fileService.getFileNames();
        fileNames.forEach(fileName -> {
            Map<Integer, List<String>> excelDataMap = excelService.getDataMap(fileName);
            Invoice excelInvoice = mapToExcelInvoice(excelDataMap);
            InvoiceType invoiceObl = mapToObl(excelInvoice);
            convertService.generateObl21(invoiceObl, trimFileSuffix(fileName));
            log.info("{}.xml generated: {}", fileName, getUUID(invoiceObl));
        });
    }

    private String getUUID(InvoiceType invoiceType) {
        return Optional.of(invoiceType)
                .map(InvoiceType::getUUID)
                .map(UUIDType::getValue)
                .orElse("");
    }
}
