package com.api.invoicer.service;

import com.api.invoicer.model.excel.Invoice;
import com.helger.ubl21.UBL21Marshaller;
import lombok.extern.slf4j.Slf4j;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

import static com.api.invoicer.mapper.excel.ExcelMapper.mapToInvoice;
import static com.api.invoicer.mapper.obl.OblMapper.mapToObl;
import static com.api.invoicer.util.ExcelUtil.getDataMap;

@Slf4j
@Service
public class ConvertService {
    public void generateObl21() throws IOException {
        Invoice excelInvoice = mapToInvoice(getDataMap());
        InvoiceType invoice = mapToObl(excelInvoice);
        UBL21Marshaller.invoice()
                .write(invoice, new File("target/dummy-invoice.xml"));
    }

    public InvoiceType getObl21(Invoice excel) {
        return mapToObl(excel);
    }
}
