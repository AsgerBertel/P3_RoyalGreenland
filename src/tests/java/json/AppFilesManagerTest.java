package json;

import app.ApplicationMode;
import directory.DirectoryCloner;
import directory.FileManager;
import directory.FileTester;
import directory.SettingsManager;
import directory.files.AbstractFile;
import directory.plant.AccessModifier;
import directory.plant.Plant;
import directory.plant.PlantManager;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class AppFilesManagerTest extends FileTester {

    @Override
    protected void setSettings(){
        SettingsManager.loadSettings(ApplicationMode.ADMIN);
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
        ArrayList<Plant> al;

        al = AppFilesManager.loadLocalFactoryList();

        //todo shouldnt plantmanager be null here? does reset delete plant list?
        //assertNull(al.get(0));

        PlantManager.getInstance().addPlant(new Plant(4321, "nice", new AccessModifier()));

        al = AppFilesManager.loadLocalFactoryList();

        for (Plant p: al
             ) {
            System.out.println(p.toString());
        }
    }

    @Test
    void loadPublishedFactoryList() {
    }

    @Test
    void loadPublishedFileList() {
    }

    @Test
    void loadLocalFileList() {
    }

    @Test
    void save() {
    }

    @Test
    void save1() {
    }

    @Test
    void createServerDirectories() {
    }

    @Test
    void createLocalDirectories() {
    }
}