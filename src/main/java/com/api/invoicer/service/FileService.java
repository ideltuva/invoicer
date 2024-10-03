package com.api.invoicer.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.api.invoicer.constant.FileConstant.*;

@Slf4j
@Service
public class FileService {

    public List<String> getFileNames(String filePath) {
        final File currentDirectory = new File(filePath);
        File[] files = currentDirectory.listFiles((dir, name) -> name.endsWith(EXCEL_SUFFIX_XLSX) || name.endsWith(EXCEL_SUFFIX_XLSM));
        if (ArrayUtils.isNotEmpty(files)) return Arrays.stream(files).map(File::getName).collect(Collectors.toList());
        log.info("No files found in a current directory");
        return List.of();
    }
}
