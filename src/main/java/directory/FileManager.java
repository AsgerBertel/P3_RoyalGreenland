package directory;

import directory.files.Document;
import com.google.gson.Gson;
import directory.files.AbstractFile;
import directory.files.DocumentBuilder;
import directory.files.Folder;
import json.JsonParser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class FileManager {
    // todo Archive folder path should be set on setup
    public String pathToJson = "Sample files/allFiles.JSON";
    String pathToArchive = "Sample files/Archive";
    private static FileManager FileManager;
    ArrayList<AbstractFile> allContent = new ArrayList<>();

    public static synchronized FileManager getInstance(){
        if(FileManager == null){
            FileManager = new FileManager();
        }
        return FileManager;
    }

    public void uploadFile(Path src, Path dst){
        File file = new File(src.toString());
        Path dest = Paths.get(dst.toString() + File.separator + file.getName());
        try {
            Files.copy(src, dest);
            allContent.add(DocumentBuilder.getInstance().createDocument(dst));
            updateJsonFile();
        } catch (IOException e) {
            System.out.println("Could not copy/upload file");
            e.printStackTrace();
        } // todo Error handling.
        // todo do we first create the file, when we upload it? Is this correctly implemented then?

    }

    public Folder createFolder(Path path, String name){
        // Todo Error handling
        String pathToFolder = path.toString() + File.separator + name;
        Folder folder = new Folder(Paths.get(pathToFolder).toAbsolutePath().toString());
        new File(path.toString() + File.separator + name).mkdirs();
        allContent.add(folder);
        updateJsonFile();
        return folder;
    }

    public void deleteFile(AbstractFile file) throws IOException {
        Path pathWithName = Paths.get(Paths.get(pathToArchive).toAbsolutePath() + File.separator + file.getName());
        Files.move(file.getPath(), pathWithName);
        //todo remove from json file
    }

    public void restoreDocument (Document file) throws IOException {

        Path file1 = Paths.get(Paths.get(pathToArchive).toAbsolutePath() + File.separator + file.getName());

        if (Files.exists(file.getParentPath())) {
            Files.move(file1, file.getPath());
        } else {
            file.getParentPath().toFile().mkdirs();
            Files.move(file1, file.getPath());
        }

        //todo make abstract file and add folder compatibility
    }

    public void updateJsonFile() {
        // Write object to JSON file.
        try (FileWriter writer = new FileWriter(pathToJson)) {
            JsonParser.getJsonParser().toJson(getInstance(), writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readFromJsonFile(){
        try (Reader reader = new FileReader(pathToJson)){
            FileManager = JsonParser.getJsonParser().fromJson(reader, FileManager.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readFromJsonFile(String path){
        try (Reader reader = new FileReader(path)){
            FileManager = JsonParser.getJsonParser().fromJson(reader, FileManager.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setPathToJson(String pathToJson) {
        this.pathToJson = pathToJson;
    }
}
