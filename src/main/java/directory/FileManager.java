package directory;

import directory.files.AbstractFile;
import directory.files.Document;
import directory.files.DocumentBuilder;
import directory.files.Folder;
import json.AppFilesManager;
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


    private ArrayList<AbstractFile> allContent = new ArrayList<>();
    private ArrayList<AbstractFile> archive = new ArrayList<>();

    private Folder mainFilesRoot;
    private Folder archiveRoot;

    private static FileManager fileManager;
    private PreferencesManager preferencesManager;

    // Private constructor for ensuring that no other class can create a new instance this class
    private FileManager(){
        // createFolderTree(); todo todo todo !!!!!!!!!!
    }

    public static synchronized FileManager getInstance() {
        if (fileManager == null) {
            fileManager = AppFilesManager.loadFileManager();

            // If still null create new FileManager and save it on the server
            if(fileManager == null){
                fileManager = new FileManager();
                AppFilesManager.save(fileManager);
            }
            fileManager.preferencesManager = PreferencesManager.getInstance();
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
            updateJsonFiles();
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

    public void restoreFile(AbstractFile file) throws IOException {
        Path pathWithName = Paths.get(Paths.get(PreferencesManager.getInstance().getServerArchivePath()) + File.separator + file.getName());

        Files.move(pathWithName, Paths.get(PreferencesManager.getInstance().getServerDocumentsPath() + File.separator + file.getName()));

        Folder archiveFolder = (Folder)getInstance().archive.get(0);
        archiveFolder.getContents().remove(file);
        Folder contentFolder = (Folder)getInstance().allContent.get(0);
        contentFolder.getContents().add(file);

        getInstance().updateJsonFiles();
    }

    public void updateJsonFiles() {
        // Write object to JSON file.
        AppFilesManager.save(this);
        /*try (FileWriter writer = new FileWriter(PreferencesManager.getInstance().getServerAppFilesPath() + "allFiles.JSON")) {
            JsonParser.getJsonParser().toJson(getInstance(), writer);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    // todo remove when get test instance is removed
    protected static FileManager readFileManagerFromJson() {
        // String pathStr;
        PreferencesManager prefs = PreferencesManager.getInstance();
        Path allFilesList = Paths.get(prefs.getServerAppFilesPath() + FILES_LIST_FILE_NAME);


        if(!Files.exists(allFilesList)){
            FileManager fileManager = new FileManager();
            fileManager.updateJsonFiles();
            return fileManager;
        }

        try (Reader reader = new FileReader(PreferencesManager.getInstance().getServerAppFilesPath() + "allFiles.JSON")) {
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

    /**
     * Searches the local file system and creates a folder object corresponding to the given root file.
     * The folder will contain AbstractFile objects corresponding to the files stored within the root file.
     * @param root the root folder from which the folder object is generated. Must be a directory.
     * @return
     * @throws IOException
     */
    public static Folder createFolderTree(File root) throws IOException{
        if(!root.exists()){
            // todo - Magnus
        }

        if(!root.isDirectory()){
            // todo illegal argument exception?? - Magnus
        }

        Folder rootFolder = new Folder(root.getPath());
        rootFolder.getContents().addAll(findAllChildren(rootFolder));

        return rootFolder;
    }

    // Finds all children of the given root folder in the file system and add them to the root Folder object
    private static ArrayList<AbstractFile> findAllChildren(Folder root) throws IOException{
        ArrayList<AbstractFile> contents = new ArrayList<>();

        Files.walk(root.getPath(), 1)
                .filter(path1 -> Files.isDirectory(path1) && !path1.equals(root.getPath()))
                .forEach(file -> root.getContents().add(new Folder(file.toString(), true)));

        Files.walk(root.getPath(), 1)
                .filter(Files::isRegularFile)
                .forEach(file -> root.getContents().add(DocumentBuilder.getInstance().createDocument(file)));

        return contents;
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