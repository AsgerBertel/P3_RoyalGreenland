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
    ArrayList<AbstractFile> allContent = new ArrayList<>();

    private static FileManager FileManager;

    public static synchronized FileManager getInstance() {
        if (FileManager == null) {
            FileManager = new FileManager();
        }
        return FileManager;
    }

    public void uploadFile(Path src, Path dst) {
        File file = new File(src.toString());
        Path dest = Paths.get(dst.toString() + File.separator + file.getName());
        try {
            Files.copy(src, dest);
            allContent.add(DocumentBuilder.getInstance().createDocument(dest));
            updateJsonFile();
        } catch (IOException e) {
            System.out.println("Could not copy/upload file");
            e.printStackTrace();
        } // todo Error handling.
        // todo do we first create the file, when we upload it? Is this correctly implemented then?

    }

    public Folder createFolder(Path path, String name) {
        // Todo Error handling
        String pathToFolder = path.toString() + File.separator + name;
        Folder folder = new Folder(Paths.get(pathToFolder).toString());
        new File(pathToFolder).mkdirs();
        allContent.add(folder);
        updateJsonFile();
        return folder;
    }

    public void deleteDocument(Document file) throws IOException {
        Path pathWithName = Paths.get(Paths.get(pathToArchive) + File.separator + file.getName());
        Files.move(file.getPath(), pathWithName);

        //deleteEmptyFolders(file.getPath());
    }

    public void restoreDocument(Document file) throws IOException {

        Path file1 = Paths.get(Paths.get(pathToArchive) + File.separator + file.getName());

        if (Files.exists(file.getParentPath())) {
            Files.move(file1, file.getPath());
        } else {
            file.getParentPath().toFile().mkdirs();
            Files.move(file1, file.getPath());
        }
    }

    /*
    private void deleteEmptyFolders(Path path) throws IOException {

        Folder folder = new Folder(path.getParent());

        File file = new File(folder.getPath().toString());

        while (file.isDirectory() && file.length() == 0){
            Files.delete(folder.getPath());
            folder = new Folder(folder.getParentPath());
            file = new File(folder.getPath().toString());
        }
    }*/

    public void deleteFolder(Folder folder) {

    }

    public void updateJsonFile() {
        // Write object to JSON file.
        try (FileWriter writer = new FileWriter(pathToJson)) {
            JsonParser.getJsonParser().toJson(getInstance(), writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readFromJsonFile() {
        // String pathStr;

        try (Reader reader = new FileReader(pathToJson)) {
            FileManager = JsonParser.getJsonParser().fromJson(reader, FileManager.class);
            /* // todo change read and write json to convert to unix file system.
            for (AbstractFile file : FileManager.allContent) {
                pathStr = file.getPath().toString().replace("\\", "/");
                        file.setPath(Paths.get(pathStr));
            }*/
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setPathToJson(String pathToJson) {
        this.pathToJson = pathToJson;
    }
}
