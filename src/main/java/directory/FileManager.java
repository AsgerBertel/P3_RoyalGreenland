package directory;

import directory.files.AbstractFile;
import directory.files.Document;
import directory.files.DocumentBuilder;
import directory.files.Folder;
import json.AppFilesManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;

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
        // Create a list of AbstractFiles based on the files inside the server document path
        Path mainFilesRootPath = Paths.get(Settings.getServerDocumentsPath());
        mainFiles = findFiles(mainFilesRootPath);

        // Initialize list of archived documents // todo should these automatic file detection features be enabled? - Magnus
        Path archiveFilesRoot = Paths.get(Settings.getServerArchivePath());
        archiveFiles = findFiles(archiveFilesRoot);
    }

    private static ArrayList<AbstractFile> findFiles(Path root){
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

    public void uploadFile(Path src) throws IOException {
        File file = new File(src.toString());
        Path dest = Paths.get(Settings.getServerDocumentsPath() + file.getName());

        if (Files.exists(dest)) {
            deleteFile(DocumentBuilder.getInstance().createDocument(dest));
        }

        //todo if file already exists, the old one is deleted but this can only happen once.
        //todo make some kind of counter to file name
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
            AppFilesManager.save(this);
            //loggingTools.LogEvent(file.getName(), LogEventType.CREATED); // todo reimplement
        } catch (IOException e) {
            System.out.println("Could not copy/upload file");
            e.printStackTrace();
        } // todo Error handling.

        //todo if file already exists, the old one is deleted but this can only happen once.
        //todo make some kind of counter to file name
    }

    // Creates a folder in the root directory of main files
    public Folder createFolder(String name){
        Folder folder = new Folder(name);

        createFolderFile(Settings.getServerDocumentsPath() + name);

        mainFiles.add(folder);
        AppFilesManager.save(this);
        return folder;
    }

    // Creates a new folder inside the given parent folder
    public Folder createFolder(String name, Folder parentFolder) {
        Folder folder = new Folder(parentFolder.getPath() + "/" + name);

        createFolderFile(Settings.getServerDocumentsPath() + folder.getPath());

        mainFiles.add(folder);
        AppFilesManager.save(this);
        return folder;
    }

    private void createFolderFile(String fullPath){
        boolean isSuccessful = new File(fullPath).mkdirs();

        if (!isSuccessful) {
            System.out.println("mkdirs was not successful");
            // Todo should probably throw an exception - Magnus
        }
    }

    public void deleteFile(AbstractFile file) {
        Path pathWithName = Paths.get(Paths.get(Settings.getServerArchivePath()) + File.separator + file.getName());
        try {
            Files.move(file.getPath(), pathWithName);

            //Remove the file from its' parent if it has one
            Optional<Folder> parent = findParent(file, getMainFiles());
            if(parent.isPresent())
                parent.get().getContents().remove(file);


            Folder archiveFolder = (Folder) getInstance().archiveFiles.get(0); // todo Implement properly (archive files no longer have a root)
            archiveFolder.getContents().add(file);
            AppFilesManager.save(this);
        } catch (IOException e) {
            System.out.println("Could not delete file");
            e.printStackTrace();
        }
    }

    //todo restore to original path not root folder
    public void restoreFile(AbstractFile file) throws IOException {
        Path pathWithName = Paths.get(Paths.get(Settings.getServerArchivePath()) + File.separator + file.getName());

        Files.move(pathWithName, Paths.get(Settings.getServerDocumentsPath() + File.separator + file.getName()));

        Folder archiveFolder = (Folder) getInstance().archiveFiles.get(0); // todo Implement properly (archive files no longer have a root)
        archiveFolder.getContents().remove(file);
        Folder contentFolder = (Folder) getInstance().mainFiles.get(0); // todo Implement properly (archive files no longer have a root) - Magnus
        contentFolder.getContents().add(file);

        AppFilesManager.save(this);
    }

    /**
     * Searches the filesystem and creates Abstractfile objects based on the children of the given root folder
     *
     * @return
     * @throws IOException
     */

    public static Optional<Folder> findParent(AbstractFile child, ArrayList<AbstractFile> searchArea) {
        for(AbstractFile file : searchArea){
            // If the file is in the top layer of the search area it has no parent
            if(file.equals(child))
                return Optional.empty();

            // Check if any of the subdirectories is the parent folder
            if(file instanceof Folder){
                Optional<Folder> parent = findParent(child, (Folder) file);
                if(parent.isPresent())
                    return parent;
            }
        }

        return Optional.empty();
    }

    private static Optional<Folder> findParent(AbstractFile child, Folder parent) {
        if (parent.getContents().contains(child))
            return Optional.of(parent);

        for (AbstractFile current : parent.getContents()) {
            if (current instanceof Folder) {
                Optional<Folder> folder = findParent(child, (Folder) current);
                if (folder != null)
                    return folder;
            }
        }
        return Optional.empty();
    }

}