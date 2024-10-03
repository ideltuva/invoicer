package com.api.invoicer.component;

import com.api.invoicer.facade.FileHandlerFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import static com.api.invoicer.constant.FileConstant.FILE_PATH;

@Slf4j
@Component
public class CommandLineAppStartupRunner implements CommandLineRunner {

    final FileHandlerFacade fileHandlerFacade;

    public CommandLineAppStartupRunner(FileHandlerFacade fileHandlerFacade) {
        this.fileHandlerFacade = fileHandlerFacade;
    }

    @Override
    public void run(String... args) {
        fileHandlerFacade.handleFiles(FILE_PATH);
    }
}
