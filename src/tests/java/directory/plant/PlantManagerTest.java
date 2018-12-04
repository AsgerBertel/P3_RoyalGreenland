package directory.plant;

import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

class PlantManagerTest {
    private File resourcesDirectory = new File("src/tests/resTest" + File.separator);
    private Path pathToJsonTest = Paths.get(resourcesDirectory.getAbsolutePath() + File.separator + "Main Files Test/RLFiles/Server/App Files/allPlants.JSON");

    @BeforeEach
    void initEach(){
        /* todo These tests are temporarily removed as they violate the singleton principle
           either change the PlantManager/FilesManager to not use singleton patterns or change the path inside settings class when running tests
            ie. Settings.setPath() (don't forget to change back afterwards) - Magnus
         */

        //PlantManager.getInstance().readFromJsonFile(pathToJsonTest.toString());
    }
/*
    @Test
    void getInstance() {
        assertSame(PlantManager.getInstance(), PlantManager.getInstance());
    }

    @Test
    void getAllPlants() {
        PlantManager.getInstance().readFromJsonFile(pathToJsonTest.toString());
        assertEquals(1000, PlantManager.getInstance().getAllPlants().get(0).getId());
    }

    @Test
    void getPlant() {
        Plant tempPlant = new Plant(1000,"plant0", new AccessModifier());
        Plant readPlant = PlantManager.getInstance().getPlant(1000);
        assertEquals(tempPlant.getId(), readPlant.getId());
        assertEquals(tempPlant.getName(), readPlant.getName());
    }

    @Test
    void addPlantAndDeletePlantUpdateJsonFile() {
        PlantManager.getInstance().setPathToJson(pathToJsonTest.toString());
        Plant tempPlant = new Plant(1003,"plant3", new AccessModifier());
        PlantManager.getInstance().addPlant(tempPlant);
        PlantManager.getInstance().readFromJsonFile();
        assertEquals("plant3", PlantManager.getInstance().getAllPlants().get(2).getName());
        PlantManager.getInstance().deletePlant(1003);
        assertEquals(2, PlantManager.getInstance().getAllPlants().size());
    }*/
}