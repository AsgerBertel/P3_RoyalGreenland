package json;


import directory.FileManager;
import directory.Settings;
import directory.plant.PlantManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
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
        String path = Settings.getServerAppFilesPath() + FILES_LIST_FILE_NAME;
        return loadInstanceFromJsonFile(path, FileManager.class);
    }

    /**
     * Loads the PlantManager instance stored in App Files. Returns null if no file is found or an error occurred
     * while reading the file.
     * @return the instance of FileManager stored in the path. Returns null if no FileManager exists in the path.
     */
    public static PlantManager loadPlantManager(){
        String path = Settings.getServerAppFilesPath() + FACTORY_LIST_FILE_NAME;
        return loadInstanceFromJsonFile(path, PlantManager.class);
    }

    private static <T> T loadInstanceFromJsonFile(String path, java.lang.Class<T> classOfT){
        if(!Files.exists(Paths.get(path)))
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
        saveObjectToJson(fileManager, Settings.getServerAppFilesPath() + FILES_LIST_FILE_NAME);
    }

    public static void save(PlantManager plantManager){
        saveObjectToJson(plantManager, Settings.getServerAppFilesPath() + FACTORY_LIST_FILE_NAME);
    }

    private static void saveObjectToJson(Object object, String path){
        try (FileWriter writer = new FileWriter(path)) {
            // Converts the object to JSon
            JsonParser.getJsonParser().toJson(object, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createServerDirectories() throws IOException {
        Path serverRoot = Paths.get(Settings.getServerPath());

        // Throw exception if application installation path is invalid
        if(!Files.exists(serverRoot.getParent())) throw new FileNotFoundException("The chosen server directory could not be found");

        Path serverDocumentsPath = Paths.get(Settings.getServerDocumentsPath());
        Path serverArchivePath = Paths.get(Settings.getServerArchivePath());
        Path serverAppFilesPath = Paths.get(Settings.getServerAppFilesPath());
        Path publishedAppFilesPath = Paths.get(Settings.getPublishedAppFilesPath());
        Path publishedDocumentsPath = Paths.get(Settings.getPublishedDocumentsPath());

        boolean succes = true;
        if(!Files.exists(serverDocumentsPath))
            succes = serverDocumentsPath.toFile().mkdirs();

        if(!Files.exists(serverArchivePath))
            succes &= serverArchivePath.toFile().mkdirs();

        if(!Files.exists(serverAppFilesPath))
            succes &= serverAppFilesPath.toFile().mkdirs();

        if(!Files.exists(publishedAppFilesPath))
            succes &= publishedAppFilesPath.toFile().mkdirs();

        if(!Files.exists(publishedDocumentsPath))
            succes &= publishedDocumentsPath.toFile().mkdirs();

        if(!succes){ // todo Look into whyt mkdirs() might fail and throw appropriate exception (Probably something about write permissions)
            // todo Also in the case of the server directories connection might be a factor
            throw new IOException("Could not create application directories");
        }
    }

    public static void createLocalDirectories() throws FileNotFoundException, IOException{
        Path localRoot = Paths.get(Settings.getLocalPath());

        // Throw exception if application installation path is invalid
        if(!Files.exists(localRoot.getParent())) throw new FileNotFoundException("Local Application folder could not be found");

        Path localDocumentsPath = Paths.get(Settings.getLocalFilesPath());
        Path localAppFilesPath = Paths.get(Settings.getLocalAppFilesPath());

        boolean succes = true;
        if(!Files.exists(localDocumentsPath))
            succes = localDocumentsPath.toFile().mkdirs();

        if(!Files.exists(localAppFilesPath))
            succes &= localAppFilesPath.toFile().mkdirs();

        if(!succes){ // todo Look into whyt mkdirs() might fail and throw appropriate exception (Probably something about write permissions)
            throw new IOException("Could not create application directories");
        }

    }
}
