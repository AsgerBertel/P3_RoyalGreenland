package json;

import app.ApplicationMode;
import directory.update.DirectoryCloner;
import directory.FileManager;
import directory.FileTester;
import directory.SettingsManager;
import directory.files.AbstractFile;
import directory.files.Document;
import directory.files.Folder;
import directory.plant.AccessModifier;
import directory.plant.Plant;
import directory.plant.PlantManager;
import directory.update.UpdateFailException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AppFilesManagerTest extends FileTester {

    Path pathToFolder = Paths.get("02_VINTERTØRRET FISK");
    Folder folder;

    Plant plant;

    @Override
    protected void setSettings(){
        SettingsManager.loadSettings(ApplicationMode.ADMIN);
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

        PlantManager.getInstance().addPlant(plant);

        pm = AppFilesManager.loadPlantManager();

        assertNotNull(pm);
    }

    @Test
    void loadLocalFactoryList() {
        FileManager.getInstance();
        ArrayList<Plant> al;

        //asserts that localFactoryList is empty
        al = AppFilesManager.loadLocalFactoryList();
        assertEquals(new ArrayList<Plant>(), al);

        PlantManager.getInstance().addPlant(plant);

        //asserts no exceptions are thrown
        assertDoesNotThrow(DirectoryCloner::publishFiles);
        assertDoesNotThrow(DirectoryCloner::updateLocalFiles);

        //asserts that localFactoryList now has the plant
        al = AppFilesManager.loadLocalFactoryList();
        assertTrue(al.contains(plant));
    }

    @Test
    void loadPublishedFactoryList() {
        FileManager.getInstance();
        ArrayList<Plant> al;

        //asserts that publishedFactoryList is empty
        al = AppFilesManager.loadPublishedFactoryList();
        assertEquals(new ArrayList<Plant>(), al);

        PlantManager.getInstance().addPlant(plant);

        //asserts no exceptions are thrown
        assertDoesNotThrow(DirectoryCloner::publishFiles);

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
        assertDoesNotThrow(DirectoryCloner::publishFiles);
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

        //assert no exceptions are thrown
        assertDoesNotThrow(DirectoryCloner::publishFiles);
        DirectoryCloner.publishFiles();
        assertDoesNotThrow(DirectoryCloner::updateLocalFiles);
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