package json;


import directory.FileManager;
import directory.Settings;
import directory.plant.PlantManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class AppFilesManager {

    private static final String FILES_LIST_FILE_NAME = "allFiles.JSON";
    private static final String FACTORY_LIST_FILE_NAME = "allPlants.JSON";

    /**
     * Loads the FileManager instance stored in App Files. Returns null if no file is found or an error occurred
     * while reading the file.
     * @return the instance of FileManager stored in the path. Returns null if no FileManager exists in the path.
     */
    public static FileManager loadFileManager(){
        String path = Settings.getInstance().getServerAppFilesPath() + FILES_LIST_FILE_NAME;
        return loadInstanceFromJsonFile(path, FileManager.class);
    }

    /**
     * Loads the PlantManager instance stored in App Files. Returns null if no file is found or an error occurred
     * while reading the file.
     * @return the instance of FileManager stored in the path. Returns null if no FileManager exists in the path.
     */
    public static PlantManager loadPlantManager(){
        String path = Settings.getInstance().getServerAppFilesPath() + FACTORY_LIST_FILE_NAME;
        return loadInstanceFromJsonFile(path, PlantManager.class);
    }

    private static <T> T loadInstanceFromJsonFile(String path, java.lang.Class<T> classOfT){
        if(Files.exists(Paths.get(path)))
            return null;

        try (Reader reader = new FileReader(path)) {
            return JsonParser.getJsonParser().fromJson(reader, classOfT);
            // todo change read and write json to convert to unix file system.
        } catch (IOException e) {
            e.printStackTrace(); // todo handle exception. Method Throws maybe? - Magnus
            return null;
        }
    }


    public static void save(FileManager fileManager){
        saveObjectToJson(fileManager, Settings.getInstance().getServerAppFilesPath() + FILES_LIST_FILE_NAME);
    }

    public static void save(PlantManager plantManager){
        saveObjectToJson(plantManager, Settings.getInstance().getServerAppFilesPath() + FACTORY_LIST_FILE_NAME);
    }

    private static void saveObjectToJson(Object object, String path){

        try (FileWriter writer = new FileWriter(path)) {
            // Converts the object to JSon
            JsonParser.getJsonParser().toJson(object, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }







}
