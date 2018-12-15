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
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import util.TestUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


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
    private Button btnReturn;

    private VBox plantVBox;

    @BeforeEach
    void setup() throws IOException, InterruptedException {
        // Reset files
        resetFiles();
        FileManager.resetInstance();
        PlantManager.resetInstance();

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

        // Create reference to file Explorer back button
        btnReturn = findNode("#btnReturn");
    }

    // Assert that the folder 03 is now inside the 02 folder.
    @RepeatedTest(value = 2)
    void viewFilesExplorerTest(){
        // Open first folder
        doubleClickOn(flpFileView.getChildren().get(0));

        // Click on first folder
        clickOn(flpFileView.getChildren().get(0));

        // That the selected file is the same as the file moved in the setup method.
        assertEquals(itemToMoveAdmin.getValue().getName(), fileController.getSelectedFileExplorer().getFile().getName());
    }

    @RepeatedTest(value = 2)
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

    @RepeatedTest(value = 2)
    void selectPlantTest(){
        // Select first plant
        clickOn(drdPlant);
        moveBy(0,80);
        clickOn();

        // Assert that only 1 folder is available to the plant.
        assertEquals(1, flpFileView.getChildren().size());

        // Open first folder
        doubleClickOn(flpFileView.getChildren().get(0));

        // Assert that the folder also only have 1 file inside.
        assertEquals(1, flpFileView.getChildren().size());

        // Select first file
        clickOn(flpFileView.getChildren().get(0));

        // Assert that the file added to the plant is also the selected file.
        assertEquals(fileController.getSelectedFileExplorer().getFile().getName(), itemAddedToPlant.getValue().getName());
    }

    @RepeatedTest(value = 2)
    void navigateExplorer() throws InterruptedException {
        // Select first file and save path.
        clickOn(flpFileView.getChildren().get(0));
        String rootPath = fileController.getSelectedFileExplorer().getFile().getOSPath().toString();

        // Save the number of elements initially in root for assertion.
        int elementsInRoot = flpFileView.getChildren().size();

        // Open first folder
        doubleClickOn(flpFileView.getChildren().get(0));

        // Open second folder
        Thread.sleep(500);
        doubleClickOn(flpFileView.getChildren().get(0));

        // Navigate back to root.
        clickOn(btnReturn);
        clickOn(btnReturn);

        // Click on first element to get reference.
        clickOn(flpFileView.getChildren().get(0));

        // Assert that the old root path is now the same after entering and exiting folders.
        assertEquals(rootPath, fileController.getSelectedFileExplorer().getFile().getOSPath().toString());

        // Assert that the number of folders in the root folder is still the same.
        assertEquals(elementsInRoot, flpFileView.getChildren().size());
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
}
