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
}