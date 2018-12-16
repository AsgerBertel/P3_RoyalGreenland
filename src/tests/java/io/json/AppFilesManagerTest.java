package io.json;

import app.ApplicationMode;
import io.update.DirectoryCloner;
import model.managing.FileManager;
import model.FileTester;
import model.managing.SettingsManager;
import model.AbstractFile;
import model.Folder;
import model.AccessModifier;
import model.Plant;
import model.managing.PlantManager;
import io.update.UpdateFailException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class AppFilesManagerTest extends FileTester {

    private final Path pathToFolder = Paths.get("02_VINTERTØRRET FISK");
    private Folder folder;

    private Plant plant;

    @Override
    protected void setSettings(){
        SettingsManager.loadSettings(ApplicationMode.ADMIN);
        plant = new Plant(4321, "nice", new AccessModifier());

    }

    @Test
    void loadFileManager() {
        FileManager fm;

        FileManager.getInstance();

        fm = AppFilesManager.loadFileManager();

        assertNotNull(fm);
    }

    @Test
    void loadPlantManager() {
        PlantManager pm;

        PlantManager.getInstance().addPlant(plant);

        pm = AppFilesManager.loadPlantManager();

        assertNotNull(pm);
    }

    @Test
    void loadLocalFactoryList() throws UpdateFailException {
        FileManager.getInstance();
        ArrayList<Plant> al;

        //asserts that localFactoryList is empty
        al = AppFilesManager.loadLocalFactoryList();
        assertEquals(new ArrayList<Plant>(), al);

        PlantManager.getInstance().addPlant(plant);

        DirectoryCloner.publishFiles();
        DirectoryCloner.updateLocalFiles();

        //asserts that localFactoryList now has the plant
        al = AppFilesManager.loadLocalFactoryList();
        assertTrue(al.contains(plant));
    }

    @Test
    void loadPublishedFactoryList() throws UpdateFailException {
        FileManager.getInstance();
        ArrayList<Plant> al;

        //asserts that publishedFactoryList is empty
        al = AppFilesManager.loadPublishedFactoryList();
        assertEquals(new ArrayList<Plant>(), al);

        PlantManager.getInstance().addPlant(plant);

        DirectoryCloner.publishFiles();

        //asserts that publishedFactoryList now has the plant
        al = AppFilesManager.loadPublishedFactoryList();
        assertTrue(al.contains(plant));
    }

    @Test
    void loadPublishedFileList() throws IOException, UpdateFailException {
        FileManager.getInstance();
        PlantManager.getInstance();
        ArrayList<AbstractFile> al;
        folder = (Folder) findInMainFiles(pathToFolder);

        //asserts that publishedFileList is empty
        al = AppFilesManager.loadPublishedFileList();
        assertEquals(new ArrayList<AbstractFile>(), al);

        //publishes a renamed folder
        FileManager.getInstance().renameFile(folder, "new folder");
        DirectoryCloner.publishFiles();

        //asserts that folder is in publishedFileList
        al = AppFilesManager.loadPublishedFileList();
        assertTrue(al.contains(folder));
    }

    @Test
    void loadLocalFileList() throws IOException, UpdateFailException {
        FileManager.getInstance();
        PlantManager.getInstance();
        ArrayList<AbstractFile> al;
        folder = (Folder) findInMainFiles(pathToFolder);

        //asserts that localFileList is empty
        al = AppFilesManager.loadLocalFileList();
        assertEquals(new ArrayList<AbstractFile>(), al);

        //publishes a renamed folder
        FileManager.getInstance().renameFile(folder, "new folder");
        DirectoryCloner.publishFiles();
        DirectoryCloner.updateLocalFiles();

        //asserts that folder is in localFileList
        al = AppFilesManager.loadLocalFileList();
        assertTrue(al.contains(folder));
    }

    @Test
    void save(){
        folder = (Folder) findInMainFiles(pathToFolder);

        FileManager fm = FileManager.getInstance();
        FileManager oldFm = fm;

        fm.getMainFiles().add(folder);

        AppFilesManager.save(fm);

        //they are equal because of singleton pattern. Every instance is the same
        //every time something happens the fileManager saves itself.
        assertEquals(oldFm, fm);
    }

    @Test
    void save1() {
        PlantManager pm = PlantManager.getInstance();
        PlantManager oldPm = pm;

        pm.addPlant(plant);

        AppFilesManager.save(pm);

        //they are equal because of singleton pattern. Every instance is the same
        //every time something happens the PlantManager saves itself.
        assertEquals(oldPm, pm);
    }

    @Test
    void createServerDirectories() throws IOException {
        Path newServerPath = SettingsManager.getServerDocumentsPath();
        Path wrongPath = Paths.get("hurgh/geburg/lurk");

        SettingsManager.setServerPath(newServerPath);
        AppFilesManager.createServerDirectories();

        //asserts that new server path is added
        assertEquals(newServerPath, SettingsManager.getServerPath());

        SettingsManager.setServerPath(wrongPath);

        //asserts that wrong server path is not possible
        Executable test = AppFilesManager::createServerDirectories;
        assertThrows(FileNotFoundException.class, test);
    }

    @Test
    void createLocalDirectories() throws IOException {
        Path newServerPath = SettingsManager.getServerDocumentsPath();
        Path wrongPath = Paths.get("hurgh/geburg/lurk");

        SettingsManager.setLocalPath(newServerPath);
        AppFilesManager.createLocalDirectories();

        //asserts that new local path is added
        assertEquals(newServerPath, SettingsManager.getLocalPath());

        SettingsManager.setLocalPath(wrongPath);

        //asserts that wrong local path is not possible
        Executable test = AppFilesManager::createLocalDirectories;
        assertThrows(FileNotFoundException.class, test);
    }
}