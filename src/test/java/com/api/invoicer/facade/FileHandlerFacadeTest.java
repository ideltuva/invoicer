package com.api.invoicer.facade;

import com.api.invoicer.component.CommandLineAppStartupRunner;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;

import static com.api.invoicer.constant.FileConstant.DIRECTORY_NAME;
import static com.api.invoicer.constant.FileConstant.FILE_PATH;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FileHandlerFacadeTest {
    final String PATH = "src/test/resources/static";
    final String TEST_FILE = "Birzu_M.xml";

    @MockBean
    CommandLineAppStartupRunner CommandLineAppStartupRunner;
    @Autowired
    FileHandlerFacade fileHandlerFacade;

    @Test
    public void handleFilesWhenPathGiven() {
        deleteFolderIfFound(PATH);
        fileHandlerFacade.handleFiles(PATH);

        File file = new File(String.format("%s/%s/%s", PATH, DIRECTORY_NAME, TEST_FILE));
        Assert.assertTrue(FileUtils.isFileNewer(file, 2000L));

        deleteFolderIfFound(PATH);
    }

    @Test
    public void handleNoPathGivenFiles() {
        deleteFolderIfFound(FILE_PATH);
        fileHandlerFacade.handleFiles(FILE_PATH);

        File file = new File(String.format("%s/%s/%s", FILE_PATH, DIRECTORY_NAME, TEST_FILE));
        Assert.assertTrue(FileUtils.isFileNewer(file, 2000L));

        deleteFolderIfFound(FILE_PATH);
    }

    private void deleteFolderIfFound(String path) {
        File file = new File(String.format("%s/%s", path, DIRECTORY_NAME));
        try {
            FileUtils.cleanDirectory(file);
            FileUtils.forceDelete(file);
        }
        catch (Exception ignored) { }
    }
}
