package directory.plant;

import com.google.gson.Gson;
import directory.files.DocumentBuilder;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * Singleton pattern.
 * Used to get all plants. All plants are stored in allPlants.JSON file.
 * Use the readFromJsonFile method to load the plants into this class.
 */

public class PlantManager {
    private ArrayList<Plant> allPlants = new ArrayList<>();
    private static PlantManager plantManager;
    private static String pathToJson = "Sample files/allPlants.JSON";

    /**
     * Use this function to access the PlantManager according to the singleton Pattern.
     * @return current instance of the PlantManager.
     */
    public static synchronized PlantManager getInstance(){
        if(plantManager == null){
            plantManager = new PlantManager();
        }
        return plantManager;
    }

    public void setPathToJson(String pathToJson) {
        PlantManager.getInstance().pathToJson = pathToJson;
    }

    /**
     * Use to retrieve all plants.
     * @return arrayList of all plants.
     */
    public ArrayList<Plant> getAllPlants(){
        return allPlants;
    }

    /**
     * Used to retrieve a plant from a plant ID.
     * @param ID Plant ID.
     * @return Plant with ID int ID.
     */
    public Plant getPlant(int ID){
        for(Plant plant : getInstance().allPlants){
            if (plant.getId() == ID){
                return plant;
            }
        }
        return null;
    }

    /**
     * Add a plant to the PlantManager and updates the JSON file.
     * The JSON file has to be updated, so that it is updated on all machines.
     * @param plant The Plant to add.
     */
    public void addPlant(Plant plant) {
        getInstance().allPlants.add(plant);
        updateJsonFile();
    }

    /**
     * Delete a plant from PlantManager with the given ID.
     * The JSON file has to be updated, so that it is updated on all machines.
     * @param ID Plant ID of the given plant.
     */
    public void deletePlant(int ID){
        ArrayList<Plant> tempA = new ArrayList<>();
        tempA.addAll(getInstance().allPlants);

        for(Plant plant : tempA){
            if (plant.getId() == ID){
                getInstance().allPlants.remove(plant);
            }
        }
        updateJsonFile();
    }

    /**
     * Used to update the JSON file to the current state of the PlantManager.
     */
    public void updateJsonFile(){
        // Write object to JSON file.
        Gson g = new Gson();
        try (FileWriter writer = new FileWriter(pathToJson)){
            g.toJson(getInstance(), writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load the JSON file into the PlantManager.
     */
    public void readFromJsonFile(){
        Gson g = new Gson();
        try (Reader reader = new FileReader(pathToJson)){
            plantManager = g.fromJson(reader, PlantManager.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load the JSON file into the PlantManager.
     */
    public void readFromJsonFile(String pathToJson){
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

        DocumentBuilder.getInstance().createDocument(Paths.get("Sample files/Main Files/01_SALTFISK/FL 01 GR_01 Flowdiagram Produktion af saltfisk.pdf"));
    }
}
