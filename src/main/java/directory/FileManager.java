package directory;

import directory.files.Document;
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
    public static String pathToJson = "Sample files/allFiles.JSON";
    String pathToArchive = "Sample files/Archive";
    ArrayList<AbstractFile> allContent = new ArrayList<>();
    ArrayList<AbstractFile> archive = new ArrayList<>();

    private static FileManager FileManager;

    public ArrayList<AbstractFile> getAllContent() {
        return allContent;
    }

    public static synchronized FileManager getInstance() {
        if (FileManager == null) {
            FileManager = readFilesFromJson();
        }
        return FileManager;
    }

    public static synchronized FileManager getTestInstance() {
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
            updateFilesJson();
        } catch (IOException e) {
            System.out.println("Could not copy/upload file");
            e.printStackTrace();
        } // todo Error handling.
        // todo Is this correctly implemented? We just copy the file from src to dst.

    }

    public Folder createFolder(Path path, String name) {
        // Todo Error handling
        String pathToFolder = path.toString() + File.separator + name;
        Folder folder = new Folder(Paths.get(pathToFolder).toString());
        new File(pathToFolder).mkdirs();
        allContent.add(folder);
        updateFilesJson();
        return folder;
    }

    public void deleteFile(AbstractFile file) throws IOException {
        Path pathWithName = Paths.get(Paths.get(pathToArchive) + File.separator + file.getName());
        Files.move(file.getPath(), pathWithName);
        allContent.remove(file);
        archive.add(file);
        updateFilesJson();
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

    public void updateFilesJson() {
        // Write object to JSON file.
        try (FileWriter writer = new FileWriter(pathToJson)) {
            JsonParser.getJsonParser().toJson(getInstance(), writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static FileManager readFilesFromJson() {
        // String pathStr;
        try (Reader reader = new FileReader(pathToJson)) {
            return JsonParser.getJsonParser().fromJson(reader, FileManager.class);
            /* // todo change read and write json to convert to unix file system.
            for (AbstractFile file : FileManager.allContent) {
                pathStr = file.getPath().toString().replace("\\", "/");
                        file.setPath(Paths.get(pathStr));
            }*/
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setPathToJson(String pathToJson) {
        this.pathToJson = pathToJson;
    }

    public void initFolderTree() throws IOException{
        getInstance().allContent.clear();

        Folder root = new Folder("Sample files/Main Files");

        getInstance().allContent.add(root);

        Files.walk(root.getPath(), 1)
                .filter(path1 -> Files.isDirectory(path1) && !path1.equals(root.getPath()))
                .forEach(file -> root.getContents().add(new Folder(file.toString(), true)));

        Files.walk(root.getPath(), 1)
                .filter(Files::isRegularFile)
                .forEach(file -> root.getContents().add(DocumentBuilder.getInstance().createDocument(file)));

        updateFilesJson();
    }

    public Folder findParent(Folder child) {
        for (AbstractFile current : getInstance().getAllContent()) {
            if (current instanceof Folder) {
                if (((Folder) current).getContents().contains(child)) {
                    return (Folder) current;
                } else {
                    return ((Folder) current).findParent(child);
                }
            }
        }
        return child;
    }
}
