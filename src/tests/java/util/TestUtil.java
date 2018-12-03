package util;

import app.ApplicationMode;
import directory.DirectoryCloner;
import directory.Settings;
import gui.DMSApplication;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestUtil {

    private final static String REPLACEMENT_FOLDER_NAME = "Server Original";

    public static void resetTestFiles() throws IOException { // Todo actually make test files that are independent of actual files
        /*Settings.loadSettings(ApplicationMode.ADMIN);
        Path replacementFolder = Paths.get(Settings.getServerPath()).getParent().getParent().resolve(REPLACEMENT_FOLDER_NAME);
        Path alteredFolder = Paths.get(Settings.getServerPath()).getParent();

        DirectoryCloner.deleteFolder(alteredFolder.toFile());
        DirectoryCloner.copyFolder(replacementFolder, alteredFolder);
        System.out.println("is it run?");*/
    }


}
