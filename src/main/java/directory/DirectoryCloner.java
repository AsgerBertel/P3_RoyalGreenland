package directory;

import directory.files.AbstractFile;
import directory.files.Document;
import directory.files.Folder;
import gui.DMSApplication;
import json.AppFilesManager;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;

public class DirectoryCloner {

    public static void main(String[] args) throws Exception {
        mergeFolders(Paths.get("C:\\Users\\Magnus\\Desktop\\Test\\MergeFolder"),Paths.get("C:\\Users\\Magnus\\Desktop\\MergeFolder"), true);
    }

    public static void publishFiles() throws Exception {

        FileManager fileManager = AppFilesManager.loadFileManager();
        if (fileManager == null)
            return;

        ArrayList<AbstractFile> newFiles = fileManager.getMainFiles();
        ArrayList<AbstractFile> oldFiles = AppFilesManager.loadPublishedFileList();
        applyUpdate(oldFiles, newFiles, SettingsManager.getPublishedDocumentsPath(), SettingsManager.getServerDocumentsPath());

        // Replace app files
        replaceIfExists(SettingsManager.getServerAppFilesPath().resolve(AppFilesManager.FILES_LIST_FILE_NAME), SettingsManager.getPublishedAppFilesPath().resolve(AppFilesManager.FILES_LIST_FILE_NAME));
        //todo remove //
        //replaceIfExists(SettingsManager.getServerAppFilesPath().resolve(AppFilesManager.FACTORY_LIST_FILE_NAME), SettingsManager.getPublishedAppFilesPath().resolve(AppFilesManager.FACTORY_LIST_FILE_NAME));
    }

    public static void updateLocalFiles() throws IOException {
        if(!Files.exists(SettingsManager.getLocalAppFilesPath()) || !Files.exists(SettingsManager.getLocalFilesPath()))
            AppFilesManager.createLocalDirectories();

        if(!isUpdateAvailable())
            return;

        ArrayList<AbstractFile> newFiles = AppFilesManager.loadPublishedFileList();
        ArrayList<AbstractFile> oldFiles = AppFilesManager.loadLocalFileList();
        applyUpdate(oldFiles, newFiles, SettingsManager.getLocalFilesPath(), SettingsManager.getServerDocumentsPath());

        // Replace app files
        replaceIfExists(SettingsManager.getPublishedAppFilesPath().resolve(AppFilesManager.FILES_LIST_FILE_NAME), SettingsManager.getLocalAppFilesPath().resolve(AppFilesManager.FILES_LIST_FILE_NAME));
        //Todo remove //
        //replaceIfExists(SettingsManager.getPublishedAppFilesPath().resolve(AppFilesManager.FACTORY_LIST_FILE_NAME), SettingsManager.getLocalAppFilesPath().resolve(AppFilesManager.FACTORY_LIST_FILE_NAME));
    }

    private static boolean isUpdateAvailable() throws IOException {
        Path publishedFilesList = SettingsManager.getPublishedAppFilesPath().resolve(AppFilesManager.FILES_LIST_FILE_NAME);
        Path publishedPlantList = SettingsManager.getPublishedAppFilesPath().resolve(AppFilesManager.FACTORY_LIST_FILE_NAME);

        if(!Files.exists(SettingsManager.getServerPath()))
            throw new ServerUnavailableException();

        if(!Files.exists(publishedFilesList) || !Files.exists(publishedPlantList))
            return false;

        File localFilesList = SettingsManager.getLocalAppFilesPath().resolve(AppFilesManager.FILES_LIST_FILE_NAME).toFile();
        File localFactoryList = SettingsManager.getLocalAppFilesPath().resolve(AppFilesManager.FACTORY_LIST_FILE_NAME).toFile();
        File serverFilesList = SettingsManager.getServerAppFilesPath().resolve(AppFilesManager.FILES_LIST_FILE_NAME).toFile();
        File serverFactoryList = SettingsManager.getServerAppFilesPath().resolve(AppFilesManager.FACTORY_LIST_FILE_NAME).toFile();

        if(!localFactoryList.exists() || !localFilesList.exists())
            return true;

        try {
            if(!FileUtils.contentEquals(localFilesList, serverFilesList) || !FileUtils.contentEquals(localFactoryList, serverFactoryList))
                return true;
            else
                return false;
        } catch (IOException e) {
            throw new IOException("Unable to compare local files to server files", e);
        }
    }

    /** Updates the oldFiles list to match the updatedFiles list and updates the corresponding files on the file system */
    private static void applyUpdate(ArrayList<AbstractFile> oldFiles, ArrayList<AbstractFile> updatedFiles,
                                    Path oldFilesPath, Path updatedFilesPath) throws IOException {
        // Remove files that are no longer up to date
        oldFiles = removeOutdatedFiles(oldFiles, updatedFiles, oldFilesPath);
        // Add any files that have been deleted
        addNewFiles(oldFiles, updatedFiles, oldFilesPath, updatedFilesPath);
    }

