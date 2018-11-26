package directory;

import directory.files.AbstractFile;
import directory.files.Document;
import directory.files.DocumentBuilder;
import directory.files.Folder;
import gui.log.LogEvent;
import gui.log.LogEventType;
import gui.log.LoggingTools;
import json.AppFilesManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Stack;

public class FileManager {

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
        mainFilesRoot = findFiles(mainFilesRootPath);

        // Initialize list of archived documents //
        // todo should these automatic file detection features be enabled? - Magnus
        Path archiveFilesRootPath = Paths.get(Settings.getServerArchivePath());
        archiveRoot = findFiles(archiveFilesRootPath);
    }

    private static Folder findFiles(Path root){
        if (!Files.exists(root)) {
            // todo Check if server connection failure or just non-existing file throw exception maybe
        }
        System.out.println(root.toString());

        if (!Files.isDirectory(root)) {
            throw new IllegalArgumentException("Root file must be a directory");
        }

        // TODO --------- Important ---------- The root folder is initiated with an empty path. If the path is not create folder, upload file etc. will not create files with correct relative path - Magnus
        // TODO This doesn't seem clean but i dunno - Magnus
        return new Folder("", loadChildren(root, root));
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
                        }
                    });
            Files.walk(rootPath, 1)
                    .filter(path1 -> !path1.equals(rootPath))
                    .forEach(newFilePath -> {
                        Path relativePath = basePath.relativize(newFilePath);

                        if(!Files.isDirectory(newFilePath)) {
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
        return mainFilesRoot.getContents();
    }

    public ArrayList<AbstractFile> getArchiveFiles() {
        return archiveRoot.getContents();
    }

    // Uploads a file directly to the root of the main files
    public Document uploadFile(Path src) {
        File file = new File(src.toString());
        Path dest = Paths.get(Settings.getServerDocumentsPath() + file.getName());

        if (Files.exists(dest)) {
            // todo throw file already exists exception - Magnus
            deleteFile(DocumentBuilder.getInstance().createDocument(dest));
        }
        Document newDoc = DocumentBuilder.getInstance().createDocument(src.getFileName());
        getMainFiles().add(newDoc);
        return newDoc;

        //todo if file already exists, the old one is deleted but this can only happen once.
        //todo make some kind of counter to file name
    }

    public Document uploadFile(Path src, Folder dstFolder) {
        File file = new File(src.toString());

        Path dest = Paths.get(Settings.getServerDocumentsPath() + dstFolder.getPath().toString() + File.separator + file.getName());

        if (Files.exists(dest)) {
            // todo should show prompt - Magnus
            deleteFile(DocumentBuilder.getInstance().createDocument(dest));
        }

        try {
            Files.copy(src, dest);
            Document doc = DocumentBuilder.getInstance().createDocument(dest);
            dstFolder.getContents().add(doc);
            AppFilesManager.save(this);
            LoggingTools.log(new LogEvent(file.getName(), LogEventType.CREATED));
            return doc;
        } catch (IOException e) {
            System.out.println("Could not copy/upload file");
            e.printStackTrace();
            // todo Should probably throw new exception.
        }

        // todo add document to files list - Magnus

        //todo if file already exists, the old one is deleted but this can only happen once.
        //todo make some kind of counter to file name
        return null;
    }

    // Creates a folder in the root directory of main files
    public Folder createFolder(String name){
        Folder folder = new Folder(name);

        createFolderFile(Settings.getServerDocumentsPath() + name);

        mainFilesRoot.getContents().add(folder);
        AppFilesManager.save(this);
        return folder;
    }

    // Creates a new folder inside the given parent folder
    public Folder createFolder(String name, Folder parentFolder) {
        Folder folder = new Folder(parentFolder.getPath() + File.separator + name);

        createFolderFile(Settings.getServerDocumentsPath() + folder.getPath());

        parentFolder.getContents().add(folder);
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
        Path archivePath = Paths.get(Settings.getServerArchivePath() + file.getPath());

        // todo maybe change mkdirs - kristian
        try {
            new File(archivePath.getParent().toString()).mkdirs();
            Files.move(Paths.get(Settings.getServerDocumentsPath()+ file.getPath()), archivePath);
            insertFile(file, mainFilesRoot, archiveRoot);
            Optional<Folder> parent = findParent(file, mainFilesRoot.getContents());
            LoggingTools.log(new LogEvent(file.getName(), LogEventType.ARCHIVED));

            if(parent.isPresent())
                parent.get().getContents().remove(file);

            AppFilesManager.save(this);
        } catch (IOException e) {
            System.out.println("Could not delete file");
            e.printStackTrace();
        }
    }

    public String addVersionNumber(AbstractFile file){
        int versionNumber;
        String name1;
        String name2;
        String fileName = file.getName();
        char c = fileName.charAt(fileName.lastIndexOf(".") - 1);

        if (c == ')'){
            String str = fileName.substring(fileName.lastIndexOf("(") + 1,
                    fileName.lastIndexOf(")"));

            versionNumber = Integer.parseInt(str);
            versionNumber++;

            name1 = fileName.substring(
                    0,fileName.lastIndexOf("("));

            name2 = fileName.substring(
                    fileName.lastIndexOf(")") + 1,
                    fileName.length());
        } else {
            versionNumber = 1;

            name1 = fileName.substring(
                    0, fileName.lastIndexOf("."));

            name2 = fileName.substring(
                    fileName.lastIndexOf("."),
                    fileName.length());
        }

        String newFileName = name1 + "(" + versionNumber + ")" + name2;

        return newFileName;
    }

    //todo restore to original path not root folder
    public void restoreFile(AbstractFile file) throws IOException {
        Path pathOrigin = Paths.get(Settings.getServerDocumentsPath() + file.getPath().toString());
        Files.move(Paths.get(Settings.getServerArchivePath() + file.getPath().toString()), pathOrigin);

        insertFile(file, archiveRoot, mainFilesRoot);

        Optional<Folder> rootOptional;// = findParent(file, mainFilesRoot.getContents());
/*
        if(file instanceof Folder)
            rootOptional.ifPresent(parent -> parent.getContents().add(new Folder((Folder)file)));
        else if (file instanceof Document)
            rootOptional.ifPresent(parent -> parent.getContents().add(new Document((Document)file)));
*/
        rootOptional = findParent(file, archiveRoot);
        rootOptional.ifPresent(parent -> parent.getContents().remove(file));

        AppFilesManager.save(this);
    }
    private void insertFile(AbstractFile file, Folder srcRoot, Folder dstRoot) {
        if(file instanceof Document) {
            insertDocument((Document)file, srcRoot, dstRoot);
        }
        else if (file instanceof Folder) {
            insertFolder((Folder) file, srcRoot, dstRoot);
        }
    }
    private void insertFolder(Folder folder, Folder srcRoot, Folder dstRoot) {
        for(AbstractFile file : folder.getContents()) {
            if(file instanceof Document)
                insertDocument((Document)file, srcRoot, dstRoot);
            else if (file instanceof Folder)
                insertFolder((Folder) file, srcRoot, dstRoot);
        }
    }
    private void insertDocument(Document document, Folder src, Folder dst) {
        Stack<Folder> stack = new Stack<>();
        Optional<Folder> optional = findParent(document, src.getContents());
        Folder folderToInsert = dst;
        Folder temp;
        boolean folderExists = false;

        while(optional.isPresent()) {
            stack.push(optional.get());
            optional = findParent(optional.get(), getMainFiles());
        }
        while(!stack.empty()) {
            temp = new Folder(stack.peek().getPath().toString());

            for(AbstractFile file : folderToInsert.getContents()) {
                if(file.getPath().toString().equals(temp.getPath().toString()))
                    folderExists = true;
            }
            if(!folderExists) {
                folderToInsert.getContents().add(temp);
                folderToInsert = temp;
            }
            else
                folderToInsert = searchContentByPath(folderToInsert, temp);

            stack.pop();
            folderExists = false;
        }
        folderToInsert.getContents().add(new Document(document));
    }

    private Folder searchContentByPath(Folder folder, Folder target) {
        for(AbstractFile file : folder.getContents()) {
            if(file.getPath().toString().equals(target.getPath().toString()))
                return (Folder) file;
        }
        return null;
    }
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

    // Saves the current instance to the json file
    public void save() {
        AppFilesManager.save(this);
    }

    // Looks for a File with a path corresponding to the given path
    public Optional<AbstractFile> findInMainFiles(Path fullPath){
        Path basePath = Paths.get(Settings.getServerDocumentsPath());
        Path relativePath = basePath.relativize(fullPath);
        return findFile(relativePath, getMainFiles());
    }

    private Optional<AbstractFile> findFile(Path fileRelativePath, ArrayList<AbstractFile> searchArea){
        for(AbstractFile abstractFile : searchArea){
            Path filePath = abstractFile.getPath();
            if(fileRelativePath.startsWith(filePath)){
                if(fileRelativePath.equals(filePath)){
                    return Optional.of(abstractFile);
                }else if(abstractFile instanceof Folder){
                    return findFile(fileRelativePath, ((Folder) abstractFile).getContents());
                }
            }
        }
        return Optional.empty();
    }
}