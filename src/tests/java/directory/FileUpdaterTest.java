package directory;

import app.ApplicationMode;
import directory.files.AbstractFile;
import directory.files.Folder;
import directory.plant.PlantManager;
import gui.DMSApplication;
import gui.GUITest;
import json.AppFilesManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.TestUtil;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class FileUpdaterTest extends GUITest {

    Path pathToFolder = Paths.get("02_VINTERTØRRET FISK");
    Folder folder;

    @BeforeEach
    void setup() throws IOException {
        TestUtil.resetTestFiles();
        AppFilesManager.createLocalDirectories();
        AppFilesManager.createServerDirectories();
    }



    @Test
    void start() throws IOException, InterruptedException {
        SettingsManager.loadSettings(ApplicationMode.ADMIN);
        FileManager.resetInstance();
        FileManager.getInstance();
        PlantManager.resetInstance();
        PlantManager.getInstance();
        DirectoryCloner.publishFiles();
        FileUpdater fu = new FileUpdater(DMSApplication.getDMSApplication());
        fu.start();

        assertEquals(FileManager.getInstance().getMainFiles(), AppFilesManager.loadLocalFileList());

        //publishes a renamed folder
        folder = (Folder) FileManager.getInstance().findFile(pathToFolder, FileManager.getInstance().getMainFiles()).get();
        FileManager.getInstance().renameFile(folder, "new name");
        DirectoryCloner.publishFiles();
        Thread.sleep(10000);

        //asserts that folder is in localFileList
        ArrayList<AbstractFile> al = AppFilesManager.loadLocalFileList();
        assertTrue(al.contains(folder));

        fu.setRunning(false);
    }
}