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
    private ArrayList<Plant> allPlants = new ArrayList<>();
    private static PlantManager plantManager;
    private static String pathToJson = "Sample files/allPlants.JSON";

    private PlantManager() {
    }

    public static synchronized PlantManager getInstance(){
        if(plantManager == null){
            plantManager = new PlantManager();
        }
        return plantManager;
    }

    public ArrayList<Plant> getAllPlants(){
        return getInstance().getAllPlants();
    }

    public Plant getPlant(int ID){
        for(Plant plant : getInstance().allPlants){
            if (plant.getId() == ID){
                return plant;
            }
        }
        return null;
    }

    public void addPlant(Plant plant) {
        getInstance().allPlants.add(plant);
        updateJsonFile();
    }

    public void deletePlant(int ID){
        ArrayList<Plant> tempA = new ArrayList<>();
        tempA.addAll(getInstance().allPlants);

        for(Plant plant : tempA){
            if (plant.getId() == ID){
                getInstance().allPlants.remove(plant);
            }
        }
        updateJsonFile();

        System.out.println(getInstance().allPlants);
    }

    public void updateJsonFile(){
        // Write object to JSON file.
        Gson g = new Gson();
        try (FileWriter writer = new FileWriter(pathToJson)){
            g.toJson(getInstance(), writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readFromJsonFile(){
        Gson g = new Gson();
        try (Reader reader = new FileReader(pathToJson)){
            plantManager = g.fromJson(reader, PlantManager.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void testPlantManager(){
        getInstance().readFromJsonFile();
        getInstance().addPlant(new Plant(1006, "sut", new AccessModifier()));
        getInstance().addPlant(new Plant(1008, "hej2", new AccessModifier()));

        System.out.println(getInstance().allPlants.get(1).getId());

        deletePlant(1008);

    }
}