    /**
     * Compares oldFiles to updatedFiles. Finds all files in oldFiles that are changed or deleted.
     * @return a list of files from oldFiles that are changed or no longer exists in new files.
     */
    private static ArrayList<AbstractFile> findOutdatedFiles(ArrayList<AbstractFile> oldFiles, ArrayList<AbstractFile> updatedFiles){
        ArrayList<AbstractFile> filesToDelete = new ArrayList<>();
        for (AbstractFile file : oldFiles) {
            if (file instanceof Document) {
                // Documents can be compared with equals therefore we can use .contains()
                if (!updatedFiles.contains(file))
                    filesToDelete.add(file);
            } else if (file instanceof Folder) {
                // Folder's .equals() implementation also compares children which is not relevant here.
                // Therefore a custom contains() is used.
                if (!containsFolderWithPath(updatedFiles, file.getOSPath()))
                    filesToDelete.add(file);
            }
        }
        return filesToDelete;
    }

    private static ArrayList<AbstractFile> deleteFilesFrom(ArrayList<AbstractFile> allFiles, ArrayList<AbstractFile> filesToDelete, Path rootPath) throws IOException {
        ArrayList<AbstractFile> newFilesList = new ArrayList<>(allFiles);
        for (AbstractFile fileToDelete : filesToDelete) {
            Path fileToDeletePath = rootPath.resolve(fileToDelete.getOSPath());
            boolean success;

            // Fail safe to make sure only files inside the application folder are deleted
            if(!fileToDeletePath.toString().contains(DMSApplication.APP_TITLE))
                throw new IOException("Attempted to delete file that is not inside the DMS Application folder. File : ");

            // Remove file from disk
            if(fileToDelete instanceof Folder){
                success = deleteFolder(fileToDeletePath.toFile());
            }else{
                success = Files.deleteIfExists(fileToDeletePath);
            }

            if (!success)
                throw new IOException("Could not delete file " + fileToDelete.getOSPath() + " from " + rootPath.toString());
            // A custom .remove() is used as the folders .equals() does not fit this use case
            removeFileWithPath(newFilesList, fileToDelete.getOSPath());
        }
        return newFilesList;
    }

    /**
     * Compares oldFiles to newFiles and removes files from oldFiles that have been updated or deleted.
     * @return the files that does not need to be updated (the intersection of oldFiles and newFiles)
     */
    public static ArrayList<AbstractFile> removeOutdatedFiles(ArrayList<AbstractFile> oldFiles, ArrayList<AbstractFile> newFiles, Path oldFilesRoot) throws IOException {
        ArrayList<AbstractFile> modifiedOldFiles = new ArrayList<>(oldFiles);

        ArrayList<AbstractFile> filesToDelete = findOutdatedFiles(oldFiles, newFiles);
        modifiedOldFiles = deleteFilesFrom(modifiedOldFiles, filesToDelete, oldFilesRoot);
        // ModifiedOldFiles should now be the intersection between oldFiles and newFiles

        // Recursively repeat procedure on any sub folders that are common between new and old
        for (AbstractFile oldFile : modifiedOldFiles) {
            if (oldFile instanceof Folder) {
                // Find matching folder in newFiles
                Optional<AbstractFile> newFolder = getFileByPath(newFiles, oldFile.getOSPath());

                if (newFolder.isPresent() && newFolder.get() instanceof Folder) {
                    Folder oldFolder = ((Folder) oldFile);
                    ArrayList<AbstractFile> newContents = removeOutdatedFiles(
                            ((Folder) oldFile).getContents(),
                            ((Folder) newFolder.get()).getContents(),
                            oldFilesRoot);
                    oldFolder.getContents().clear();
                    oldFolder.getContents().addAll(newContents);
                }
            }
        }
        return modifiedOldFiles;
    }

    public static ArrayList<AbstractFile> addNewFiles(ArrayList<AbstractFile> oldFiles, ArrayList<AbstractFile> newFiles, Path oldFilesRoot, Path newFileRoot) throws IOException {
        ArrayList<AbstractFile> modifiedOldFiles = new ArrayList<>();
        modifiedOldFiles.addAll(oldFiles);

        ArrayList<AbstractFile> filesToAdd = findMissingFiles(oldFiles, newFiles);

        // Run procedure for any subfolder
        for (AbstractFile oldFile : oldFiles) {
            if (oldFile instanceof Folder) {
                Folder oldFolder = (Folder) oldFile;

                Optional<AbstractFile> newFolder = getFileByPath(newFiles, oldFolder.getOSPath());

                if (newFolder.isPresent() && newFolder.get() instanceof Folder) {
                    ArrayList<AbstractFile> newChildren = addNewFiles(
                            oldFolder.getContents(),
                            ((Folder) newFolder.get()).getContents(),
                            oldFilesRoot,
                            newFileRoot);
                    oldFolder.getContents().clear();
                    oldFolder.getContents().addAll(newChildren);
                }
            }
        }

        // Add the new files
        for (AbstractFile addedFile : filesToAdd) {
            Path publishPath = (oldFilesRoot.resolve(addedFile.getOSPath()));

            // Fail safe
            if(!publishPath.toString().contains(DMSApplication.APP_TITLE))
                throw new IOException("Attempted to delete file that is not inside the DMS Application folder. File : " + publishPath);

            if (Files.exists(publishPath)) {
                if (addedFile instanceof Folder) {
                    deleteFolder(publishPath.toFile());
                } else {
                    Files.delete(publishPath);
                }
            }

            if (addedFile instanceof Folder) {
                copyFolder(newFileRoot.resolve(addedFile.getOSPath()), publishPath);
            } else {
                Files.copy(newFileRoot.resolve(addedFile.getOSPath()), publishPath);
            }

            modifiedOldFiles.add(addedFile);
        }

        return modifiedOldFiles;
    }


