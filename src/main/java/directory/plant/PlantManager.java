package directory.plant;

import directory.PreferencesManager;
import json.JsonParser;

import java.io.*;
import java.util.ArrayList;

/**
 * Singleton pattern.
 * Used to get all plants. All plants are stored in allPlants.JSON file.
 * Use the readFileManagerFromJson method to load the plants into this class.
 */

public class PlantManager {
    private ArrayList<Plant> allPlants = new ArrayList<>();
    private static String pathToJson = "Sample files/allPlants.JSON";
    private static final String FACTORY_LIST_FILE_NAME = "allPlants.JSON";
    private static PlantManager plantManager;

    /**
     * Use this function to access the PlantManager according to the singleton Pattern.
     * @return current instance of the PlantManager.
     */
    public static synchronized PlantManager getInstance(){
        if(plantManager == null){
            plantManager = readFromJsonFile();
        }
        return plantManager;
    }

    public void setPathToJson(String pathToJson) {
        getInstance().pathToJson = pathToJson;
    }

    public ArrayList<Plant> ReadJsonAndGetAllPlants(){
        return allPlants;
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
    protected void updateJsonFile(){
        // Write object to JSON file.
        try (FileWriter writer = new FileWriter(PreferencesManager.getInstance().getServerAppFilesPath() + File.separator + "allPlants.JSON")){
            JsonParser.getJsonParser().toJson(getInstance(), writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load the JSON file into the PlantManager.
     */
    public static PlantManager readFromJsonFile(){
        try (Reader reader = new FileReader(PreferencesManager.getInstance().getServerAppFilesPath() + File.separator + "allPlants.JSON")){
                return JsonParser.getJsonParser().fromJson(reader, PlantManager.class);
        } catch (IOException e) {
            System.out.println("Could not read JSON plants.");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Load the JSON file into the PlantManager.
     */
    public void readFromJsonFile(String pathToJson){
        try (Reader reader = new FileReader(pathToJson)){
            plantManager = JsonParser.getJsonParser().fromJson(reader, PlantManager.class);
        } catch (IOException e) {
            System.out.println("Could not read JSON plants.");
            e.printStackTrace();
        }
    }
}
