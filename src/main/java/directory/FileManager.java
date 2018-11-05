package directory;

import com.google.gson.Gson;
import directory.files.AbstractFile;
import directory.files.DocumentBuilder;
import directory.files.Folder;

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
        Folder folder = new Folder(Paths.get(pathToFolder));
        new File(path.toString() + File.separator + name).mkdirs();
        allContent.add(folder);
        updateJsonFile();
        return folder;
    }

    public void deleteFile(AbstractFile file){
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
            FileManager = g.fromJson(reader, FileManager.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readFromJsonFile(String path){
        Gson g = new Gson();
        try (Reader reader = new FileReader(path)){
            FileManager = g.fromJson(reader, FileManager.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setPathToJson(String pathToJson) {
        this.pathToJson = pathToJson;
    }
}
