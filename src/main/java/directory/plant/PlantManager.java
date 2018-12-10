package directory.plant;

import json.AppFilesManager;

import java.util.ArrayList;

/**
 * Singleton pattern.
 * Used to get all plants. All plants are stored in allPlants.JSON file.
 * Use the readFileManagerFromJson method to load the plants into this class.
 */

public class PlantManager {
    private ArrayList<Plant> allPlants = new ArrayList<>();
    private static PlantManager plantManager;

    /**
     * Use this function to access the PlantManager according to the singleton Pattern.
     * @return current instance of the PlantManager.
     */
    public static synchronized PlantManager getInstance(){
        if(plantManager == null){
            plantManager = AppFilesManager.loadPlantManager();

            if(plantManager == null) {
                plantManager = new PlantManager();
                AppFilesManager.save(plantManager);
            }
        }
        return plantManager;
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
        AppFilesManager.save(this);
    }
}
