package io.update;

import model.managing.FileManager;
import model.managing.SettingsManager;
import model.AbstractFile;
import model.Document;
import model.Folder;
import gui.AlertBuilder;
import app.DMSApplication;
import log.LoggingErrorTools;
import io.json.AppFilesManager;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Optional;

public class DirectoryCloner {

    // Clones files from working directory into the published file
    public static void publishFiles() throws UpdateFailException {
        FileManager fileManager = AppFilesManager.loadFileManager();
        if (fileManager == null)
            return;

        ArrayList<AbstractFile> newFiles = fileManager.getMainFiles();
        ArrayList<AbstractFile> oldFiles = AppFilesManager.loadPublishedFileList();

        try {
            applyUpdate(oldFiles, newFiles,
                    SettingsManager.getPublishedDocumentsPath(),
                    SettingsManager.getServerDocumentsPath());

            // Replace app files
            replaceIfExists(SettingsManager.getServerAppFilesPath().resolve(AppFilesManager.FILES_LIST_FILE_NAME),
                    SettingsManager.getPublishedAppFilesPath().resolve(AppFilesManager.FILES_LIST_FILE_NAME));
            replaceIfExists(SettingsManager.getServerAppFilesPath().resolve(AppFilesManager.FACTORY_LIST_FILE_NAME),
                    SettingsManager.getPublishedAppFilesPath().resolve(AppFilesManager.FACTORY_LIST_FILE_NAME));
        } catch (IOException | IllegalFileException e) {
            e.printStackTrace();
            throw new UpdateFailException(e);
        }
    }

    // Clones files from published files into the local files if the server is available
    public static void updateLocalFiles() throws UpdateFailException {
        try {
            if (!Files.exists(SettingsManager.getLocalAppFilesPath()) || !Files.exists(SettingsManager.getLocalFilesPath()))
                AppFilesManager.createLocalDirectories();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            LoggingErrorTools.log(e, 4);
            AlertBuilder.customErrorPopUp("Error",
                    "Local io not found",
                    "Local directories were not present before update" +
                            "Error logged in: " + SettingsManager.getLocalErrorLogsPath());
        } catch (IOException e) {
            e.printStackTrace();
            LoggingErrorTools.log(e);
            AlertBuilder.IOExceptionPopUp();
        }

        try {
            // Check if update is available
            if (!isUpdateAvailable())
                return;

            ArrayList<AbstractFile> newFiles = AppFilesManager.loadPublishedFileList();
            ArrayList<AbstractFile> oldFiles = AppFilesManager.loadLocalFileList();
            applyUpdate(oldFiles, newFiles, SettingsManager.getLocalFilesPath(), SettingsManager.getServerDocumentsPath());

            // Replace application files
            replaceIfExists(SettingsManager.getPublishedAppFilesPath().resolve(AppFilesManager.FILES_LIST_FILE_NAME),
                    SettingsManager.getLocalAppFilesPath().resolve(AppFilesManager.FILES_LIST_FILE_NAME));
            replaceIfExists(SettingsManager.getPublishedAppFilesPath().resolve(AppFilesManager.FACTORY_LIST_FILE_NAME),
                    SettingsManager.getLocalAppFilesPath().resolve(AppFilesManager.FACTORY_LIST_FILE_NAME));

            AppFilesManager.saveLocalUpdateTime();
        }catch (IOException | IllegalFileException e) {
            e.printStackTrace();
            throw new UpdateFailException(e);
        }
    }

    /**
     * @return true if an update is available.
     * @throws IOException                if unable to compare directories due to I/O error.
     * @throws ServerUnavailableException if unable to find server path.
     */
    private static boolean isUpdateAvailable() throws IOException {
        Path publishedFilesList = SettingsManager.getPublishedAppFilesPath().resolve(AppFilesManager.FILES_LIST_FILE_NAME);
        Path publishedPlantList = SettingsManager.getPublishedAppFilesPath().resolve(AppFilesManager.FACTORY_LIST_FILE_NAME);

        if (!Files.exists(SettingsManager.getServerPath()))
            return false;

        if (!Files.exists(publishedFilesList) || !Files.exists(publishedPlantList))
            return false;

        File localFilesList = SettingsManager.getLocalAppFilesPath().resolve(AppFilesManager.FILES_LIST_FILE_NAME).toFile();
        File localFactoryList = SettingsManager.getLocalAppFilesPath().resolve(AppFilesManager.FACTORY_LIST_FILE_NAME).toFile();
        File serverFilesList = SettingsManager.getServerAppFilesPath().resolve(AppFilesManager.FILES_LIST_FILE_NAME).toFile();
        File serverFactoryList = SettingsManager.getServerAppFilesPath().resolve(AppFilesManager.FACTORY_LIST_FILE_NAME).toFile();

        if (!localFactoryList.exists() || !localFilesList.exists())
            return true;

        try {
            return !FileUtils.contentEquals(localFilesList, serverFilesList)
                    || !FileUtils.contentEquals(localFactoryList, serverFactoryList);
        } catch (IOException e) {
            throw new IOException("Unable to compare local io to server io", e);
        }
    }

