package json;


import directory.FileManager;
import directory.Settings;
import directory.files.AbstractFile;
import directory.plant.PlantManager;
import gui.AlertBuilder;
import gui.log.LoggingErrorTools;
import javafx.scene.control.Alert;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class AppFilesManager {

    public static final String FILES_LIST_FILE_NAME = "allFiles.JSON";
    public static final String FACTORY_LIST_FILE_NAME = "allPlants.JSON";

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

    public static ArrayList<AbstractFile> loadPublishedFileList(){
        String path = Settings.getPublishedAppFilesPath() + FILES_LIST_FILE_NAME;
        FileManager publishedFileManager = loadInstanceFromJsonFile(path, FileManager.class);
        if(publishedFileManager != null) {
            return publishedFileManager.getMainFiles();
        }
        return new ArrayList<>();
    }

    public static ArrayList<AbstractFile> loadLocalFileList(){
        String path = Settings.getLocalAppFilesPath() + FILES_LIST_FILE_NAME;
        FileManager publishedFileManager = loadInstanceFromJsonFile(path, FileManager.class);
        if(publishedFileManager != null) {
            return publishedFileManager.getMainFiles();
        }
        return new ArrayList<>();
    }

    private static <T> T loadInstanceFromJsonFile(String path, java.lang.Class<T> classOfT){
        if(!Files.exists(Paths.get(path)))
            return null;

        try (Reader reader = new FileReader(path)) {
            return JsonParser.getJsonParser().fromJson(reader, classOfT);
        } catch (IOException e) {
            AlertBuilder.IOExceptionPopUp();
            LoggingErrorTools.log(e);
            e.printStackTrace();
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
        Path serverRoot = Settings.getServerPath();

        // Throw exception if application installation path is invalid
        if(!Files.exists(serverRoot.getParent())) throw new FileNotFoundException("The chosen server directory could not be found");

        ArrayList<Path> applicationPaths = new ArrayList<>();
        applicationPaths.add(Settings.getServerDocumentsPath());
        applicationPaths.add(Settings.getServerArchivePath());
        applicationPaths.add(Settings.getServerAppFilesPath());
        applicationPaths.add(Settings.getPublishedAppFilesPath());
        applicationPaths.add(Settings.getPublishedDocumentsPath());
        applicationPaths.add(Settings.getServerErrorLogsPath());
        applicationPaths.add(Settings.getLocalFilesPath());
        applicationPaths.add(Settings.getLocalAppFilesPath());

        for(Path appPath : applicationPaths){
            try{
                if(!Files.exists(appPath))
                    Files.createDirectories(appPath);
            }catch (IOException e){
                throw new IOException("Failed to create an application folder : " + appPath.toString(), e);
            }
        }
    }

    public static void createLocalDirectories() throws IOException{
        Path localRoot = Settings.getLocalPath();

        // Throw exception if application installation path is invalid
        if(!Files.exists(localRoot.getParent())) throw new FileNotFoundException("Local Application folder could not be found");

        Path localDocumentsPath = Settings.getLocalFilesPath();
        Path localAppFilesPath = Settings.getLocalAppFilesPath();

        if(!Files.exists(localDocumentsPath))
            if(!localDocumentsPath.toFile().mkdirs())
                throw new IOException("Could not create local Documents folder");

        if(!Files.exists(localAppFilesPath))
            if(!localAppFilesPath.toFile().mkdirs())
                throw new IOException("Could not create local App Files folder");
    }
}
