package directory;

import directory.files.AbstractFile;
import directory.files.Document;
import directory.files.DocumentBuilder;
import directory.files.Folder;
import gui.log.LogEventType;
import gui.log.LoggingTools;
import json.AppFilesManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class FileManager {
    private ArrayList<AbstractFile> mainFiles = new ArrayList<>();
    private ArrayList<AbstractFile> archiveFiles = new ArrayList<>();

    private Folder mainFilesRoot;
    private Folder archiveRoot;

    private static FileManager fileManager;

    public static synchronized FileManager getInstance() {
        if (fileManager == null) {
            // Attempt to load file list from the server
            fileManager = AppFilesManager.loadFileManager();

            // If still null create new FileManager and save it on the server
            if (fileManager == null) {
                fileManager = new FileManager();
                AppFilesManager.save(fileManager);
            }
        }
        return fileManager;
    }

    // Private constructor for ensuring that no other class can create a new instance this class
    private FileManager() {
        Settings settings = Settings.getInstance();

        // Create a list of AbstractFiles based on the files inside the server document path
        Path mainFilesRootPath = Paths.get(settings.getServerDocumentsPath());
        mainFiles = loadAbstractFiles(mainFilesRootPath);

        // Initialize list of archived documents // todo should these automatic file detection features be enabled? - Magnus
        Path archiveFilesRoot = Paths.get(settings.getServerArchivePath());
        archiveFiles = loadAbstractFiles(archiveFilesRoot);
    }

    private static ArrayList<AbstractFile> loadAbstractFiles(Path root){
        if (!Files.exists(root)) {
            // todo Check if server connection failure or just non-existing file throw exception maybe
        }

        if (!Files.isDirectory(root)) {
            throw new IllegalArgumentException("Root file must be a directory");
        }

        return loadChildren(root, root);
    }

    /* Find all children of the given root and creates a list of corresponding abstractFile instances with paths
       relative to the base path */
    private static ArrayList<AbstractFile> loadChildren(Path rootPath, Path basePath){
        ArrayList<AbstractFile> children = new ArrayList<>();

        try {
            // Iterate through all files within the rootFolder and add them to the files list
            Files.walk(rootPath, 1)
                    .filter(path -> !path.equals(rootPath))
                    .forEach(newFilePath -> {
                        Path relativePath = basePath.relativize(newFilePath);

                        if (Files.isDirectory(newFilePath)) {
                            Folder folder = new Folder(relativePath.toString());
                            // Recursive call to load all child elements of this folder
                            folder.getContents().addAll(loadChildren(newFilePath, basePath));
                            children.add(folder);
                        } else {
                            Document document = DocumentBuilder.getInstance().createDocument(relativePath);
                            children.add(document);
                        }
                    });

        } catch (IOException e) {
            e.printStackTrace(); // todo handle properly
        }
        return children;
    }
/*
    //todo temporary
    public static synchronized FileManager getTestInstance() {
        if (fileManager == null) {
            fileManager = new FileManager();
        }
        return fileManager;
    }*/

    public ArrayList<AbstractFile> getMainFiles() {
        return mainFiles;
    }

    public ArrayList<AbstractFile> getArchiveFiles() {
        return archiveFiles;
    }

    public void uploadFile(Path src, Folder dstFolder) throws IOException {
        File file = new File(src.toString());

        Path dest = Paths.get(dstFolder.getPath().toString() + File.separator + file.getName());

        if (Files.exists(dest)) {
            deleteFile(DocumentBuilder.getInstance().createDocument(dest));
        }

        try {
            Files.copy(src, dest);
            Document doc = DocumentBuilder.getInstance().createDocument(dest);
            dstFolder.getContents().add(doc);
            updateJsonFiles();
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

        if (!isSuccessful) {
            System.out.println("mkdirs was not successful");
            return null;
        }

        parentFolder.getContents().add(createdFolder);
        updateJsonFiles();
        return createdFolder;
    }

    public void deleteFile(AbstractFile file) {
        Path pathWithName = Paths.get(Paths.get(Settings.getInstance().getServerArchivePath()) + File.separator + file.getName());
        try {
            Files.move(file.getPath(), pathWithName);
            Folder parent = findParent(file);
            parent.getContents().remove(file);

            Folder archiveFolder = (Folder) getInstance().archiveFiles.get(0);
            archiveFolder.getContents().add(file);
            getInstance().updateJsonFiles();
        } catch (IOException e) {
            System.out.println("Could not delete file");
            e.printStackTrace();
        }
    }

    //todo restore to original path not root folder
    public void restoreFile(AbstractFile file) throws IOException {
        Path pathWithName = Paths.get(Paths.get(Settings.getInstance().getServerArchivePath()) + File.separator + file.getName());

        Files.move(pathWithName, Paths.get(Settings.getInstance().getServerDocumentsPath() + File.separator + file.getName()));

        Folder archiveFolder = (Folder) getInstance().archiveFiles.get(0);
        archiveFolder.getContents().remove(file);
        Folder contentFolder = (Folder) getInstance().mainFiles.get(0);
        contentFolder.getContents().add(file);

        updateJsonFiles();
    }

    public void updateJsonFiles() {
        // Write object to JSON file.
        AppFilesManager.save(this);
        /*try (FileWriter writer = new FileWriter(Settings.getInstance().getServerAppFilesPath() + "allFiles.JSON")) {
            JsonParser.getJsonParser().toJson(getInstance(), writer);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
    }

    /**
     * Searches the filesystem and creates Abstractfile objects based on the children of the given root folder
     *
     * @return
     * @throws IOException
     */


    private Folder getRootElement() {
        return (Folder) getInstance().mainFiles.get(0);
        //todo get root of what? documents or archiveFiles? Specify or remove method - Magnus
    }

    // todo same as above. Parent in archiveFiles or main documents?
    public Folder findParent(AbstractFile child) {
        return findParent(child, getRootElement());

    }

    private Folder findParent(AbstractFile child, Folder parent) {
        if (parent.getContents().contains(child)) {
            return parent;
        }

        for (AbstractFile current : parent.getContents()) {
            if (current instanceof Folder) {
                Folder folder = findParent(child, (Folder) current);
                if (folder != null) {
                    return folder;
                }
            }
        }
        return null;
    }

}