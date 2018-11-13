package directory;

import directory.files.AbstractFile;
import directory.files.DocumentBuilder;
import directory.files.Folder;
import json.JsonParser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    // todo Archive folder path should be set on setup
    private static String pathToJson = "Sample files/allFiles.JSON";
    private String pathToFiles = "Sample files/Main Files";
    private String pathToArchive = "Sample files/Archive";
    private ArrayList<AbstractFile> allContent = new ArrayList<>();
    private ArrayList<AbstractFile> archive = new ArrayList<>();

    private static FileManager fileManager;

    public static synchronized FileManager getInstance() {
        if (fileManager == null) {
            fileManager = readFilesFromJson();
        }
        return fileManager;
    }

    public ArrayList<AbstractFile> getAllContent() {
        return allContent;
    }

    public ArrayList<AbstractFile> getArchive() {
        return archive;
    }

    public static synchronized FileManager getTestInstance() {
        if (fileManager == null) {
            fileManager = new FileManager();
        }
        return fileManager;
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

        boolean isSuccessful = new File(pathToFolder).mkdirs();

        if(!isSuccessful){
            System.out.println("mkdirs was not successful");
            return null;
        }

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

    public void restoreFile(AbstractFile file) throws IOException {
        Path file1 = Paths.get(Paths.get(pathToArchive) + File.separator + file.getName());

        if (Files.exists(file.getParentPath())) {
            Files.move(file1, file.getPath());
        } else {
            boolean isSuccessful = file.getParentPath().toFile().mkdirs();
            if (isSuccessful){
                Files.move(file1, file.getPath());
            } else {
                System.out.println("mkdirs was not successful");
            }
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

    protected static FileManager readFilesFromJson() {
        // String pathStr;
        try (Reader reader = new FileReader(pathToJson)) {
            return JsonParser.getJsonParser().fromJson(reader, FileManager.class);
            /* // todo change read and write json to convert to unix file system.
            for (AbstractFile file : fileManager.allContent) {
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
        // First crawl all the files
        getInstance().allContent.clear();

        Folder root = new Folder(pathToFiles);

        getInstance().allContent.add(root);

        Files.walk(root.getPath(), 1)
                .filter(path1 -> Files.isDirectory(path1) && !path1.equals(root.getPath()))
                .forEach(file -> root.getContents().add(new Folder(file.toString(), true)));

        Files.walk(root.getPath(), 1)
                .filter(Files::isRegularFile)
                .forEach(file -> root.getContents().add(DocumentBuilder.getInstance().createDocument(file)));

        // Crawl archive
        Folder rootArchive = new Folder(pathToArchive);

        getInstance().archive.add(rootArchive);

        Files.walk(rootArchive.getPath(), 1)
                .filter(path1 -> Files.isDirectory(path1) && !path1.equals(rootArchive.getPath()))
                .forEach(file -> rootArchive.getContents().add(new Folder(file.toString(), true)));

        Files.walk(rootArchive.getPath(), 1)
                .filter(Files::isRegularFile)
                .forEach(file -> rootArchive.getContents().add(DocumentBuilder.getInstance().createDocument(file)));

        updateFilesJson();
    }

    public Folder findParent(List<AbstractFile> fileList, Folder child) {
        for (AbstractFile current : fileList) {
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
