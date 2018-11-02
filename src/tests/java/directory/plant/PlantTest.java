package directory.plant;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlantTest {
    private Plant plant = new Plant(1007, "Nuuk", new AccessModifier());
    private Plant plant2 = plant;

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
}