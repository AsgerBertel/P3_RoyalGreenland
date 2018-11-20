package directory;

import directory.files.AbstractFile;
import directory.files.Document;
import directory.files.DocumentBuilder;
import directory.files.Folder;
import gui.log.LogEventType;
import gui.log.LoggingTools;
import json.JsonParser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class FileManager {
    // todo Archive folder path should be set on setup
    private static String pathToJson = "Sample files/allFiles.JSON";

    private static final String FILES_LIST_FILE_NAME = "allFiles.JSON";

    private LoggingTools loggingTools = new LoggingTools();

    private ArrayList<AbstractFile> allContent = new ArrayList<>();
    private ArrayList<AbstractFile> archive = new ArrayList<>();

    private static FileManager fileManager;

    // Private constructor for ensuring that no other class can create a new instance this class
    private FileManager(){}

    public static synchronized FileManager getInstance() {
        if (fileManager == null) {
            fileManager = readFileManagerFromJson();
        }
        return fileManager;
    }

    //todo temporary
    public static synchronized FileManager getTestInstance() {
        if (fileManager == null) {
            fileManager = new FileManager();
        }
        return fileManager;
    }

    public ArrayList<AbstractFile> getAllContent() {
        return allContent;
    }

    public ArrayList<AbstractFile> getArchive() {
        return archive;
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
            getInstance().updateJsonFiles();
            loggingTools.LogEvent(file.getName(), LogEventType.CREATED);
        } catch (IOException e) {
            System.out.println("Could not copy/upload file");
            e.printStackTrace();
        } // todo Error handling.

        //todo if file already exists, the old one is deleted but this can only happen once.
        //todo make some kind of counter to file name
    }

    public Folder createFolder(Folder parentFolder, String name) {
        // Todo Error handling
        Folder createdFolder = new Folder(parentFolder.getPath() + File.separator + name);

        boolean isSuccessful = new File(createdFolder.getPath().toString()).mkdirs();

        if(!isSuccessful){
            System.out.println("mkdirs was not successful");
            return null;
        }

        parentFolder.getContents().add(createdFolder);
        updateJsonFiles();
        return createdFolder;
    }

    public void deleteFile(AbstractFile file) {
        Path pathWithName = Paths.get(Paths.get(PreferencesManager.getInstance().getServerArchivePath()) + File.separator + file.getName());
        try {
            Files.move(file.getPath(), pathWithName);
            Folder parent = findParent(file);
            parent.getContents().remove(file);

            Folder archiveFolder = (Folder)getInstance().archive.get(0);
            archiveFolder.getContents().add(file);
            getInstance().updateJsonFiles();
        } catch (IOException e) {
            System.out.println("Could not delete file");
            e.printStackTrace();
        }
    }

    //todo restore to original path not root folder
    public void restoreFile(AbstractFile file) throws IOException {
        Path pathWithName = Paths.get(Paths.get(PreferencesManager.getInstance().getServerArchivePath()) + File.separator + file.getName());

        Files.move(pathWithName, Paths.get(PreferencesManager.getInstance().getServerDocumentsPath() + File.separator + file.getName()));

        Folder archiveFolder = (Folder)getInstance().archive.get(0);
        archiveFolder.getContents().remove(file);
        Folder contentFolder = (Folder)getInstance().allContent.get(0);
        contentFolder.getContents().add(file);

        updateJsonFiles();
    }

    public void updateJsonFiles() {
        // Write object to JSON file.
        try (FileWriter writer = new FileWriter(PreferencesManager.getInstance().getServerAppFilesPath() + FILES_LIST_FILE_NAME)) {
            JsonParser.getJsonParser().toJson(fileManager, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected static FileManager readFileManagerFromJson() {
        // String pathStr;
        PreferencesManager prefs = PreferencesManager.getInstance();
        Path allFilesList = Paths.get(prefs.getServerAppFilesPath() + FILES_LIST_FILE_NAME);

        if(!Files.exists(allFilesList)){
            FileManager fileManager = new FileManager();
            fileManager.updateJsonFiles();
            return fileManager;
        }
        System.out.println("Nået");
        try (Reader reader = new FileReader(prefs.getServerAppFilesPath() + FILES_LIST_FILE_NAME)) {
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
        allContent.clear();
        Folder root = new Folder(PreferencesManager.getInstance().getServerDocumentsPath());
        allContent.add(findAllChildren(root));

        archive.clear();
        Folder rootArchive = new Folder(PreferencesManager.getInstance().getServerArchivePath());
        archive.add(findAllChildren(rootArchive));

        updateJsonFiles();
    }

    // Finds all children of the given root folder in the file system and add them to the root Folder object
    private Folder findAllChildren(Folder root) throws IOException{
        Files.walk(root.getPath(), 1)
                .filter(path1 -> Files.isDirectory(path1) && !path1.equals(root.getPath()))
                .forEach(file -> root.getContents().add(new Folder(file.toString(), true)));

        Files.walk(root.getPath(), 1)
                .filter(Files::isRegularFile)
                .forEach(file -> root.getContents().add(DocumentBuilder.getInstance().createDocument(file)));

        return root;
    }

    private Folder getRootElement(){
        return (Folder)getInstance().allContent.get(0);
        //todo get root of what? documents or archive? Specify or remove method - Magnus
    }

    // todo same as above. Parent in archive or main documents?
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