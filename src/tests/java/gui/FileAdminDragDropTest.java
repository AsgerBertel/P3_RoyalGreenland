package gui;

import directory.FileManager;
import directory.SettingsManager;
import directory.files.AbstractFile;
import directory.files.Folder;
import directory.plant.AccessModifier;
import directory.plant.Plant;
import directory.plant.PlantManager;
import gui.file_administration.FileAdminController;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;

import javafx.scene.control.TreeView;
import javafx.scene.layout.VBox;
import json.AppFilesManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import util.TestUtil;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FileAdminDragDropTest extends GUITest {

    private Button uploadDocButton, createFolderButton, deleteFileButton;
    private Button publishButton;
    private TreeView<AbstractFile> fileTree;
    private VBox plantVBox, changesVBox;
    private PlantCheckboxElement plantElement1, plantElement2;

    private FileAdminController fileController;

    @BeforeEach
    void setup() throws InterruptedException, IOException {
        resetFiles();
        fileController = (FileAdminController) dmsApplication.getCurrentTab().getTabController();
        PlantManager.getInstance().addPlant(new Plant(1001, "Testing Plant 1", new AccessModifier()));
        PlantManager.getInstance().addPlant(new Plant(1002, "Testing Plant 2", new AccessModifier()));
        boolean updated = false;
        Platform.runLater(() -> fileController.update());
        Thread.sleep(400);

        uploadDocButton = findNode("#uploadButton");
        createFolderButton = findNode("#createFolderButton");
        deleteFileButton = findNode("#deleteFileButton");

        publishButton = findNode("#publishChangesButton");
        fileTree = findNode("#fileTreeView");

        plantVBox = findNode("#plantVBox");
        changesVBox = findNode("#changesVBox");

        plantElement1 = (PlantCheckboxElement) plantVBox.getChildren().get(0);
        plantElement2 = (PlantCheckboxElement) plantVBox.getChildren().get(1);
    }

    private void resetFiles() throws IOException, InterruptedException {
        SettingsManager.setServerPath(TestUtil.getTestServerDocuments());
        SettingsManager.setLocalPath(TestUtil.getTestLocalDocuments());

        TestUtil.resetTestFiles();
        FileManager.resetInstance();
        PlantManager.resetInstance();

        AppFilesManager.createServerDirectories();
        AppFilesManager.createLocalDirectories();
    }

    private boolean containsChildWithName(Folder folder, String name){
        for(AbstractFile child : folder.getContents())
            if(child.getName().equals(name)) return true;
        return false;
    }

    @RepeatedTest(value = 2)
    void dragRootTest(){
        TreeItem<AbstractFile> root = fileTree.getRoot();
        TreeCell rootCell = getTreeCell(fileTree, root);
        Path startingPath = root.getValue().getPath();

        drag(rootCell).moveBy(0, 50).drop();
        assertEquals(startingPath, fileTree.getRoot().getValue().getPath());
        // Assert that the file system and the file list are still in sync
        assertTrue(TestUtil.doesAbstractFileMatchFileSystem(root.getValue(), SettingsManager.getServerDocumentsPath()));
    }

    @RepeatedTest(value = 2)
    void dragItemOutsideAppTest(){
        TreeItem<AbstractFile> draggedItem = fileTree.getRoot().getChildren().get(0);
        TreeCell draggedCell = getTreeCell(fileTree, draggedItem);
        Path startingPath = draggedItem.getValue().getPath();

        drag(draggedCell).moveBy(-400, 0).drop();
        assertEquals(startingPath, fileTree.getRoot().getChildren().get(0).getValue().getPath());
        // Assert that the file system and the file list are still in sync
        assertTrue(TestUtil.doesAbstractFileMatchFileSystem(draggedItem.getValue(), SettingsManager.getServerDocumentsPath()));
    }

    @RepeatedTest(value = 2)
    void dragItemToSelf(){
        int originalHash = fileTree.getRoot().hashCode();
        TreeItem<AbstractFile> draggedItem = fileTree.getRoot().getChildren().get(0);
        TreeCell draggedCell = getTreeCell(fileTree, draggedItem);
        Path startingPath = draggedItem.getValue().getPath();

        drag(draggedCell).moveBy(-30, 0).drop();
        assertEquals(startingPath, fileTree.getRoot().getChildren().get(0).getValue().getPath());
        // Assert that the file system and the file list are still in sync
        assertEquals(originalHash, fileTree.getRoot().hashCode());
        assertTrue(TestUtil.doesAbstractFileMatchFileSystem(draggedItem.getValue(), SettingsManager.getServerDocumentsPath()));
    }

    @RepeatedTest(value = 2)
    void dragNothing(){
        int originalHash = fileTree.getRoot().hashCode();
        TreeCell<AbstractFile> bottomCell = getTreeCell(fileTree, fileTree.getRoot().getChildren().get(3));
        moveTo(bottomCell);
        moveBy(0, 100);
        drag();
        dropTo(bottomCell);
        assertEquals(originalHash, fileTree.getRoot().hashCode());
    }

    @Test
    void moveFolderTest() throws InterruptedException {
        TreeItem<AbstractFile> targetItem = fileTree.getRoot().getChildren().get(0);
        TreeItem<AbstractFile> itemToMove = fileTree.getRoot().getChildren().get(1);
        moveAndAssert(itemToMove, targetItem);

        doubleClickOn(getTreeCell(fileTree, fileTree.getRoot().getChildren().get(0)));

        TreeItem<AbstractFile> newParent = fileTree.getRoot().getChildren().get(0);
        itemToMove = findChildWithName(newParent, itemToMove.getValue().getName());
        if(itemToMove == null)
            fail("Item was not moved correctly");

        moveAndAssert(itemToMove, fileTree.getRoot());
    }

    @Test
    void moveDocumentTest() throws InterruptedException {
        fileTree.getRoot().getChildren().get(1).setExpanded(true);
        Thread.sleep(200); // Coordinates will be messed up if there is no pause between expanding and locating
        moveAndAssert(fileTree.getRoot().getChildren().get(1).getChildren().get(0), fileTree.getRoot().getChildren().get(0));
    }

    private void moveAndAssert(TreeItem<AbstractFile> itemToMove, TreeItem<AbstractFile> targetItem){
        assertTrue(TestUtil.doesAbstractFileMatchFileSystem(FileManager.getInstance().getMainFilesRoot(), SettingsManager.getServerDocumentsPath()));
        assertTrue(TestUtil.doesAbstractFileMatchTreeItem(FileManager.getInstance().getMainFilesRoot(),fileTree.getRoot()));
        TreeItem<AbstractFile> originalParent = itemToMove.getParent();

        // Drag folder 1 into folder 0
        drag(getTreeCell(fileTree, itemToMove));
        dropTo(getTreeCell(fileTree, targetItem));

        assertTrue(((Folder)targetItem.getValue()).getContents().contains(itemToMove.getValue()));
        assertFalse(((Folder)originalParent.getValue()).getContents().contains(itemToMove.getValue()));

        // Assert that the fileTree, the file list and the file system are all in sync
        assertTrue(TestUtil.doesAbstractFileMatchFileSystem(FileManager.getInstance().getMainFilesRoot(), SettingsManager.getServerDocumentsPath()));
        assertTrue(TestUtil.doesAbstractFileMatchTreeItem(FileManager.getInstance().getMainFilesRoot(),fileTree.getRoot()));
    }

    @RepeatedTest(value = 2)
    void dragIntoSubfolderTest() throws InterruptedException {
        int originalTreeHash = fileTree.getRoot().hashCode();
        int originalFileListHash = fileTree.getRoot().getValue().hashCode();

        assertTrue(TestUtil.doesAbstractFileMatchFileSystem(FileManager.getInstance().getMainFilesRoot(), SettingsManager.getServerDocumentsPath()));
        assertTrue(TestUtil.doesAbstractFileMatchTreeItem(FileManager.getInstance().getMainFilesRoot(),fileTree.getRoot()));

        // Drag itemToMove folder into its' first subfolder
        fileTree.getRoot().getChildren().get(0).setExpanded(true);
        Thread.sleep(200);

        TreeItem<AbstractFile> itemToMove = fileTree.getRoot().getChildren().get(0);
        TreeItem<AbstractFile> targetItem = itemToMove.getChildren().get(0);

        FxRobot fxRobot = drag(getTreeCell(fileTree, itemToMove));
        fxRobot.dropTo(getTreeCell(fileTree, targetItem));

        // Assert that nothing has changed in the treeView and in the files list
        assertEquals(originalTreeHash, fileTree.getRoot().hashCode());
        assertEquals(originalFileListHash, fileTree.getRoot().getValue().hashCode());
        // Assert the file list still matches the file system
        assertTrue(TestUtil.doesAbstractFileMatchFileSystem(FileManager.getInstance().getMainFilesRoot(), SettingsManager.getServerDocumentsPath()));
    }

    private static TreeCell<AbstractFile> getTreeCell(TreeView<AbstractFile> tree, TreeItem<AbstractFile> treeItem){
        Set<Node> treeCells = tree.lookupAll(".tree-cell");
        List<Node> cells = new ArrayList<>(treeCells);
        int row = tree.getRow(treeItem);
        return ((TreeCell<AbstractFile>) cells.get(row));
    }

    private static TreeItem<AbstractFile> findChildWithName(TreeItem<AbstractFile> treeItem, String name){
        for(TreeItem<AbstractFile> child : treeItem.getChildren()){
            if(child.getValue().getName().equals(name)) return child;
        }
        return null;
    }


}

