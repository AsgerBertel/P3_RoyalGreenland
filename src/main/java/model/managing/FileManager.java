package model.managing;

import model.AbstractFile;
import model.Document;
import model.DocumentBuilder;
import model.Folder;
import io.update.DirectoryCloner;
import io.update.IllegalFileException;
import gui.AlertBuilder;
import log.LogEvent;
import log.LogEventType;
import log.LoggingErrorTools;
import log.LogManager;
import io.json.AppFilesManager;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
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
                fileManager = new FileManager(true);
                AppFilesManager.save(fileManager);
            }
        }
        return fileManager;
    }

    public static void resetInstance(){
        fileManager = null;
    }

    private FileManager() {
    }

    // Private constructor for ensuring that no other class can create a new instance this class
    private FileManager(boolean generateTree) {
        // Create a list of AbstractFiles based on the io inside the server document path
        Path mainFilesRootPath = SettingsManager.getServerDocumentsPath();

        try {
            mainFilesRoot = findFiles(mainFilesRootPath);
        } catch (FileNotFoundException e) {
            AlertBuilder.fileNotFoundPopUp();
        }

        Path archiveFilesRootPath = SettingsManager.getServerArchivePath();
        try {
            archiveRoot = findFiles(archiveFilesRootPath);
        } catch (FileNotFoundException e) {
            AlertBuilder.fileNotFoundPopUp();
        }
    }

    private static Folder findFiles(Path root) throws FileNotFoundException {
        if (!Files.exists(root)) {
            throw new FileNotFoundException();
        }

        if (!Files.isDirectory(root)) {
            throw new IllegalArgumentException("Root file must be a io");
        }

        return new Folder("", loadChildren(root, root));
    }

    /* Find all children of the given root and creates a list of corresponding abstractFile instances with paths
       relative to the base path */
    private static ArrayList<AbstractFile> loadChildren(Path rootPath, Path basePath) {
        ArrayList<AbstractFile> children = new ArrayList<>();

        try {
            // Iterate through all io within the rootFolder and add them to the io list
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
                            // exclude temporary word io
                            String fileName = relativePath.getFileName().toString();
                            if (fileName.startsWith("~"))
                                return;

                            Document document = DocumentBuilder.getInstance().createDocument(relativePath);
                            children.add(document);
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
            AlertBuilder.IOExceptionPopUp();
            LoggingErrorTools.log(e);
        }

        return children;
    }

    public ArrayList<AbstractFile> getMainFiles() {
        return mainFilesRoot.getContents();
    }

    public ArrayList<AbstractFile> getArchiveFiles() {
        return archiveRoot.getContents();
    }

    // Uploads a file directly to the root of the main io
    public Document uploadFile(Path src) {
        return uploadFile(src, this.mainFilesRoot);
    }

    public Document uploadFile(Path src, Folder dstFolder) {
        File file = new File(src.toString());
        Path dest = SettingsManager.getServerDocumentsPath().resolve(dstFolder.getOSPath().resolve(src.getFileName()));

        if(Files.exists(dest))
            dest = generateUniqueFileName(dest);

        try {
            Files.copy(src, dest);

            Path relativePath = SettingsManager.getServerDocumentsPath().relativize(dest);
            Document doc = DocumentBuilder.getInstance().createDocument(relativePath);

            dstFolder.getContents().add(doc);
            AppFilesManager.save(this);
            LogManager.log(new LogEvent(file.getName(), LogEventType.CREATED));
            return doc;
        } catch (IOException e) {
            e.printStackTrace();
            AlertBuilder.IOExceptionPopUp();
            LoggingErrorTools.log(e);
        }
        return null;
    }

    public boolean fileExists(Path fullPath){
        return findInMainFiles(fullPath).isPresent();
    }

    // Creates a folder in the root io of main io
    public Folder createFolder(String name) throws IOException {
        Folder folder = new Folder(name);
        Path fullPath = SettingsManager.getServerDocumentsPath().resolve(name);
        if (Files.exists(fullPath))
            throw new FileAlreadyExistsException("File " + fullPath.toString() + " already exists.");

        createFolderFile(SettingsManager.getServerDocumentsPath().resolve(name));

        mainFilesRoot.getContents().add(folder);
        AppFilesManager.save(this);
        return folder;
    }

    // Creates a new folder inside the given parent folder
    public Folder createFolder(String name, Folder parentFolder) throws IOException {
        Path fullFolderPath = SettingsManager.getServerDocumentsPath().resolve(parentFolder.getOSPath()).resolve(name);
        if (Files.exists(fullFolderPath))
            throw new FileAlreadyExistsException("Folder with name" + fullFolderPath + " already exists.");
        Folder folder = new Folder(parentFolder.getPath().resolve(name).toString());

        createFolderFile(SettingsManager.getServerDocumentsPath().resolve(folder.getOSPath()));

        parentFolder.getContents().add(folder);
        AppFilesManager.save(this);
        return folder;
    }

    private void createFolderFile(Path fullPath) throws IOException {
        Files.createDirectories(fullPath);
    }

    // Removes a file from main io
    public void deleteFile(AbstractFile file) {
        Path originalPath = SettingsManager.getServerDocumentsPath().resolve(file.getOSPath());
        Path archivePath = SettingsManager.getServerArchivePath().resolve(file.getOSPath());

        try {
            // Create parent folders if they don't exist
            new File(archivePath.getParent().toString()).mkdirs();

            if(Files.exists(archivePath)) {
                if(!Files.isDirectory(originalPath)) {
                    // Give the document a new name if it already exists
                    archivePath = generateUniqueFileName(archivePath);
                    file.setName(archivePath.getFileName().toString());
                    Files.move(originalPath, archivePath);
                } else {
                    DirectoryCloner.mergeFolders(originalPath, archivePath, false);
                    DirectoryCloner.deleteFolder(originalPath.toFile());
                }
            } else {
                if(Files.isDirectory(originalPath)) {
                    FileUtils.copyDirectory(originalPath.toFile(),archivePath.toFile());
                    DirectoryCloner.deleteFolder(originalPath.toFile());
                } else {
                    Files.move(originalPath, archivePath);
                }
            }

            insertFile(file, mainFilesRoot, archiveRoot);
            LogManager.log(new LogEvent(file.getName(), LogEventType.ARCHIVED));
            Optional<Folder> parent = findParent(file, mainFilesRoot);
            parent.ifPresent(parent1 -> parent1.getContents().remove(file));

            AppFilesManager.save(this);

        } catch (FileNotFoundException e) {
            AlertBuilder.fileNotFoundPopUp();
        } catch (IllegalFileException e) {
            AlertBuilder.illegalFileExceptionPopUp(originalPath.toString());
        } catch (IOException e) {
            AlertBuilder.IOExceptionPopUp();
        } catch (Exception e) {
            e.printStackTrace();
            LoggingErrorTools.log(e);
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
        int indexOfSeparator = pathString.lastIndexOf('.');
        if(indexOfSeparator == -1) return "";

        String extension = pathString.substring(indexOfSeparator, pathString.length());
        if (extension.contains("/") || extension.contains(File.separator))
            return "";

        return extension;
    }

    public void restoreFile(AbstractFile file) throws IOException {
        // Move the file on the file system
        Path newPath = SettingsManager.getServerDocumentsPath().resolve(file.getOSPath());
        Path oldPath = SettingsManager.getServerArchivePath().resolve(file.getOSPath());

        // Create parent folders if they don't exist
        if (!Files.exists(newPath.getParent()))
            Files.createDirectories(newPath.getParent());

        if (!Files.exists(newPath)) {
            Files.move(oldPath, newPath);
        } else {
            // Find new name if it's a document and it already exists
            if (Files.isDirectory(oldPath)) {
                try {
                    DirectoryCloner.mergeFolders(oldPath, newPath, false);
                } catch(IllegalFileException e) {
                    e.printStackTrace();
                    AlertBuilder.illegalFileExceptionPopUp(oldPath.toString());
                    LoggingErrorTools.log(e);
                }
                DirectoryCloner.deleteFolder(oldPath.toFile());
            } else {
                newPath = generateUniqueFileName(newPath);
                Files.move(oldPath, newPath);
                file.setName(newPath.getFileName().toString());
            }
        }

        // Insert the file into the main io list
        insertFile(file, archiveRoot, mainFilesRoot);
        LogManager.log(new LogEvent(file.getName(), LogEventType.RESTORED));

        // Remove the folder from the archive io list
        Optional<Folder> parent = findParent(file, archiveRoot);
        if(parent.isPresent()){
            parent.get().getContents().remove(file);
        }
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
        List<Path> pathsToDelete;
        boolean allDeleted = false;

        try {
            while (!allDeleted) {
                pathsToDelete = Files.walk(SettingsManager.getServerArchivePath().resolve(src.getOSPath()))
                        .filter(path -> !path.equals(SettingsManager.getServerArchivePath().resolve(src.getOSPath())))
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
            AlertBuilder.IOExceptionPopUp();
            LoggingErrorTools.log(e);
        }
    }

    public static Optional<Folder> findParent(AbstractFile child, Folder root) {
        Optional<Folder> parent = Optional.empty();

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

    // Saves the current instance to the io.json file
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
        Path basePath = SettingsManager.getServerDocumentsPath();
        Path relativePath = basePath.relativize(fullPath);
        return findFile(relativePath, getMainFiles());
    }

    public Optional<AbstractFile> findFile(Path fileRelativePath, ArrayList<AbstractFile> searchArea) {
        for (AbstractFile abstractFile : searchArea) {
            Path filePath = abstractFile.getOSPath();
            if (fileRelativePath.startsWith(filePath)) {
                if (fileRelativePath.equals(filePath))
                    return Optional.of(abstractFile);
                else if (abstractFile instanceof Folder)
                    return findFile(fileRelativePath, ((Folder) abstractFile).getContents());

            }
        }
        return Optional.empty();
    }

    public void moveFile(AbstractFile srcFile, Folder dstParent) throws IOException {
        Path dstPath = SettingsManager.getServerDocumentsPath().resolve(dstParent.getOSPath()).resolve(srcFile.getName());

        // Don't move if the target is the same as the destination
        Optional<Folder> parent = findParent(srcFile, getMainFilesRoot());
        if (parent.isPresent() && parent.get().equals(dstParent)) {
            // Alerts user if file already exists
            AlertBuilder.fileAlreadyExistsPopUp();
            return;
        }

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
        Path srcPath = SettingsManager.getServerDocumentsPath().resolve(src.getOSPath());
        Path dstPath = SettingsManager.getServerDocumentsPath().resolve(newParentFolder.getOSPath()).resolve(src.getName());

        // Move src in the main io list
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
        } catch (FileAlreadyExistsException e) {
            e.printStackTrace();
            AlertBuilder.fileAlreadyExistsPopUp();
            throw new RuntimeException("The generated name : " + dstPath.getFileName() + " could not be applied to " + src.getOSPath());
        }

        src.setPath(newParentFolder.getPath().resolve(src.getName()));
        Files.move(srcPath, dstPath);
    }

    private void mergeFolders(Folder src, Folder dst) throws IOException {
        ArrayList<AbstractFile> children = src.getContents();
        ArrayList<AbstractFile> copyChildren = new ArrayList<>();
        copyChildren.addAll(children);
        for (AbstractFile child : copyChildren) {
            Path childDst = SettingsManager.getServerDocumentsPath().resolve(dst.getOSPath()).resolve(child.getName());

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
        findParent(src, mainFilesRoot).get().getContents().remove(src);

        Path fileToDelete = SettingsManager.getServerDocumentsPath().resolve(src.getOSPath());
        // Fail-safe
        if(!fileToDelete.toString().contains(SettingsManager.APPLICATION_FOLDER_NAME))
            throw new IOException("Attempted to delete file that is not inside the DMS Application folder. File : " + fileToDelete);

        Files.delete(fileToDelete);
    }

    public void renameFile(AbstractFile file, String newName) throws FileAlreadyExistsException {
        if (file.getName().equals(newName)) return;
        if (file.getOSPath().toString().equals("")) return;
        Path oldPath = SettingsManager.getServerDocumentsPath().resolve(file.getOSPath().toString());
        Path newPath = oldPath.getParent().resolve(newName);

        if (Files.exists(newPath))
            throw new FileAlreadyExistsException("Name is already in use");


        if (oldPath.toFile().renameTo(newPath.toFile())) {
            if (file instanceof Folder) {
                Folder fol = (Folder) file;
                fol.setName(newName);

                AppFilesManager.save(FileManager.getInstance());
                LogManager.log(new LogEvent(fol.getName(), LogEventType.FOLDER_RENAMED));
            } else if (file instanceof Document) {
                Document doc = (Document) file;
                doc.setName(newName);

                AppFilesManager.save(FileManager.getInstance());
                LogManager.log(new LogEvent(doc.getName(), LogEventType.RENAMED));
            }
        }
    }
}