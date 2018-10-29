package directory.files;

import java.nio.file.Path;
import java.nio.file.Paths;

public class JSonTest {

    protected String plantName;
    int plantID;
    Path path;

    public JSonTest(String plantName, int plantID) {
        this.plantName = plantName;
        this.plantID = plantID;
        path = Paths.get(System.getProperty("user.dir") + "/Sample Files/Main Files");
    }
}
