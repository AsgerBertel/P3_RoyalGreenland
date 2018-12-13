package directory;

import app.ApplicationMode;
import app.DMSAdmin;
import directory.files.AbstractFile;
import directory.files.Folder;
import directory.plant.PlantManager;
import gui.DMSApplication;
import gui.GUITest;
import json.AppFilesManager;
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

    @Test
    void start() throws IOException, InterruptedException {

        PlantManager.getInstance();
        DirectoryCloner.publishFiles();
        FileUpdater fu = new FileUpdater(DMSApplication.getDMSApplication());
        fu.start();

        //asserts that all names are the same, cant equal objects because local files
        //have different modified variables (They were published)
        for (int i = 0; i < FileManager.getInstance().getMainFiles().size(); i++){
            assertEquals(FileManager.getInstance().getMainFiles().get(i).getName(),
                    AppFilesManager.loadLocalFileList().get(i).getName());
        }

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

    @Test
    void setRunning() {
    }

    @Test
    void run() {
    }
}