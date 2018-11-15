package directory;

import directory.files.AbstractFile;
import directory.files.Document;
import directory.files.DocumentBuilder;
import directory.files.Folder;
import json.JsonParser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    // todo Archive folder path should be set on setup
    private static String pathToJson = "Sample files/allFiles.JSON";
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

    public void uploadFile(Path src, Folder dstFolder) throws IOException {
        File file = new File(src.toString());

        Path dest = Paths.get(dstFolder.getPath().toString() + File.separator + file.getName());

        if (Files.exists(dest)){
            deleteFile(DocumentBuilder.getInstance().createDocument(dest));
        }

        try {
            Files.copy(src, dest);
            Document doc = DocumentBuilder.getInstance().createDocument(dest);
            dstFolder.getContents().add(doc);
            updateFilesJson();
        } catch (IOException e) {
            System.out.println("Could not copy/upload file");
            e.printStackTrace();
        } // todo Error handling.

        //todo if file already exists, the old one is deleted but this can only happen once.
        //todo make some kind of counter to file name

    }

    public Folder createFolder(Folder folder, String name) {
        // Todo Error handling
        Folder createdFolder = new Folder(folder.getPath() + File.separator + name);

        boolean isSuccessful = new File(createdFolder.getPath().toString()).mkdirs();

        if(!isSuccessful){
            System.out.println("mkdirs was not successful");
            return null;
        }

        folder.getContents().add(createdFolder);
        updateFilesJson();
        return folder;
    }

    public void deleteFile(AbstractFile file) throws IOException {
        Path pathWithName = Paths.get(Paths.get(PathsManager.getInstance().getServerArchivePath()) + File.separator + file.getName());
        Files.move(file.getPath(), pathWithName);
        Folder parent = findParent(file);
        parent.getContents().remove(file);


        Folder archiveFolder = (Folder)getInstance().archive.get(0);
        archiveFolder.getContents().add(file);
        getInstance().updateFilesJson();
    }

    public void restoreFile(AbstractFile file) throws IOException {
        Path pathWithName = Paths.get(Paths.get(PathsManager.getInstance().getServerArchivePath()) + File.separator + file.getName());



        Files.move(pathWithName, Paths.get(PathsManager.getInstance().getServerMainFilesPath() + File.separator + file.getName()));

        Folder archiveFolder = (Folder)getInstance().archive.get(0);
        archiveFolder.getContents().remove(file);
        Folder contentFolder = (Folder)getInstance().allContent.get(0);
        contentFolder.getContents().add(file);

        getInstance().updateFilesJson();
    }

    public void updateFilesJson() {
        // Write object to JSON file.
        try (FileWriter writer = new FileWriter(PathsManager.getInstance().getServerAppFilesPath() + "allFiles.JSON")) {
            JsonParser.getJsonParser().toJson(getInstance(), writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected static FileManager readFilesFromJson() {
        // String pathStr;
        try (Reader reader = new FileReader(PathsManager.getInstance().getServerAppFilesPath() + "allFiles.JSON")) {
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

        Folder root = new Folder(PathsManager.getInstance().getServerMainFilesPath());

        getInstance().allContent.add(root);

        Files.walk(root.getPath(), 1)
                .filter(Files::isRegularFile)
                .forEach(file -> root.getContents().add(DocumentBuilder.getInstance().createDocument(file)));

        Files.walk(root.getPath(), 1)
                .filter(path1 -> Files.isDirectory(path1) && !path1.equals(root.getPath()))
                .forEach(file -> root.getContents().add(new Folder(file.toString(), true)));

        // Crawl archive
        Folder rootArchive = new Folder(PathsManager.getInstance().getServerArchivePath());

        getInstance().archive.add(rootArchive);

        Files.walk(rootArchive.getPath(), 1)
                .filter(path1 -> Files.isDirectory(path1) && !path1.equals(rootArchive.getPath()))
                .forEach(file -> rootArchive.getContents().add(new Folder(file.toString(), true)));

        Files.walk(rootArchive.getPath(), 1)
                .filter(Files::isRegularFile)
                .forEach(file -> rootArchive.getContents().add(DocumentBuilder.getInstance().createDocument(file)));

        getInstance().updateFilesJson();
    }

    public Folder getRootElement(){
        return (Folder)getInstance().allContent.get(0);
        //todo error handle if not folder
    }

    public Folder findParent(AbstractFile child) {
        return findParent(child, getRootElement());

    }

    private Folder findParent(AbstractFile child, Folder parent) {
        if (parent.getContents().contains(child)){
            return parent;
        }

        for (AbstractFile current : parent.getContents()) {
            if (current instanceof Folder) {
                Folder folder = findParent(child, (Folder)current);
                if (folder != null){
                    return folder;
                }
            }
        }
        return null;
    }

}