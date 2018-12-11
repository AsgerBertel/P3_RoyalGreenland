package directory;

import app.ApplicationMode;
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
    void setSettings(){
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

  /*  @Test
    void removeOutdatedFiles() throws Exception {
        PlantManager.getInstance().addPlant(new Plant(1234, "cool", new AccessModifier()));
        DirectoryCloner.publishFiles();

        //deletes file
        FileManager.getInstance().deleteFile(folder);

        ArrayList<AbstractFile> oldFiles = FileManager.getInstance().getMainFiles();
        ArrayList<AbstractFile> newFiles = AppFilesManager.loadPublishedFileList();

        //asserts that file is deleted in mainFiles, but not published
        //todo get archive files doesnt work
        //assertTrue(FileManager.getInstance().getArchiveFiles().contains(folder));
        assertFalse(FileManager.getInstance().getMainFiles().contains(folder));
        assertTrue(AppFilesManager.loadPublishedFileList().contains(folder));

        //pushes files to local
        ArrayList<AbstractFile> modifiedOldFiles = DirectoryCloner.removeOutdatedFiles(oldFiles, newFiles, FileManager.getInstance().getMainFilesRoot().getPath());

        //asserts that file is now deletes in published.
        assertFalse(modifiedOldFiles.contains(folder));
    }*/

  /*  @Test
    void addNewFiles() throws Exception {
        PlantManager.getInstance().addPlant(new Plant(1234, "cool", new AccessModifier()));
        DirectoryCloner.publishFiles();

        //uploads new file to mainFiles
        Folder newFolder = FileManager.getInstance().createFolder("new folder");

        ArrayList<AbstractFile> oldFiles = FileManager.getInstance().getMainFiles();
        ArrayList<AbstractFile> newFiles = AppFilesManager.loadPublishedFileList();

        //asserts that file is in mainFiles but not published.
        assertTrue(FileManager.getInstance().getMainFiles().contains(newFolder));
        assertFalse(AppFilesManager.loadPublishedFileList().contains(newFolder));

        //addNewFiles
        ArrayList<AbstractFile> modifiedOldFiles = DirectoryCloner.addNewFiles(oldFiles, newFiles, Paths.get("root"), Paths.get("root"));

        //asserts that file is now in published.
        assertTrue(modifiedOldFiles.contains(newFolder));
    }*/

   /* @Test
    void findMissingFiles() throws Exception {
        PlantManager.getInstance().addPlant(new Plant(1234, "cool", new AccessModifier()));
        DirectoryCloner.publishFiles();

        ArrayList<AbstractFile> originalFiles = FileManager.getInstance().getMainFiles();

        FileManager.getInstance().renameFile(doc, "new Name");
        FileManager.getInstance().createFolder("new folder");

        ArrayList<AbstractFile> updatedFiles = FileManager.getInstance().getMainFiles();

        ArrayList<AbstractFile> missingFiles = DirectoryCloner.findMissingFiles(updatedFiles, originalFiles);

        for (AbstractFile af: missingFiles
             ) {
            System.out.println(af.toString());
        }
    }*/

   /* @Test
    void copyFolder() {
    }*/

    @Test
    void mergeFolders() {
    }

    @Test
    void deleteFolder() {
    }
}