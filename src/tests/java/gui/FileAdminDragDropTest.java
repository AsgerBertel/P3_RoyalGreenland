package gui;

import directory.FileManager;
import directory.SettingsManager;
import directory.files.AbstractFile;
import directory.files.Folder;
import directory.plant.AccessModifier;
import directory.plant.Plant;
import directory.plant.PlantManager;
import gui.file_administration.FileAdminController;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;

import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import util.TestUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileAdminDragDropTest extends GUITest {

    private Button uploadDocButton, createFolderButton, deleteFileButton;
    private Button publishButton;
    private TreeView<AbstractFile> fileTree;
    private VBox plantVBox, changesVBox;
    private PlantCheckboxElement plantElement1, plantElement2;

    private FileAdminController fileController;

    @BeforeEach
    void setup(){
        fileController = (FileAdminController) dmsApplication.getCurrentTab().getTabController();
        PlantManager.getInstance().addPlant(new Plant(1001, "Testing Plant 1", new AccessModifier()));
        PlantManager.getInstance().addPlant(new Plant(1002, "Testing Plant 2", new AccessModifier()));
        fileController.update();

        uploadDocButton = findNode("#uploadButton");
        createFolderButton = findNode("#createFolderButton");
        deleteFileButton = findNode("#deleteFileButton");

        publishButton = findNode("#publishChangesButton");
        fileTree = findNode("#filefileTree");

        plantVBox = findNode("#plantVBox");
        changesVBox = findNode("#changesVBox");

        plantElement1 = (PlantCheckboxElement) plantVBox.getChildren().get(0);
        plantElement2 = (PlantCheckboxElement) plantVBox.getChildren().get(1);

        expandTree(fileTree.getRoot());
    }

    void dragRootTest(){

    }

    private static void expandTree(TreeItem<AbstractFile> root){
        root.setExpanded(true);
        for(TreeItem<AbstractFile> child : root.getChildren()){
            expandTree(child);
        }
    }

    @Test
    void simpleDragDropTest() throws InterruptedException {
        assertTrue(TestUtil.doesAbstractFileMatchFileSystem(FileManager.getInstance().getMainFilesRoot(), SettingsManager.getServerDocumentsPath()));
        assertTrue(TestUtil.doesAbstractFileMatchTreeItem(FileManager.getInstance().getMainFilesRoot(),fileTree.getRoot()));

        // Drag folder 1 into folder 0
        TreeItem<AbstractFile> targetItem = fileTree.getRoot().getChildren().get(0);
        TreeItem<AbstractFile> itemToMove = fileTree.getRoot().getChildren().get(1);
        FxRobot fxRobot = drag(getTreeCell(fileTree, itemToMove));
        fxRobot.dropTo(getTreeCell(fileTree, targetItem));

        // Check that the folder has been moved both in the fileTree and in the files list
        assertTrue(containsFile(fileTree.getRoot().getChildren().get(0), itemToMove.getValue()));
        assertTrue(((Folder) targetItem.getValue()).getContents().contains(itemToMove.getValue()));

        assertTrue(TestUtil.doesAbstractFileMatchFileSystem(FileManager.getInstance().getMainFilesRoot(), SettingsManager.getServerDocumentsPath()));
        assertTrue(TestUtil.doesAbstractFileMatchTreeItem(FileManager.getInstance().getMainFilesRoot(),fileTree.getRoot()));
    }

    @Test
    void dragOutsideProgramTest() {

    }

    private boolean containsFile(TreeItem<AbstractFile> root, AbstractFile file){
        for(TreeItem<AbstractFile> child : root.getChildren())
            if(child.getValue().equals(file)) return true;
        return false;
    }

    @Test
    void dragIntoSubfolderTest() throws InterruptedException {
        assertTrue(TestUtil.doesAbstractFileMatchFileSystem(FileManager.getInstance().getMainFilesRoot(), SettingsManager.getServerDocumentsPath()));
        assertTrue(TestUtil.doesAbstractFileMatchTreeItem(FileManager.getInstance().getMainFilesRoot(),fileTree.getRoot()));

        // Drag itemToMove folder into its' first subfolder
        fileTree.getRoot().getChildren().get(0).setExpanded(true);
        Thread.sleep(200);

        TreeItem<AbstractFile> itemToMove = fileTree.getRoot().getChildren().get(0);
        TreeItem<AbstractFile> targetItem = itemToMove.getChildren().get(0);

        FxRobot fxRobot = drag(getTreeCell(fileTree, itemToMove));
        fxRobot.dropTo(getTreeCell(fileTree, targetItem));

        // Check (in both files list and fileTree) that the folder has not been moved
        assertFalse(containsFile(itemToMove, itemToMove.getValue()));
        assertTrue(containsFile(fileTree.getRoot(), itemToMove.getValue()));

        assertTrue(fileTree.getRoot().getChildren().contains(itemToMove));
        assertTrue(itemToMove.getChildren().contains(targetItem));

        // Assert the file list matches the fileTree
        assertTrue(TestUtil.doesAbstractFileMatchFileSystem(FileManager.getInstance().getMainFilesRoot(), SettingsManager.getServerDocumentsPath()));
        assertTrue(TestUtil.doesAbstractFileMatchTreeItem(FileManager.getInstance().getMainFilesRoot(),fileTree.getRoot()));
    }

    private static TreeCell getTreeCell(TreeView<AbstractFile> tree, TreeItem<AbstractFile> treeItem){
        Set<Node> treeCells = tree.lookupAll(".tree-cell");
        List<Node> cells = new ArrayList<>(treeCells);
        int row = tree.getRow(treeItem);
        return ((TreeCell) cells.get(row));
    }
}

