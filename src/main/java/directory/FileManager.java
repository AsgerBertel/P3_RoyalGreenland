package directory;

import directory.files.AbstractFile;
import directory.files.Document;
import directory.files.DocumentBuilder;
import directory.files.Folder;
import gui.log.LogEvent;
import gui.log.LogEventType;
import gui.log.LoggingTools;
import json.AppFilesManager;

import javax.naming.InvalidNameException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.Optional;
import java.util.stream.Collectors;

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

        // Initialize list of archived documents // todo should these automatic file detection features be enabled? - Magnus
        Path archiveFilesRootPath = Paths.get(Settings.getServerArchivePath());
        archiveRoot = findFiles(archiveFilesRootPath);
    }

    private static Folder findFiles(Path root) {
        if (!Files.exists(root)) {
            // todo Check if server connection failure or just non-existing file throw exception maybe
        }

        if (!Files.isDirectory(root)) {
            throw new IllegalArgumentException("Root file must be a directory");
        }

        // TODO --------- Important ---------- The root folder is initiated with an empty path. If the path is not create folder, upload file etc. will not create files with correct relative path - Magnus
        // TODO This doesn't seem clean but i dunno - Magnus
        return new Folder("", loadChildren(root, root));
    }

    /* Find all children of the given root and creates a list of corresponding abstractFile instances with paths
       relative to the base path */
    private static ArrayList<AbstractFile> loadChildren(Path rootPath, Path basePath) {
        ArrayList<AbstractFile> children = new ArrayList<>();

        try { // todo should be rewritten for readability. Use listFiles() instead - Magnus
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
                            // exclude temporary word files
                            String fileName = relativePath.getFileName().toString();
                            if (fileName.startsWith("~"))
                                return;

                            Document document = DocumentBuilder.getInstance().createDocument(relativePath);
                            children.add(document);
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace(); // todo handle properly
        }
        return children;
    }

    public ArrayList<AbstractFile> getMainFiles() {
        return mainFilesRoot.getContents();
    }

    public ArrayList<AbstractFile> getArchiveFiles() {
        return archiveRoot.getContents();
    }

    // Uploads a file directly to the root of the main files
    public Document uploadFile(Path src) {
        return uploadFile(src, this.mainFilesRoot);
    }

    public Document uploadFile(Path src, Folder dstFolder) {
        File file = new File(src.toString());

        Path dest = Paths.get(Settings.getServerDocumentsPath() + dstFolder.getOSPath() + File.separator + src.getFileName());

        if (Files.exists(dest)) {
            // todo should show prompt - Magnus
        }

        try {
            Files.copy(src, dest);

            Path relativePath = Paths.get(Settings.getServerDocumentsPath()).relativize(dest);
            Document doc = DocumentBuilder.getInstance().createDocument(relativePath);

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
    public Folder createFolder(String name) throws IOException, InvalidNameException {
        Folder folder = new Folder(name);
        Path fullPath = Paths.get(Settings.getServerDocumentsPath() + name);
        if (Files.exists(fullPath)) throw new InvalidNameException();

        createFolderFile(Paths.get(Settings.getServerDocumentsPath() + name));

        mainFilesRoot.getContents().add(folder);
        AppFilesManager.save(this);
        return folder;
    }

    // Creates a new folder inside the given parent folder
    public Folder createFolder(String name, Folder parentFolder) throws InvalidNameException, IOException {
        Path fullFolderPath = Paths.get(Settings.getServerDocumentsPath() + parentFolder.getOSPath() + File.separator + name);
        if (Files.exists(fullFolderPath))
            throw new InvalidNameException("Folder with name " + name + " Already exists");
        Folder folder = new Folder(parentFolder.getPath() + File.separator + name);

        createFolderFile(Paths.get(Settings.getServerDocumentsPath() + folder.getOSPath()));

        parentFolder.getContents().add(folder);
        AppFilesManager.save(this);
        return folder;
    }

    private void createFolderFile(Path fullPath) throws IOException {
        Files.createDirectories(fullPath);
    }

    // Removes a file from main files
    public void deleteFile(AbstractFile file) {
        Path originalPath = Paths.get(Settings.getServerDocumentsPath() + file.getOSPath());
        Path archivePath = Paths.get(Settings.getServerArchivePath() + file.getOSPath());

        try {
            // Create parent folders if they don't exist
            new File(archivePath.getParent().toString()).mkdirs();

            if (Files.exists(archivePath)) {
                if (!Files.isDirectory(originalPath)) {
                    // Give the document a new name if it already exists
                    archivePath = generateUniqueFileName(archivePath);
                    file.setName(archivePath.getFileName().toString());
                    Files.move(originalPath, archivePath);
                } else {
                    DirectoryCloner.mergeFolders(originalPath, archivePath, false);
                    DirectoryCloner.deleteFolder(originalPath.toFile());
                }
            } else {
                if (Files.isDirectory(originalPath)) {
                    DirectoryCloner.copyFolder(originalPath, archivePath);
                    DirectoryCloner.deleteFolder(originalPath.toFile());
                } else {
                    Files.move(originalPath, archivePath);
                }
            }

            insertFile(file, mainFilesRoot, archiveRoot);
            LoggingTools.log(new LogEvent(file.getName(), LogEventType.ARCHIVED));
            Optional<Folder> parent = findParent(file, mainFilesRoot);
            parent.ifPresent(parent1 -> parent1.getContents().remove(file));

            AppFilesManager.save(this);
        } catch (IOException e) {
            System.out.println("Could not delete file");
            e.printStackTrace();
        }
    }

    // Append incremental number at the end of file name to
    public static Path generateUniqueFileName(Path filePath) {
        Path parent = filePath.getParent();
        String extension = getExtension(filePath);
        String fileName = filePath.getName(filePath.getNameCount() - 1).toString().replace(extension, "");

        Path newUniquePath = filePath;
        int appendedNumber = 0;
        while (Files.exists(newUniquePath)) {
            appendedNumber++;

            // If the file name already has a number appended to it then replace the current number with the next one
            if (fileName.endsWith(")") && Character.isDigit(fileName.charAt(fileName.length() - 2))) {
                String currentNumberString = fileName.substring(fileName.lastIndexOf('('), fileName.lastIndexOf(')') + 1);
                fileName = fileName.replace(currentNumberString, "(" + appendedNumber + ")");
            } else {// Otherwise just append the number to the filename
                fileName += "(" + appendedNumber + ")";
            }
            newUniquePath = parent.resolve(fileName + extension);
        }

        return newUniquePath;
    }

    /**
     * @return an empty string if the file has no extension
     */
    private static String getExtension(Path filePath) {
        String pathString = filePath.toString();
        int indexOfSeperator = pathString.lastIndexOf('.');
        if(indexOfSeperator == -1) return "";

        String extension = pathString.substring(indexOfSeperator, pathString.length());
        if (extension.contains("/") || extension.contains(File.separator))
            return "";

        return extension;
    }

    //todo restore to original path not root folder
    public void restoreFile(AbstractFile file) throws IOException {
        // Move the file on the file system
        Path newPath = Paths.get(Settings.getServerDocumentsPath() + file.getOSPath().toString());
        Path oldPath = Paths.get(Settings.getServerArchivePath() + file.getOSPath().toString());

        // Create parent folders if they don't exist
        if (!Files.exists(newPath.getParent()))
            Files.createDirectories(newPath.getParent());

        if (!Files.exists(newPath)) {
            Files.move(oldPath, newPath);
        } else {
            // Find new name if it's a document and it already exists
            if (Files.isDirectory(oldPath)) {
                DirectoryCloner.mergeFolders(oldPath, newPath, false);
                DirectoryCloner.deleteFolder(oldPath.toFile());
            } else {
                newPath = generateUniqueFileName(newPath);
                Files.move(oldPath, newPath);
                file.setName(newPath.getFileName().toString());
            }
        }

        // Insert the file into the main files list
        insertFile(file, archiveRoot, mainFilesRoot);
        LoggingTools.log(new LogEvent(file.getName(), LogEventType.RESTORED));

        // Remove the folder from the archive files list
        Optional<Folder> parent = findParent(file, archiveRoot);
        parent.ifPresent(parent1 -> parent1.getContents().remove(file));
        removeEmptyFolders(archiveRoot);
        deleteEmptyDirectories(archiveRoot);

        AppFilesManager.save(this);
    }

    private void insertFile(AbstractFile file, Folder srcRoot, Folder dstRoot) {
        if (file instanceof Document) {
            insertDocument((Document) file, srcRoot, dstRoot);
        } else if (file instanceof Folder) {
            insertFolder((Folder) file, srcRoot, dstRoot);
        }
    }

    private void insertFolder(Folder folder, Folder srcRoot, Folder dstRoot) {
        for (AbstractFile file : folder.getContents()) {
            if (file instanceof Document)
                insertDocument((Document) file, srcRoot, dstRoot);
            else if (file instanceof Folder)
                insertFolder((Folder) file, srcRoot, dstRoot);
        }
    }

    private void insertDocument(Document document, Folder src, Folder dst) {
        Stack<Folder> stack = new Stack<>();
        Optional<Folder> parent = findParent(document, src);
        Folder folderToInsert = dst;
        Folder temp;
        boolean folderExists = false;

        while (parent.isPresent()) {
            stack.push(parent.get());
            parent = findParent(parent.get(), src);
        }
        while (!stack.empty()) {
            temp = new Folder(stack.peek().getPath().toString());

            if (folderToInsert.getPath().toString().equals(temp.getPath().toString()))
                folderExists = true;
            else {
                for (AbstractFile file : folderToInsert.getContents()) {
                    if (file.getPath().toString().equals(temp.getPath().toString()))
                        folderExists = true;
                }
            }
            if (!folderExists) {
                folderToInsert.getContents().add(temp);
                folderToInsert = temp;
            } else {
                for (AbstractFile file : folderToInsert.getContents()) {
                    if (file.getPath().toString().equals(temp.getPath().toString()))
                        folderToInsert = (Folder) file;
                }
            }
            stack.pop();
            folderExists = false;
        }
        if (folderToInsert != null)
            folderToInsert.getContents().add(new Document(document));
    }

    private void removeEmptyFolders(Folder src) {

        src.getContents().removeIf(e -> e instanceof Folder && isEmpty((Folder) e));
    }

    private boolean isEmpty(Folder src) {
        if (src.getContents().size() == 0)
            return true;

        src.getContents().removeIf(e -> e instanceof Folder && isEmpty((Folder) e));

        return src.getContents().size() == 0;
    }

    private void deleteEmptyDirectories(Folder src) {
        boolean allDeleted = false;
        boolean hasDeleted;
        List<Path> pathsToDelete = new ArrayList<>();
        System.out.println("hej");
        System.out.println(Settings.getServerArchivePath() + src.getOSPath());

        try {
            while (!allDeleted) {
                hasDeleted = false;
                pathsToDelete = Files.walk(Paths.get(Settings.getServerArchivePath() + src.getOSPath()))
                        .filter(path -> !path.equals(Paths.get(Settings.getServerArchivePath() + src.getOSPath())))
                        .filter(path -> Files.isDirectory(path))
                        .filter(path -> new File(path.toString()).list().length <= 0)
                        .collect(Collectors.toList());
                if (pathsToDelete.size() > 0) {
                    pathsToDelete.forEach(path -> new File(path.toString()).delete());
                    pathsToDelete.clear();
                } else
                    allDeleted = true;
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    public static Optional<Folder> findParent(AbstractFile child, Folder root) {
        Optional<Folder> parent = Optional.empty();
        String str = root.getPath().toString() + " og " + child.getParentPath();

        if (root.getPath().equals(child.getParentPath()))
            return Optional.of(root);

        for (AbstractFile current : root.getContents()) {
            if (current instanceof Folder) {
                parent = findParent(child, (Folder) current);
                if (parent.isPresent())
                    break;
            }
        }
        return parent;
    }

    // Saves the current instance to the json file
    public void save() {
        AppFilesManager.save(this);
    }

    public Folder getMainFilesRoot() {
        return mainFilesRoot;
    }

    public Folder getArchiveRoot() {
        return archiveRoot;
    }

    // Looks for a File with a path corresponding to the given path
    public Optional<AbstractFile> findInMainFiles(Path fullPath) {
        Path basePath = Paths.get(Settings.getServerDocumentsPath());
        Path relativePath = basePath.relativize(fullPath);
        return findFile(relativePath, getMainFiles());
    }

    private Optional<AbstractFile> findFile(Path fileRelativePath, ArrayList<AbstractFile> searchArea) {
        for (AbstractFile abstractFile : searchArea) {
            Path filePath = abstractFile.getOSPath();
            if (fileRelativePath.startsWith(filePath)) {
                if (fileRelativePath.equals(filePath)) {
                    return Optional.of(abstractFile);
                } else if (abstractFile instanceof Folder) {
                    return findFile(fileRelativePath, ((Folder) abstractFile).getContents());
                }
            }
        }
        return Optional.empty();
    }

    public void moveFile(AbstractFile srcFile, Folder dstParent) throws IOException {
        Path dstPath = Paths.get(Settings.getServerDocumentsPath() + dstParent.getOSPath() + File.separator + srcFile.getName());

        // Don't move if the target is the same as the destination
        Optional<Folder> parent = findParent(srcFile, getMainFilesRoot());
        if(parent.isPresent() && parent.get().equals(dstParent))
            return; // todo probably throw exception? - Magnus

        if (srcFile instanceof Folder && Files.exists(dstPath) && Files.isDirectory(dstPath)) {
            Folder existingFolder = (Folder) findInMainFiles(dstPath).get();
            mergeFolders((Folder) srcFile, existingFolder);
        } else {
            // Move and generate new name if a file with the same name already exists in the dst folder
            safeMove(srcFile, dstParent);
        }
    }

    // Move a src and generates a new name for it if another src with the same name already exists in the dst src
    private void safeMove(AbstractFile src, Folder newParentFolder) throws IOException {
        Path srcPath = Paths.get(Settings.getServerDocumentsPath() + src.getOSPath());
        Path dstPath = Paths.get(Settings.getServerDocumentsPath() + newParentFolder.getOSPath() + File.separator + src.getName());

        // Move src in the main files list
        Optional<Folder> srcParent = findParent(src, getMainFilesRoot());
        if (srcParent.isPresent()) {
            srcParent.get().getContents().remove(src);
            newParentFolder.getContents().add(src);
        } else {
            throw new RuntimeException("The parent of the original src cannot be found");
        }

        // Generate new unique name for the src and add it to the new parent src
        dstPath = generateUniqueFileName(dstPath);
        try {
            renameFile(src, dstPath.getFileName().toString());
        } catch (InvalidNameException e) {
            e.printStackTrace();
            throw new RuntimeException("The generated name : " + dstPath.getFileName() + " could not be applied to " + src.getOSPath());
        }

        src.setPath(Paths.get(newParentFolder.getPath() + File.separator + src.getName()));
        Files.move(srcPath, dstPath);
    }

    private void mergeFolders(Folder src, Folder dst) throws IOException {
        ArrayList<AbstractFile> children = src.getContents();
        for (AbstractFile child : children) {
            Path childDst = Paths.get(Settings.getServerDocumentsPath() + dst.getOSPath() + File.separator + child.getName());

            if (child instanceof Document || !Files.exists(childDst)) {
                safeMove(child, dst);
            } else {
                AbstractFile existingFile = findInMainFiles(childDst).get();
                if (existingFile instanceof Document) {
                    // If a document with the same name exists rename the folder
                    safeMove(child, findParent(dst, getMainFilesRoot()).get());
                } else {
                    mergeFolders((Folder) child, (Folder) existingFile);
                }
            }
        }
    }

    public boolean renameFile(AbstractFile file, String newName) throws InvalidNameException {
        if(file.getName().equals(newName)) return true;
        Path oldPath = Paths.get(Settings.getServerDocumentsPath() + file.getOSPath().toString());
        Path newPath = oldPath.getParent().resolve(newName);

        if (Files.exists(newPath))
            throw new InvalidNameException("Name is already in use");

        if (oldPath.toFile().renameTo(newPath.toFile())) {
            if (file instanceof Folder) {
                Folder fol = (Folder) file;
                fol.setName(newName);

                AppFilesManager.save(FileManager.getInstance());
                LoggingTools.log(new LogEvent(fol.getName(), LogEventType.FOLDER_RENAMED));
            }else if (file instanceof Document) {
                Document doc = (Document) file;
                doc.setName(newName);

                AppFilesManager.save(FileManager.getInstance());
                LoggingTools.log(new LogEvent(doc.getName(), LogEventType.RENAMED));
            }

            return true;
        }
        return false;
    }
}