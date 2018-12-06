package json;


import directory.FileManager;
import directory.Settings;
import directory.files.AbstractFile;
import directory.plant.Plant;
import directory.plant.PlantManager;
import gui.AlertBuilder;
import gui.log.LoggingErrorTools;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
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
        Path path = Settings.getServerAppFilesPath().resolve(FILES_LIST_FILE_NAME);
        return loadInstanceFromJsonFile(path, FileManager.class);
    }

    /**
     * Loads the PlantManager instance stored in App Files. Returns null if no file is found or an error occurred
     * while reading the file.
     * @return the instance of FileManager stored in the path. Returns null if no FileManager exists in the path.
     */
    public static PlantManager loadPlantManager(){
        Path path = Settings.getServerAppFilesPath().resolve(FACTORY_LIST_FILE_NAME);
        return loadInstanceFromJsonFile(path, PlantManager.class);
    }

    public static ArrayList<Plant> loadLocalFactoryList(){
        Path path = Settings.getLocalAppFilesPath().resolve(FACTORY_LIST_FILE_NAME);
        PlantManager plantManager = loadInstanceFromJsonFile(path, PlantManager.class);
        if(plantManager != null)
            return plantManager.getAllPlants();

        return new ArrayList<>();
    }

    public static ArrayList<Plant> loadPublishedFactoryList(){
        Path path = Settings.getPublishedAppFilesPath().resolve(FACTORY_LIST_FILE_NAME);
        PlantManager plantManager = loadInstanceFromJsonFile(path, PlantManager.class);
        if(plantManager != null)
            return plantManager.getAllPlants();

        return new ArrayList<>();
    }

    public static ArrayList<AbstractFile> loadPublishedFileList(){
        Path path = Settings.getPublishedAppFilesPath().resolve(FILES_LIST_FILE_NAME);
        FileManager publishedFileManager = loadInstanceFromJsonFile(path, FileManager.class);
        if(publishedFileManager != null) {
            return publishedFileManager.getMainFiles();
        }
        return new ArrayList<>();
    }

    public static ArrayList<AbstractFile> loadLocalFileList(){
        Path path = Settings.getLocalAppFilesPath().resolve(FILES_LIST_FILE_NAME);
        FileManager publishedFileManager = loadInstanceFromJsonFile(path, FileManager.class);
        if(publishedFileManager != null) {
            return publishedFileManager.getMainFiles();
        }
        return new ArrayList<>();
    }

    private static <T> T loadInstanceFromJsonFile(Path path, java.lang.Class<T> classOfT){
        if(!Files.exists(path))
            return null;

        try (Reader reader = new FileReader(path.toString())) {
            return JsonParser.getJsonParser().fromJson(reader, classOfT);
        } catch (IOException e) {
            AlertBuilder.IOExceptionPopUp();
            LoggingErrorTools.log(e);
            e.printStackTrace();
            return null;
        }
    }

    public static void save(FileManager fileManager){
        saveObjectToJson(fileManager, Settings.getServerAppFilesPath().resolve(FILES_LIST_FILE_NAME));
    }

    public static void save(PlantManager plantManager){
        saveObjectToJson(plantManager, Settings.getServerAppFilesPath().resolve(FACTORY_LIST_FILE_NAME));
    }

    private static void saveObjectToJson(Object object, Path path){
        try (FileWriter writer = new FileWriter(path.toString())) {
            // Converts the object to JSon
            JsonParser.getJsonParser().toJson(object, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates the server directories.
     * @throws IOException if .mkdirs() fails.
     */
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

        createAppFolders(applicationPaths);
    }

    /**
     * Creates the local directories, throws IOException if .mkdirs fails
     * @throws IOException if .mkdirs() fails.
     */
    public static void createLocalDirectories() throws IOException{
        Path localRoot = Settings.getLocalPath();

        // Throw exception if application installation path is invalid
        if(!Files.exists(localRoot.getParent())) throw new FileNotFoundException("Local Application folder could not be found");

        ArrayList<Path> applicationPaths = new ArrayList<>();
        applicationPaths.add(Settings.getLocalFilesPath());
        applicationPaths.add(Settings.getLocalAppFilesPath());

        createAppFolders(applicationPaths);
    }

    private static void createAppFolders(ArrayList<Path> paths) throws IOException{
        for(Path appPath : paths){
            try{
                if(!Files.exists(appPath))
                    Files.createDirectories(appPath);
            }catch (IOException e){
                throw new IOException("Failed to create an application folder : " + appPath.toString(), e);
            }
        }
    }

    /**
     * Creates the server side AppFiles besides JSON files.
     * @throws FileNotFoundException if working or published AppFiles folder is missing.
     * @throws IOException if BufferedWriter fails to read from currentFileID.
     */
    private static void createServerAppFiles() throws IOException {
        Path appFilesPath = Paths.get(Settings.getServerAppFilesPath());
        Path publishedAppFilesPath = Paths.get(Settings.getPublishedAppFilesPath());

        if(!Files.exists(appFilesPath.getParent()))
            throw new FileNotFoundException("Server Working Files folder could not be found");
        if(!Files.exists(publishedAppFilesPath.getParent()))
            throw new FileNotFoundException("Server Published Files folder could not be found");

        Path currentFileIDPath = Paths.get(appFilesPath + File.separator + "currentFileID");

        if(!Files.exists(currentFileIDPath)) {
            try (BufferedWriter writer = Files.newBufferedWriter(currentFileIDPath)) {
                writer.write("0");
            } catch (IOException e) {
                e.printStackTrace();
                throw new IOException("Could not write to currentFileID file");
            }
        }

    }
}
