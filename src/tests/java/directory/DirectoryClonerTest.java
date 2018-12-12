package directory;

import app.ApplicationMode;
import com.sun.scenario.Settings;
import directory.files.AbstractFile;
import directory.files.Document;
import directory.files.Folder;
import directory.plant.AccessModifier;
import directory.plant.Plant;
import directory.plant.PlantManager;
import json.AppFilesManager;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class DirectoryClonerTest extends FileTester {

    ArrayList<AbstractFile> al;
    Path docPath = Paths.get("04_MASKINTØRRET FISK/FL 04 GR_02   Flowdiagram for produktion af maskintørret fisk.pdf");
    Document doc;
    Path folderPath = Paths.get("02_VINTERTØRRET FISK");
    Folder folder;
    Path parentFolderPath = Paths.get("04_MASKINTØRRET FISK");
    Folder parentFolder;
    Path pathKalFolder = Paths.get("02_VINTERTØRRET FISK/KAL");
    Folder KALFolder;

    @Override
    protected void setSettings(){
        SettingsManager.loadSettings(ApplicationMode.ADMIN);
        al = FileManager.getInstance().getMainFiles();
        doc = (Document) findInMainFiles(docPath);
        folder = (Folder) findInMainFiles(folderPath);
        parentFolder = (Folder) findInMainFiles(parentFolderPath);
        KALFolder = (Folder) findInMainFiles(pathKalFolder);
    }

    @Test
    void publishFiles() throws Exception {
        //asserting that both are equal when publishFiles() is used
        PlantManager.getInstance();
        DirectoryCloner.publishFiles();
        assertEquals(AppFilesManager.loadPublishedFileList(), FileManager.getInstance().getMainFiles());

        //asserting not equals when FileManager does something without publishing
        FileManager.getInstance().deleteFile(doc);
        assertNotEquals(AppFilesManager.loadPublishedFileList(), FileManager.getInstance().getMainFiles());

        //asserting equal again when files are published
        DirectoryCloner.publishFiles();
        assertEquals(AppFilesManager.loadPublishedFileList(), FileManager.getInstance().getMainFiles());
    }

    @Test
    void updateLocalFiles() throws Exception {
        PlantManager.getInstance().addPlant(new Plant(1234, "cool", new AccessModifier()));
        DirectoryCloner.publishFiles();
        al = AppFilesManager.loadPublishedFileList();

        //assert that they are not equal before update
        assertNotEquals(al, AppFilesManager.loadLocalFileList());

        //assert that local files and published files are equal after update
        DirectoryCloner.updateLocalFiles();
        assertEquals(al, AppFilesManager.loadLocalFileList());
    }

    @Test
    void mergeFolders() {

        //merges folders in filesystem but not in contents of folders.
        DirectoryCloner.mergeFolders(SettingsManager.getServerDocumentsPath().resolve(folder.getOSPath()),
                SettingsManager.getServerDocumentsPath().resolve(parentFolder.getOSPath()), true);

        //assert that document is not moved in folder contents
        assertFalse(parentFolder.getContents().contains(folder.getContents().get(1)));
        assertTrue(folder.getContents().contains(folder.getContents().get(1)));

        String trueString = parentFolder.getOSPath().resolve(folder.getContents().get(1).getName()).toString();
        trueString = SettingsManager.getServerDocumentsPath().toString() + File.separator + trueString;

        //assert that document is copied to folder through the filesystem, but still exists in old folder
        assertTrue(Files.exists(Paths.get(trueString)));
        assertTrue(Files.exists(SettingsManager.getServerDocumentsPath().resolve(folder.getContents().get(1).getOSPath())));
    }

    @Test
    void deleteFolder() {

        //deletes in filesystem but not in our folder system
        DirectoryCloner.deleteFolder(SettingsManager.getServerDocumentsPath().resolve(KALFolder.getOSPath()).toFile());

        //assert that the folder exists in contents but not in the filesystem
        assertTrue(folder.getContents().contains(KALFolder));
        assertFalse(Files.exists(SettingsManager.getServerDocumentsPath().resolve(KALFolder.getOSPath())));
    }
}