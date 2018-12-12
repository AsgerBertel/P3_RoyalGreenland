package directory.plant;

import app.ApplicationMode;
import directory.FileTester;
import directory.SettingsManager;
import gui.DMSApplication;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlantManagerTest extends FileTester {

    @Override
    protected void setSettings(){
        SettingsManager.loadSettings(ApplicationMode.ADMIN);
    }

    @Test
    void resetInstance() {

    }

    @Test
    void getAllPlants() {
    }

    /*@Test
    void getPlant() {
    }*/

    @Test
    void addPlant() {
    }

    @Test
    void deletePlant() {
    }

    @Test
    void updateJsonFile() {
    }
}