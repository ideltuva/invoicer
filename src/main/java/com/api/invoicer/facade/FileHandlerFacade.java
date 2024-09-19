package com.api.invoicer.facade;

import com.api.invoicer.service.ConvertService;
import com.api.invoicer.service.ExcelService;
import com.api.invoicer.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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

        });
    }
}
