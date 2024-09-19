package com.api.invoicer.service;

import com.helger.ubl21.UBL21Marshaller;
import lombok.extern.slf4j.Slf4j;
import oasis.names.specification.ubl.schema.xsd.invoice_21.InvoiceType;
import org.springframework.stereotype.Service;

import java.io.File;

import static com.api.invoicer.constant.FileConstant.FOLDER_NAME;

@Slf4j
@Service
public class ConvertService {

    public void generateObl21(InvoiceType invoice, String fileName) {
        log.info("Generating an xml file for the excel: {}", fileName);
        UBL21Marshaller.invoice()
                .write(invoice, new File(String.format("%s/%s.xml", FOLDER_NAME, fileName)));
    }
}
