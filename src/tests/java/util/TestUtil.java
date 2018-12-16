package util;

import directory.FileManager;
import directory.SettingsManager;
import directory.files.AbstractFile;
import directory.files.Folder;
import directory.plant.PlantManager;
import gui.DMSApplication;
import gui.Tab;
import gui.file_administration.FileAdminController;
import gui.log.LogManager;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import json.AppFilesManager;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.opentest4j.AssertionFailedError;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import static org.apache.commons.io.FileUtils.listFiles;

public class TestUtil {

    private static final Path TEST_SERVER_PATH = Paths.get("TestingFiles/Server/");
    private static final Path TEST_LOCAL_PATH = Paths.get("TestingFiles/Local/");
    private static final String APPLICATION_FOLDER_NAME = "RG DMS";
    private static final String REPLACEMENT_FOLDER_NAME = APPLICATION_FOLDER_NAME + " Original";

    private TestUtil() {

    }

    public static void main(String[] args) throws IOException {
        resetTestFiles();
    }

    public static void resetTestFiles() throws IOException {
        Path oldServerFolder = TEST_SERVER_PATH.resolve(APPLICATION_FOLDER_NAME);
        Path oldLocalFolder = TEST_LOCAL_PATH.resolve(APPLICATION_FOLDER_NAME);
        Path replacementFolder = TEST_SERVER_PATH.resolve(REPLACEMENT_FOLDER_NAME);

        FileAdminController fileController = (FileAdminController) Tab.FILE_ADMINISTRATION.getTabController();
        if (fileController != null)
            fileController.stopWatchThread();
        DMSApplication.getDMSApplication().stopWatcherThreads();

        attemptDeletions(oldServerFolder);
        attemptDeletions(oldLocalFolder);

        FileUtils.copyDirectory(replacementFolder.toFile(), oldServerFolder.toFile());
        while (!areDirsEqual(oldServerFolder.toFile(), replacementFolder.toFile())) {
            try {
                System.out.println("sleeping while copying");
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        AppFilesManager.createServerDirectories();
        AppFilesManager.createLocalDirectories();

        FileManager.resetInstance();
        PlantManager.resetInstance();

        FileManager.getInstance();
        PlantManager.getInstance();

        if (fileController != null)
            fileController.startWatchThread();
        DMSApplication.getDMSApplication().startWatcherThreads();
    }

    private static void attemptDeletions(Path path) {
        int maxAttempts = 20;
        int currentAttempts = 0;
        while (Files.exists(path) && path.toString().contains(APPLICATION_FOLDER_NAME) && currentAttempts < maxAttempts) {
            currentAttempts++;
            try {
                FileUtils.deleteDirectory(path.toFile());
            } catch (IOException e) {
                System.out.println("Attempted to delete file " + path.toString() + " but failed");
                if (currentAttempts == maxAttempts)
                    e.printStackTrace();
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public static boolean areDirsEqual(File dir1, File dir2) {
        ArrayList<File> dir1List = new ArrayList<>(FileUtils.listFiles(dir1, null, true));
        ArrayList<File> dir2List = new ArrayList<>(FileUtils.listFiles(dir2, null, true));

        if (dir1List.size() != dir2List.size())
            return false;

        for (int i = 0; i < dir1List.size(); i++) {
            if (!dir1List.get(i).getName().equals(dir2List.get(i).getName()))
                return false;
        }

        return true;
    }

    public static final Path getTestServerDocuments() {
        return TEST_SERVER_PATH.resolve(APPLICATION_FOLDER_NAME);
    }

    public static final Path getTestLocalDocuments() {
        return TEST_LOCAL_PATH.resolve(APPLICATION_FOLDER_NAME);
    }

    /**
     * Checks if the given file (and its' children) match the treeItem (and its' children)
     */
    public static boolean doesAbstractFileMatchTreeItem(AbstractFile file, TreeItem<AbstractFile> treeItem) {
        if (!file.equals(treeItem.getValue()))
            return false;

        if (file instanceof Folder) {
            for (AbstractFile child : ((Folder) file).getContents()) {
                TreeItem<AbstractFile> childTreeItem = findMatchingTreeItem(treeItem.getChildren(), child);
                if (!doesAbstractFileMatchTreeItem(child, childTreeItem))
                    return false;
            }
        }

        return true;
    }

    private static TreeItem<AbstractFile> findMatchingTreeItem(ObservableList<TreeItem<AbstractFile>> treeItems, AbstractFile file) {
        for (TreeItem<AbstractFile> treeItem : treeItems) {
            if (treeItem.getValue().equals(file)) return treeItem;
        }
        return null;
    }

    /**
     * Checks if the given file (and its' children) exist on the file system
     */
    public static boolean doesAbstractFileMatchFileSystem(AbstractFile file, Path basePath) {
        if (!Files.exists(basePath.resolve(file.getOSPath())))
            return false;

        if (file instanceof Folder) {
            for (AbstractFile child : ((Folder) file).getContents())
                if (!doesAbstractFileMatchFileSystem(child, basePath))
                    return false;
        }

        return true;
    }


}
