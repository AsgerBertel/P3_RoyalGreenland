package app;

import directory.DirectoryCloner;
import directory.Settings;
import gui.DMSApplication;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class RestoreTestConditions {

    private final static String REPLACEMENT_FOLDER_NAME = "Server Original";

    public static void main(String[] args) throws IOException {
        Settings.loadSettings(ApplicationMode.ADMIN);
        Path replacementFolder = Paths.get(Settings.getServerPath()).getParent().getParent().resolve(REPLACEMENT_FOLDER_NAME);
        Path alteredFolder = Paths.get(Settings.getServerPath()).getParent();

        DirectoryCloner.deleteFolder(alteredFolder.toFile());
        DirectoryCloner.copyFolder(replacementFolder, alteredFolder);
    }


}
