package json;

import app.ApplicationMode;
import directory.DirectoryCloner;
import directory.FileManager;
import directory.FileTester;
import directory.SettingsManager;
import directory.files.AbstractFile;
import directory.files.Document;
import directory.files.Folder;
import directory.plant.AccessModifier;
import directory.plant.Plant;
import directory.plant.PlantManager;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class AppFilesManagerTest extends FileTester {

    Path pathToFolder = Paths.get("02_VINTERTØRRET FISK");
    Folder folder;

    Plant plant;

    @Override
    protected void setSettings(){
        SettingsManager.loadSettings(ApplicationMode.ADMIN);
        folder = (Folder) findInMainFiles(pathToFolder);
        plant = new Plant(4321, "nice", new AccessModifier());
    }

    @Test
    void loadFileManager() {
        FileManager fm;

        fm = AppFilesManager.loadFileManager();

        assertNull(fm);

        FileManager.getInstance();

        fm = AppFilesManager.loadFileManager();

        assertNotNull(fm);
    }

    @Test
    void loadPlantManager() {
        PlantManager pm;

        pm = AppFilesManager.loadPlantManager();

        assertNull(pm);

        PlantManager.getInstance();

        pm = AppFilesManager.loadPlantManager();

        assertNotNull(pm);
    }

    @Test
    void loadLocalFactoryList() throws IOException {
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
    void loadPublishedFactoryList() throws IOException {
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
    void loadPublishedFileList() throws IOException {
        FileManager.getInstance();
        PlantManager.getInstance();
        ArrayList<AbstractFile> al;

        //asserts that publishedFileList is empty
        al = AppFilesManager.loadPublishedFileList();
        assertEquals(new ArrayList<AbstractFile>(), al);

        //publishes a renamed folder
        FileManager.getInstance().renameFile(folder, "new folder");
        DirectoryCloner.publishFiles();

        //todo when a changed file is published, then it is in root folder in the filesystem
        //todo and all other folders are in the same folder as root?
        //asserts that folder is in publishedFileList
        al = AppFilesManager.loadPublishedFileList();
        assertTrue(al.contains(folder));
    }

    @Test
    void loadLocalFileList() throws IOException {
        FileManager.getInstance();
        PlantManager.getInstance();
        ArrayList<AbstractFile> al;

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
    void createServerDirectories() {
    }

    @Test
    void createLocalDirectories() {
    }
}