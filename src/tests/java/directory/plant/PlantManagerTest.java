package directory.plant;

import app.ApplicationMode;
import directory.FileTester;
import directory.SettingsManager;
import gui.DMSApplication;
import json.AppFilesManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlantManagerTest extends FileTester {

    Plant plant;

    @Override
    protected void setSettings(){
        SettingsManager.loadSettings(ApplicationMode.ADMIN);
        plant = new Plant(4567, "plant", new AccessModifier());
    }

    @Test
    void getAllPlants() {
        PlantManager pm = PlantManager.getInstance();

        pm.addPlant(plant);
        pm.addPlant(plant);
        pm.addPlant(plant);

        assertEquals(3, pm.getAllPlants().size());
        assertTrue(pm.getAllPlants().contains(plant));
    }

    /*@Test
    void getPlant() {
    }*/

    @Test
    void addPlant() {
        PlantManager pm = PlantManager.getInstance();

        pm.addPlant(plant);

        assertTrue(pm.getAllPlants().contains(plant));
    }

    @Test
    void deletePlant() {
        PlantManager pm = PlantManager.getInstance();

        pm.addPlant(plant);

        assertTrue(pm.getAllPlants().contains(plant));

        pm.deletePlant(plant.getId());

        assertFalse(pm.getAllPlants().contains(plant));
    }

    @Test
    void updateJsonFile() {
        PlantManager pm = PlantManager.getInstance();

        pm.addPlant(plant);

        pm.updateJsonFile();

        PlantManager newPm = PlantManager.getInstance();

        assertEquals(pm, newPm);
    }
}