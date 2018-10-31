package directory.files.testJson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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


    public static void JSONtester(){
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Path.class, new pathSerializer());
        gsonBuilder.registerTypeAdapter(Path.class, new pathDeserializer());
        Gson g = gsonBuilder.create();

        JSonTest plant = new JSonTest("Fabrik", 1000);
        String output = g.toJson(plant);
        System.out.println("OUTPUT:   " + output);
        JSonTest inPlant = g.fromJson(output, JSonTest.class);
        System.out.println(inPlant.path);
    }

    public Path getPath() {
        return path;
    }
}

