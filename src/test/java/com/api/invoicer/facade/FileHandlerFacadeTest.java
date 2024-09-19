package com.api.invoicer.facade;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FileHandlerFacadeTest {

    @Autowired
    FileHandlerFacade fileHandlerFacade;

    @Test
    public void handleFile() {
        fileHandlerFacade.handleFiles();
    }
}
