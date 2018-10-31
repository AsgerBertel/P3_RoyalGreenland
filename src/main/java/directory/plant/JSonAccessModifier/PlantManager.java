package directory.plant.JSonAccessModifier;

import directory.plant.Plant;

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
}
