package util;

import gui.DMSApplication;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestUtil {

    private static final Path TEST_SERVER_PATH = Paths.get("TestingFiles/Server/");
    private static final String APPLICATION_FOLDER_NAME = "RG DMS";
    private static final String REPLACEMENT_FOLDER_NAME = APPLICATION_FOLDER_NAME + " Original";

    public static void resetTestFiles() throws IOException { // Todo actually make test files that are independent of actual files
        Path oldFolder = TEST_SERVER_PATH.resolve(APPLICATION_FOLDER_NAME);
        Path replacementFolder = TEST_SERVER_PATH.resolve(REPLACEMENT_FOLDER_NAME);

        if(Files.exists(oldFolder) && oldFolder.toString().contains(APPLICATION_FOLDER_NAME))
            FileUtils.deleteDirectory(oldFolder.toFile());

        FileUtils.copyDirectory(replacementFolder.toFile(), oldFolder.toFile());
    }

    public static final Path getTestDocuments(){
        return TEST_SERVER_PATH.resolve(APPLICATION_FOLDER_NAME);
    }


}
