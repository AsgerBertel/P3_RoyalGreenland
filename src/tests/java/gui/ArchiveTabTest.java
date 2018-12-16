package gui;

import model.managing.FileManager;
import model.managing.SettingsManager;
import model.AbstractFile;
import model.managing.PlantManager;
import controller.ArchiveController;
import controller.FileAdminController;
import javafx.scene.Node;
import javafx.scene.control.*;
import io.json.AppFilesManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.TestUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;


class ArchiveTabTest extends GUITest {

    private ArchiveController fileController;
    private TreeView<AbstractFile> fileTree;

    @BeforeEach
    void setup() throws IOException{
        resetFiles();
        clickOn((ToggleButton)findNode("#archiveButton"));
        fileController = (ArchiveController) dmsApplication.getCurrentTab().getTabController();
    }

    private void resetFiles() throws IOException{
        SettingsManager.setServerPath(TestUtil.getTestServerDocuments());
        SettingsManager.setLocalPath(TestUtil.getTestLocalDocuments());

        TestUtil.resetTestFiles();
        FileManager.resetInstance();
        PlantManager.resetInstance();

        AppFilesManager.createServerDirectories();
        AppFilesManager.createLocalDirectories();
    }

    @Test
    void testRestore(){
        clickOn((ToggleButton)findNode("#administrateDocumentsButton"));
        FileAdminController adminController = (FileAdminController) dmsApplication.getCurrentTab().getTabController();

        //Find a click a folder
        fileTree = findNode("#fileTreeView");
        TreeItem<AbstractFile> selectedItem = fileTree.getRoot().getChildren().get(0);
        String itemToDeleteName = selectedItem.getValue().getName();
        clickOn(getTreeCell(fileTree, selectedItem));
        clickOn((Button)findNode("#deleteFileButton"));

        // Go back to archive
        clickOn((ToggleButton)findNode("#archiveButton"));


        // Click file in archive and restore
        fileTree = findNode("#Filetree");
        selectedItem = fileTree.getRoot().getChildren().get(0);
        assertEquals(selectedItem.getValue().getName(), itemToDeleteName);
        clickOn(getTreeCell(fileTree, selectedItem));
        clickOn((Button)findNode("#btnRestore"));

        // Go back to adminfiles
        clickOn((ToggleButton)findNode("#administrateDocumentsButton"));
        fileTree = findNode("#fileTreeView");
        selectedItem = fileTree.getRoot().getChildren().get(0);
        assertEquals(selectedItem.getValue().getName(), itemToDeleteName);
    }

    private static TreeCell<AbstractFile> getTreeCell(TreeView<AbstractFile> tree, TreeItem<AbstractFile> treeItem){
        Set<Node> treeCells = tree.lookupAll(".tree-cell");
        List<Node> cells = new ArrayList<>(treeCells);
        int row = tree.getRow(treeItem);
        return ((TreeCell<AbstractFile>) cells.get(row));
    }
}