    /**
     * Updates the oldFiles list to match the updatedFiles list and updates the corresponding io on the file system
     *
     * @throws IOException          removeOutDatedFiles throws IOException
     * @throws IllegalFileException if a file which does not contain the DMSApplication.APP_TITLE is included in the update.
     */
    private static void applyUpdate(ArrayList<AbstractFile> oldFiles,
                                    ArrayList<AbstractFile> updatedFiles,
                                    Path oldFilesPath,
                                    Path updatedFilesPath)
            throws IOException, IllegalFileException {
        // Remove io that are no longer up to date
        oldFiles = removeOutdatedFiles(oldFiles, updatedFiles, oldFilesPath);
        // Add any io that have been deleted
        addNewFiles(oldFiles, updatedFiles, oldFilesPath, updatedFilesPath);
    }

    /**
     * Compares oldFiles to newFiles and removes io from oldFiles that have been updated or deleted.
     *
     * @return the io that does not need to be updated (the intersection of oldFiles and newFiles)
     * @throws IOException if
     */
    private static ArrayList<AbstractFile> removeOutdatedFiles(ArrayList<AbstractFile> oldFiles,
                                                               ArrayList<AbstractFile> newFiles,
                                                               Path oldFilesRoot)
            throws IOException, IllegalFileException {
        ArrayList<AbstractFile> modifiedOldFiles = new ArrayList<>(oldFiles);

        // Find and delete outdated files
        ArrayList<AbstractFile> filesToDelete = findOutdatedFiles(oldFiles, newFiles);
        modifiedOldFiles = deleteFilesFrom(modifiedOldFiles, filesToDelete, oldFilesRoot);

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

    /**
     * Compares oldFiles to updatedFiles. Finds all io in oldFiles that are changed or deleted.
     *
     * @return a list of io from oldFiles that are changed or no longer exists in new io.
     */
    private static ArrayList<AbstractFile> findOutdatedFiles(ArrayList<AbstractFile> oldFiles,
                                                             ArrayList<AbstractFile> updatedFiles) {
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

    /**
     * @param allFiles      ArrayList of the io to delete from
     * @param filesToDelete ArrayList of io to delete
     * @param rootPath      path from resolve from
     * @return an ArrayList without the io from filesToDelete paramater.
     * @throws FileNotFoundException optional more specific IOException.
     *                               Caused by org.apache.commons.io.FileUtils throwing FileNotFoundException.
     * @throws IOException           deletedFolder or Files.deleteIfExists is unsuccesful.
     * @throws IllegalFileException  if a file is not included in the DMSApplication domain,
     *                               specifically if DMSApplication.APP_TITLE is not included in the file path.
     */
    private static ArrayList<AbstractFile> deleteFilesFrom(ArrayList<AbstractFile> allFiles,
                                                           ArrayList<AbstractFile> filesToDelete,
                                                           Path rootPath)
            throws IOException, IllegalFileException {
        ArrayList<AbstractFile> newFilesList = new ArrayList<>(allFiles);
        for (AbstractFile fileToDelete : filesToDelete) {
            Path fileToDeletePath = rootPath.resolve(fileToDelete.getOSPath());
            boolean success;

            // Fail safe to make sure only io inside the application folder are deleted
            if (!fileToDeletePath.toString().contains(DMSApplication.APP_TITLE))
                throw new IllegalFileException("Attempted to delete file that is not inside the DMS Application folder. File : ");

            // Remove file from disk
            if (fileToDelete instanceof Folder) {
                success = deleteFolder(fileToDeletePath.toFile());
            } else {
                success = Files.deleteIfExists(fileToDeletePath);
            }
            FileUtils.deleteDirectory(fileToDeletePath.toFile());

            if (!success)
                throw new IOException("Could not delete file " + fileToDelete.getOSPath() + " from " + rootPath.toString());
            // A custom .remove() is used as the folders .equals() does not fit this use case
            removeFileWithPath(newFilesList, fileToDelete.getOSPath());
        }
        return newFilesList;
    }

    private static ArrayList<AbstractFile> addNewFiles(ArrayList<AbstractFile> oldFiles,
                                                       ArrayList<AbstractFile> newFiles,
                                                       Path oldFilesRoot,
                                                       Path newFileRoot)
            throws IOException {
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

        // Add the new io
        for (AbstractFile addedFile : filesToAdd) {
            Path publishPath = (oldFilesRoot.resolve(addedFile.getOSPath()));

            if (Files.exists(publishPath)) {
                if (addedFile instanceof Folder) {
                    deleteFolder(publishPath.toFile());
                } else {
                    try {
                        Files.delete(publishPath);
                    } catch (IOException e) {
                        throw new IOException("Could not delete file: " + publishPath.toString() + " during update", e);
                    }
                }
            }
            try {
                if (addedFile instanceof Folder) {
                    FileUtils.copyDirectory(newFileRoot.resolve(addedFile.getOSPath()).toFile(), publishPath.toFile());
                } else {
                    Files.copy(newFileRoot.resolve(addedFile.getOSPath()), publishPath);
                }
            } catch (IOException e) {
                throw new IOException(e);
            }
            modifiedOldFiles.add(addedFile);
        }

        return modifiedOldFiles;
    }

    /**
     * @return a list of files in updatedFiles that are missing from originalFiles
     * @throws IllegalFileException if a file is outside of the application's domain.
     */
    private static ArrayList<AbstractFile> findMissingFiles(ArrayList<AbstractFile> originalFiles,
                                                            ArrayList<AbstractFile> updatedFiles) {
        ArrayList<AbstractFile> missingFiles = new ArrayList<>();
        // Find io that should be added
        for (AbstractFile file : updatedFiles) {
            if (file instanceof Document) {
                // If the old io doesn't have this file or if the lastUpdateDate does not match
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

    /**
     * @param src     the folder from which data is copied
     * @param dst     the destination folder
     * @param replace if true the method will overwrite any duplicate io with the io from src. If false a new name
     *                will be generated for the src file.
     * @throws IllegalFileException if the folder to merge is not within the application's domain.
     */
    // Merges src folder into dst folder.
    public static void mergeFolders(Path src, Path dst, boolean replace) throws IllegalFileException {
        // Create folder in the new path in case it doesn't exist
        createDirectories(dst);
        File[] fileToCopy = src.toFile().listFiles();
        if (fileToCopy == null) return;
        for (File originalFile : fileToCopy) {
            Path newFilePath = dst.resolve(originalFile.getName());

            if (!newFilePath.toString().contains(DMSApplication.APP_TITLE))
                throw new IllegalFileException(
                        "Attempted to delete file that is not inside the DMS Application folder. File : ");

            if (originalFile.isDirectory()) {
                if (Files.exists(newFilePath)) {
                    mergeFolders(originalFile.toPath(), newFilePath, replace);
                } else {
                    try {
                        FileUtils.copyDirectory(originalFile.toPath().toFile(), newFilePath.toFile());
                    } catch (IOException e) {
                        e.printStackTrace();
                        LoggingErrorTools.log(e);
                        AlertBuilder.IOExceptionPopUpWithString(
                                originalFile.toString() + "\n" + newFilePath.toString());
                    }
                }
            } else {
                try {
                    if (Files.exists(newFilePath)) {
                        if (replace) {
                            Files.delete(newFilePath);
                            Files.copy(originalFile.toPath(), newFilePath);
                        } else {
                            // Generate new unique name for the file by appending a number to the end
                            newFilePath = FileManager.generateUniqueFileName(newFilePath);
                            Files.copy(originalFile.toPath(), newFilePath);
                        }
                    } else {
                        Files.copy(originalFile.toPath(), dst.resolve(originalFile.getName()));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    AlertBuilder.IOExceptionPopUpWithString(originalFile.toString() + "\n" + newFilePath);
                    LoggingErrorTools.log(e);
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

    /**
     * Method to replace a path with another, deleting the file at the destination path if it exists.
     *
     * @param src path to source.
     * @param dst path to destination.
     * @throws FileNotFoundException optional more specific IOException if the src file does not exist.
     * @throws IOException           if Files.delete(dst) fails to delete dst path, incase this already exists.
     * @throws IllegalFileException  if the destination path does not include DMSApplication.APP_TITLE.
     */
    private static void replaceIfExists(Path src, Path dst) throws IOException, IllegalFileException {
        if (!Files.exists(src))
            throw new FileNotFoundException("Given src file does not exist");

        if (!dst.toString().contains(DMSApplication.APP_TITLE))
            throw new IllegalFileException(
                    "Attempted to delete file that is not inside the DMS Application folder. File : " + dst.toString());

        if (Files.exists(dst)) {
            try {
                Files.delete(dst);
            } catch (IOException e) {
                throw new IOException("Could not delete file: " + dst.toString(), e);
            }
        }
        try {
            Files.copy(src, dst);
        } catch (IOException e) {
            throw new IOException("Could not copy file: " + src.toString(), e);
        }
    }

    private static void createDirectories(Path dst) {
        try {
            Files.createDirectories(dst);
        } catch (IOException e) {
            e.printStackTrace();
            AlertBuilder.IOExceptionPopUpWithString(dst.toString());
            LoggingErrorTools.log(e);
        }
    }
}
