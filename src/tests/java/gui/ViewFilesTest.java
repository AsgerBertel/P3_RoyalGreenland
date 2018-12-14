package gui;

import directory.FileExplorer;
import directory.FileManager;
import directory.SettingsManager;
import directory.files.AbstractFile;
import directory.plant.AccessModifier;
import directory.plant.Plant;
import directory.plant.PlantManager;
import gui.file_overview.FileOverviewController;
import gui.plant_administration.PlantAdministrationController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import json.AppFilesManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.TestUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class ViewFilesTest extends GUITest {

    private FileOverviewController fileController;
    private PlantAdministrationController plantAdminController;

    private TreeView<AbstractFile> fileTree;
    private FileExplorer fileExplorer;
    private FlowPane flpFileView;
    private TreeItem<AbstractFile> targetItemAdmin;
    private TreeItem<AbstractFile> itemToMoveAdmin;
    private TreeItem<AbstractFile> itemAddedToPlant;
    private TreeItem<AbstractFile> selectedItem;
    private ComboBox<Plant> drdPlant;

    private VBox plantVBox;

    Plant plant1 = new Plant(4321,"Testing factory 1", new AccessModifier());
    Plant plant2 = new Plant(1234, "Testing Factory 2", new AccessModifier());
    private PlantElement plant1Element, plant2Element;

    @BeforeEach
    void setup() throws IOException, InterruptedException {
        // Reset files
        resetFiles();

        // Wait for reset files.
        Thread.sleep(4000);

        // Go to plants and add a plant
        clickOn((ToggleButton)findNode("#administratePlantsButton"));
        TextField nameTextField = findNode("#fieldCreatePlantName");
        TextField idTextField = findNode("#fieldCreatePlantId");
        Button saveEditButton = findNode("#btnCreatePlant");
        writeInTextField(nameTextField, "Nuuk");
        writeInTextField(idTextField, Integer.toString(1010));
        clickOn(saveEditButton);

        // Go to view files tab
        clickOn((ToggleButton)findNode("#administrateDocumentsButton"));

        // Create reference for file tree
        fileTree = findNode("#fileTreeView");

        // Move something to enable the publish button.
        targetItemAdmin = fileTree.getRoot().getChildren().get(0);
        itemToMoveAdmin = fileTree.getRoot().getChildren().get(1);
        drag(getTreeCell(fileTree, itemToMoveAdmin));
        dropTo(getTreeCell(fileTree, targetItemAdmin));

        // Click folder and add file to plant
        addFileToPlant();

        // Publish changes.
        clickOn((Button)findNode("#publishChangesButton"));

        // Go to view files tab
        clickOn((ToggleButton)findNode("#viewDocumentsButton"));

        // Create reference to controller
        fileController = (FileOverviewController) dmsApplication.getCurrentTab().getTabController();

        // Create reference to fileExplorer
        fileExplorer = fileController.getFileExplorer();

        // Create reference to fileExplorer view
        flpFileView = findNode("#flpFileView");

        // Create reference to file tree
        fileTree = findNode("#Filetree");

        // Create reference for plant dropdown
        drdPlant = findNode("#drdPlant");
    }

    private static TreeCell<AbstractFile> getTreeCell(TreeView<AbstractFile> tree, TreeItem<AbstractFile> treeItem){
        Set<Node> treeCells = tree.lookupAll(".tree-cell");
        List<Node> cells = new ArrayList<>(treeCells);
        int row = tree.getRow(treeItem);
        return ((TreeCell<AbstractFile>) cells.get(row));
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

    // Assert that the folder 03 is now inside the 02 folder.
    @Test
    void viewFilesExplorerTest(){
        // Open first folder
        doubleClickOn(flpFileView.getChildren().get(0));

        // Click on first folder
        clickOn(flpFileView.getChildren().get(0));

        assertEquals(itemToMoveAdmin.getValue().getName(), fileController.getSelectedFileExplorer().getFile().getName());
    }

    @Test
    void viewFilesTreeTest(){
        // Expand first folder
        selectedItem = fileTree.getRoot().getChildren().get(0);
        doubleClickOn(getTreeCell(fileTree, selectedItem));

        // Click on first subfolder
        TreeItem<AbstractFile> subSelectedItem = fileTree.getRoot().getChildren().get(0).getChildren().get(0);
        clickOn(getTreeCell(fileTree, subSelectedItem));

        // Assert that the selected item has the same name as the one originally moved in file admin. See setup()
        assertEquals(fileTree.getSelectionModel().getSelectedItem().getValue().getName(), itemToMoveAdmin.getValue().getName());
    }

    @Test
    void selectPlantTest(){
        // Select first plant
        clickOn(drdPlant);
        moveBy(0,80);
        clickOn();

        // Open first folder
        doubleClickOn(flpFileView.getChildren().get(0));

        // Select first file
        clickOn(flpFileView.getChildren().get(0));

        assertEquals(fileController.getSelectedFileExplorer().getFile().getName(), itemAddedToPlant.getValue().getName());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void writeInTextField(TextField textField, String text){
        clickOn(textField);
        selectAllAndDelete();
        write(text);
    }

    private void addFileToPlant(){
        doubleClickOn(getTreeCell(fileTree, fileTree.getRoot().getChildren().get(0)));

        itemAddedToPlant = fileTree.getRoot().getChildren().get(0).getChildren().get(2);

        clickOn(getTreeCell(fileTree, itemAddedToPlant));

        plantVBox = findNode("#plantVBox");
        clickOn(plantVBox.getChildren().get(0));

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