    public static ArrayList<AbstractFile> findMissingFiles(ArrayList<AbstractFile> originalFiles, ArrayList<AbstractFile> updatedFiles){
        ArrayList<AbstractFile> missingFiles = new ArrayList<>();
        // Find files that should be added
        for (AbstractFile file : updatedFiles) {
            if (file instanceof Document) {
                // If the old files doesn't have this file or if the lastUpdateDate does not match
                if (!originalFiles.contains(file))
                    missingFiles.add(file);
            } else {
                if (!containsFolderWithPath(originalFiles, file.getOSPath()))
                    missingFiles.add(file);
            }
        }
        return missingFiles;
    }

    private static boolean containsFolderWithPath(ArrayList<AbstractFile> files, Path path) {
        for (AbstractFile file : files) {
            if (file.getOSPath().equals(path))
                return true;
        }
        return false;
    }

    private static Optional<AbstractFile> getFileByPath(ArrayList<AbstractFile> files, Path path) {
        for (AbstractFile file : files) {
            if (file.getOSPath().equals(path))
                return Optional.of(file);
        }
        return Optional.empty();
    }

    private static boolean removeFileWithPath(ArrayList<AbstractFile> files, Path path) {
        for (AbstractFile file : files) {
            if (file.getOSPath().equals(path)) {
                files.remove(file);
                return true;
            }
        }
        return false;
    }

    public static void copyFolder(Path src, Path dst) throws IOException {
        Files.createDirectories(dst);
        File[] fileToCopy = src.toFile().listFiles();
        if(fileToCopy == null) return;
        for(File file : fileToCopy){
            if(file.isDirectory()){
                copyFolder(src.resolve(file.getName()), dst.resolve(file.getName()));
            }else{
                Files.copy(file.toPath(), dst.resolve(file.getName()));
            }
        }
    }

    /**
     * @param src the folder from which data is copied
     * @param dst the destination folder
     * @param replace if true the method will overwrite any duplicate files with the files from src. If false a new name
     *                will be generated for the src file.
     */
    // Merges src folder into dst folder.
    public static void mergeFolders(Path src, Path dst, boolean replace) throws IOException {
        // Create folder in the new path in case it doesn't exist
        Files.createDirectories(dst);
        File[] fileToCopy = src.toFile().listFiles();
        if(fileToCopy == null) return;
        for(File originalFile : fileToCopy){
            Path newFilePath = dst.resolve(originalFile.getName());

            if(!newFilePath.toString().contains(DMSApplication.APP_TITLE))
                throw new IOException("Attempted to delete file that is not inside the DMS Application folder. File : ");

            if(originalFile.isDirectory()){
                if(Files.exists(newFilePath)){
                    mergeFolders(originalFile.toPath(), newFilePath, replace);
                }else{
                    copyFolder(originalFile.toPath(), newFilePath);
                }
            }else{
                if(Files.exists(newFilePath)){
                    if(replace){
                        Files.delete(newFilePath);
                        Files.copy(originalFile.toPath(), newFilePath);
                    }else{
                        // Generate new unique name for the file by appending a number to the end
                        newFilePath = FileManager.generateUniqueFileName(newFilePath);
                        Files.copy(originalFile.toPath(), newFilePath);
                    }
                }else{
                    Files.copy(originalFile.toPath(), dst.resolve(originalFile.getName()));
                }

            }
        }
    }


    public static boolean deleteFolder(File folder) {
        boolean success = true;
        File[] files = folder.listFiles();
        if (files != null) { //some JVMs return null for empty dirs
            for (File f : files) {
                if (f.isDirectory()) {
                    success &= deleteFolder(f);
                } else {
                    success &= f.delete();
                }
            }
        }
        return folder.delete() && success;
    }

    private static void replaceIfExists(Path src, Path dst) throws IOException {
        if (!Files.exists(src))
            throw new IllegalArgumentException("Given src file does not exist");

        if(!dst.toString().contains(DMSApplication.APP_TITLE))
            throw new IOException("Attempted to delete file that is not inside the DMS Application folder. File : ");

        if (Files.exists(dst))
            Files.delete(dst);

        Files.copy(src, dst);
    }


}
