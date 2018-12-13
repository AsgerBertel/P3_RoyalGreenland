package util;

import directory.files.AbstractFile;
import directory.files.Folder;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

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

    public static void resetTestFiles() throws IOException { // Todo actually make test files that are independent of actual files
        Path oldServerFolder = TEST_SERVER_PATH.resolve(APPLICATION_FOLDER_NAME);
        Path oldLocalFolder = TEST_LOCAL_PATH.resolve(APPLICATION_FOLDER_NAME);
        Path replacementFolder = TEST_SERVER_PATH.resolve(REPLACEMENT_FOLDER_NAME);

        if (Files.exists(oldServerFolder) && oldServerFolder.toString().contains(APPLICATION_FOLDER_NAME)){
            FileUtils.deleteDirectory(oldServerFolder.toFile());
        }

        if (Files.exists(oldLocalFolder) && oldLocalFolder.toString().contains(APPLICATION_FOLDER_NAME)){
            FileUtils.deleteDirectory(oldLocalFolder.toFile());
        }

        FileUtils.copyDirectory(replacementFolder.toFile(), oldServerFolder.toFile());
    }

    public static final Path getTestServerDocuments() {
        return TEST_SERVER_PATH.resolve(APPLICATION_FOLDER_NAME);
    }

    public static final Path getTestLocalDocuments(){
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
                if(!doesAbstractFileMatchTreeItem(child, childTreeItem))
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
