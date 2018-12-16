package model;

import app.ApplicationMode;
import model.managing.SettingsManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlantTest extends FileTester {
    private Plant plant;
    private Plant plant2;


    void setSettings(){
        SettingsManager.loadSettings(ApplicationMode.ADMIN);
        plant = new Plant(1007, "Nuuk", new AccessModifier());
        plant2 = plant;
    }

    @Test
    void getAccessModifier() {
        assertEquals(plant2.getAccessModifier(), plant.getAccessModifier());
    }

    @Test
    void getId() {
        assertEquals(1007, plant.getId());
        assertEquals(plant2.getId(), plant.getId());
    }

    @Test
    void setId() {
        plant.setId(2007);
        assertEquals(2007, plant.getId());
    }

    @Test
    void getName() {
        assertEquals("Nuuk", plant.getName());
        assertEquals(plant2.getName(), plant.getName());
    }

    @Test
    void setName() {
        plant.setName("Sisimiut");
        assertEquals("Sisimiut", plant.getName());
    }

    @Test
    void toStringTest() {
        String str = "1007 - Nuuk";

        assertEquals(str, plant.toString());
    }
}