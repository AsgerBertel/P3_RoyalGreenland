package io.update;

import app.ApplicationMode;
import model.FileTester;
import model.AbstractFile;
import model.Document;
import model.Folder;
import model.AccessModifier;
import model.Plant;
import io.json.AppFilesManager;
import model.managing.FileManager;
import model.managing.PlantManager;
import model.managing.SettingsManager;
import org.junit.jupiter.api.RepeatedTest;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class DirectoryClonerTest extends FileTester {

    private ArrayList<AbstractFile> al;
    private final Path docPath = Paths.get("04_MASKINTØRRET FISK/FL 04 GR_02   Flowdiagram for produktion af maskintørret fisk.pdf");
    private Document doc;
    private final Path folderPath = Paths.get("02_VINTERTØRRET FISK");
    private Folder folder;
    private final Path parentFolderPath = Paths.get("04_MASKINTØRRET FISK");
    private Folder parentFolder;
    private final Path pathKalFolder = Paths.get("02_VINTERTØRRET FISK/KAL");
    private Folder KALFolder;

    @Override
    protected void setSettings(){
        SettingsManager.loadSettings(ApplicationMode.ADMIN);
        al = FileManager.getInstance().getMainFiles();
        doc = (Document) findInMainFiles(docPath);
        folder = (Folder) findInMainFiles(folderPath);
        parentFolder = (Folder) findInMainFiles(parentFolderPath);
        KALFolder = (Folder) findInMainFiles(pathKalFolder);
    }

    @RepeatedTest(value = 2)
    void publishFiles() throws Exception {
        //asserting that both are equal when publishFiles() is used
        PlantManager.getInstance();
        DirectoryCloner.publishFiles();
        assertEquals(AppFilesManager.loadPublishedFileList(), FileManager.getInstance().getMainFiles());

        //asserting not equals when FileManager does something without publishing
        FileManager.getInstance().deleteFile(doc);
        assertNotEquals(AppFilesManager.loadPublishedFileList(), FileManager.getInstance().getMainFiles());

        //asserting equal again when io are published
        DirectoryCloner.publishFiles();
        assertEquals(AppFilesManager.loadPublishedFileList(), FileManager.getInstance().getMainFiles());
    }

    @RepeatedTest(value = 2)
    void updateLocalFiles() throws Exception {
        PlantManager.getInstance().addPlant(new Plant(1234, "cool", new AccessModifier()));
        DirectoryCloner.publishFiles();
        al = AppFilesManager.loadPublishedFileList();

        //assert that they are not equal before update
        assertNotEquals(al, AppFilesManager.loadLocalFileList());

        //assert that local io and published io are equal after update
        DirectoryCloner.updateLocalFiles();
        assertEquals(al, AppFilesManager.loadLocalFileList());
    }

    @RepeatedTest(value = 2)
    void mergeFolders() throws IllegalFileException {

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

    @RepeatedTest(value = 2)
    void deleteFolder() {

        //deletes in filesystem but not in our folder system
        DirectoryCloner.deleteFolder(SettingsManager.getServerDocumentsPath().resolve(KALFolder.getOSPath()).toFile());

        //assert that the folder exists in contents but not in the filesystem
        assertTrue(folder.getContents().contains(KALFolder));
        assertFalse(Files.exists(SettingsManager.getServerDocumentsPath().resolve(KALFolder.getOSPath())));
    }
}