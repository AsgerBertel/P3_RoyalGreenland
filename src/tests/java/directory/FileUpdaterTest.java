package directory;

import app.ApplicationMode;
import app.DMSAdmin;
import directory.files.AbstractFile;
import directory.files.Folder;
import directory.plant.PlantManager;
import gui.DMSApplication;
import gui.GUITest;
import json.AppFilesManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
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

   /* @BeforeEach
    void resetBeforeEachMethod() throws IOException {
        SettingsManager.setServerPath(TestUtil.getTestServerDocuments());
        SettingsManager.setLocalPath(TestUtil.getTestLocalDocuments());

        TestUtil.resetTestFiles();
        FileManager.resetInstance();
        PlantManager.resetInstance();

        AppFilesManager.createServerDirectories();
        AppFilesManager.createLocalDirectories();
        SettingsManager.loadSettings(ApplicationMode.ADMIN);
    }*/

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


        //asserts that all names are the same, cant equal objects because local files
        //have different modified variables (They were published)
        for (int i = 0; i < FileManager.getInstance().getMainFiles().size(); i++){
            assertEquals(FileManager.getInstance().getMainFiles().get(i).getName(),
                    AppFilesManager.loadLocalFileList().get(i).getName());
        }

        //assertEquals(FileManager.getInstance().getMainFiles(), AppFilesManager.loadLocalFileList());

        //publishes a renamed folder

        folder = (Folder) FileManager.getInstance().findFile(pathToFolder, FileManager.getInstance().getMainFiles()).get();
        FileManager.getInstance().renameFile(folder, "new name");
        DirectoryCloner.publishFiles();
        Thread.sleep(10000);

        //asserts that folder is in localFileList
        ArrayList<AbstractFile> al = AppFilesManager.loadLocalFileList();

        System.out.println(((Folder) al.get(0)).folderContents.toString());
        System.out.println(folder.folderContents.toString());
        assertEquals(al.get(0), folder);
        //assertTrue(al.contains(folder));

        fu.setRunning(false);
    }
}