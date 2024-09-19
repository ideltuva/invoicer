package com.api.invoicer.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FileService {
    private final String EXCEL_SUFFIX = ".xlsx";
    private final File currentDirectory = new File(".");
    public List<String> getFileNames() {
        File[] files = currentDirectory.listFiles((dir, name) -> name.endsWith(EXCEL_SUFFIX));
        if (ArrayUtils.isNotEmpty(files)) return Arrays.stream(files).map(File::getName).collect(Collectors.toList());
        log.info("No files found in a current directory");
        return List.of();
    }
}
