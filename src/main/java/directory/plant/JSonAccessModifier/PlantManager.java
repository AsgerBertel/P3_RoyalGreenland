package directory.plant.JSonAccessModifier;

import com.google.gson.Gson;
import directory.plant.AccessModifier;
import directory.plant.Plant;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

public class PlantManager {
    private static ArrayList<Plant> allPlants = new ArrayList<>();

    public static ArrayList<Plant> getAllPlants(){
        return allPlants;
    }

    public static Plant getPlant(int ID){
        for(Plant plant : allPlants){
            if (plant.getId() == ID){
                return plant;
            }
        }
        return null;
    }

    public static void addPlant(Plant plant) {
        allPlants.add(plant);
        updateJsonFile();
    }

    public static void deletePlant(int ID){
        for(Plant plant : allPlants){
            if (plant.getId() == ID){
                allPlants.remove(plant);
            }
        }
        updateJsonFile();
    }

    public static void updateJsonFile(){
        // Write object to JSON file.
        Gson g = new Gson();
        try (FileWriter writer = new FileWriter("Sample files/allPlants.JSON")){
            g.toJson(allPlants, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void testPlantManager(){
        addPlant(new Plant(1001, "hej1", new AccessModifier()));
        addPlant(new Plant(1002, "hej2", new AccessModifier()));
    }
}
