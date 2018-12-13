package directory;

import app.ApplicationMode;
import app.DMSAdmin;
import directory.files.AbstractFile;
import directory.files.Folder;
import directory.plant.PlantManager;
import gui.DMSApplication;
import json.AppFilesManager;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class FileUpdaterTest extends FileTester{

    Path pathToFolder = Paths.get("02_VINTERTØRRET FISK");
    Folder folder;

    @Override
    protected void setSettings(){
        SettingsManager.loadSettings(ApplicationMode.ADMIN);
        folder = (Folder) findInMainFiles(pathToFolder);
    }

    @Test
    void start() throws IOException, InterruptedException {
        PlantManager.getInstance();
        DirectoryCloner.publishFiles();
        FileUpdater fu = new FileUpdater(DMSApplication.getDMSApplication());
        fu.start();

        assertEquals(FileManager.getInstance().getMainFiles(), AppFilesManager.loadLocalFileList());

        //publishes a renamed folder
        FileManager.getInstance().renameFile(folder, "new folder");
        DirectoryCloner.publishFiles();
        Thread.sleep(10000);

        //asserts that folder is in localFileList
        ArrayList<AbstractFile> al = AppFilesManager.loadLocalFileList();
        assertTrue(al.contains(folder));

        fu.setRunning(false);
    }

    @Test
    void setRunning() {
    }

    @Test
    void run() {
    }
}