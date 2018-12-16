package gui;

import directory.SettingsManager;
import directory.plant.AccessModifier;
import directory.plant.Plant;
import directory.plant.PlantManager;
import gui.plant_administration.PlantAdministrationController;
import gui.settings.SettingsController;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseButton;
import org.apache.commons.io.FileUtils;
import org.assertj.core.error.future.ShouldNotHaveFailed;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import util.TestUtil;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class PlantAdminTabTest extends GUITest {

    private Plant plant1 = new Plant(4321,"Testing factory 1", new AccessModifier());
    private Plant plant2 = new Plant(1234, "Testing Factory 2", new AccessModifier());
    private PlantElement plant1Element, plant2Element;
    private PlantAdministrationController plantController;
    private Button menuCreatePlantButton, menuEditPlantButton, deletePlantButton;

    @BeforeEach
    void loadTab() throws IOException {
        SettingsManager.setServerPath(TestUtil.getTestServerDocuments());
        SettingsManager.setLocalPath(TestUtil.getTestLocalDocuments());
        TestUtil.resetTestFiles();
        PlantManager.getInstance().getAllPlants().clear();
        PlantManager.getInstance().getAllPlants().add(plant1);
        PlantManager.getInstance().getAllPlants().add(plant2);
        clickOn((ToggleButton)findNode("#administratePlantsButton"));
        plantController = (PlantAdministrationController) dmsApplication.getCurrentTab().getTabController();
        plant1Element = (PlantElement) plantController.getPlantVBox().getChildren().get(0);
        plant2Element = (PlantElement) plantController.getPlantVBox().getChildren().get(1);

        menuCreatePlantButton = findNode("#btnCreatePlantSidebar");
        menuEditPlantButton = findNode("#btnEditPlantSidebar");
        deletePlantButton = findNode("#btnDeletePlantSidebar");
    }

    private void writeInTextField(TextField textField, String text){
        clickOn(textField);
        selectAllAndDelete();
        write(text);
    }

    @RepeatedTest(value = 2)
    void disabledMenuButtonsTest() {
        assertFalse(menuCreatePlantButton.isDisabled());
        assertTrue(menuEditPlantButton.isDisabled());
        assertTrue(deletePlantButton.isDisabled());

        clickOn(plant1Element);
        assertFalse(menuCreatePlantButton.isDisabled());
        assertFalse(menuEditPlantButton.isDisabled());
        assertFalse(deletePlantButton.isDisabled());
    }

    @RepeatedTest(value = 2)
    void disabledSaveEditButton(){
        Button saveEditButton = findNode("#btnSavePlantEdit");
        TextField nameTextField = findNode("#fieldEditPlantName");

        clickOn(plant1Element);
        clickOn(menuEditPlantButton);
        assertTrue(saveEditButton.isDisabled());

        writeInTextField(nameTextField, "Test");
        assertFalse(saveEditButton.isDisabled());
    }

    @RepeatedTest(value = 2)
    void createPlantMenuButtonTest() {
        clickOn(plant1Element);
        clickOn(menuEditPlantButton);
        clickOn(menuCreatePlantButton);
        assertTrue(findNode("#createPane").isVisible());
        assertFalse(findNode("#editPane").isVisible());
    }

    @RepeatedTest(value = 2)
    void editPlantMenuButtonTest() {
        clickOn(plant1Element);
        clickOn(menuEditPlantButton);
        assertTrue(findNode("#editPane").isVisible());
        assertFalse(findNode("#createPane").isVisible());
        assertEquals(((TextField) findNode("#fieldEditPlantName")).getText(), plant1.getName());
        assertEquals(((TextField) findNode("#fieldEditPlantId")).getText(), Integer.toString(plant1.getId()));
    }

    @RepeatedTest(value = 2)
    void editPlantTest() {
        String originalPlantName = plant1.getName();
        String newPlantName = "*/(€$£{!? Test name";
        int originalPlantID = plant1.getId();
        int newPlantID = 7643;

        clickOn(plant1Element);
        clickOn(menuEditPlantButton);

        writeInTextField((TextField) findNode("#fieldEditPlantName"), newPlantName);
        writeInTextField((TextField) findNode("#fieldEditPlantId"), Integer.toString(newPlantID));

        // Assert the values have not yet changed
        assertEquals(originalPlantName, plant1.getName());
        assertEquals(originalPlantID, plant1.getId());

        // Save changes and assert that the changes have now been applied
        clickOn((Button) findNode("#btnSavePlantEdit"));
        assertEquals(newPlantName, plant1.getName());
        assertEquals(newPlantID, plant1.getId());
    }

    @RepeatedTest(value = 2)
    void createPlantTest() {
        String newPlantName = "Test Factory";
        int newPlantID = 1000;
        int startingListSize = plantController.getPlantVBox().getChildren().size();
        Plant newPlant = new Plant(newPlantID, newPlantName, new AccessModifier());

        TextField nameTextField = findNode("#fieldCreatePlantName");
        TextField idTextField = findNode("#fieldCreatePlantId");
        Button saveEditButton = findNode("#btnCreatePlant");

        writeInTextField(nameTextField, newPlantName);
        writeInTextField(idTextField, Integer.toString(newPlantID));

        clickOn(saveEditButton);

        assertEquals(startingListSize + 1, plantController.getPlantVBox().getChildren().size());
        assertEquals(newPlant, ((PlantElement)plantController.getPlantVBox().getChildren().get(startingListSize)).getPlant());
    }

    @RepeatedTest(value = 2) // Create plant with the same name and id as already existing plant
    void createDuplicatePlantTest() throws InterruptedException {
        String newPlantName = plant1.getName();
        int newPlantID = plant1.getId();
        int startingListSize = plantController.getPlantVBox().getChildren().size();

        TextField nameTextField = findNode("#fieldCreatePlantName");
        TextField idTextField = findNode("#fieldCreatePlantId");
        Button saveEditButton = findNode("#btnCreatePlant");

        clickOn(plant1Element);
        clickOn(menuCreatePlantButton);

        writeInTextField(nameTextField, newPlantName);
        writeInTextField(idTextField, Integer.toString(newPlantID));

        clickOn(saveEditButton);

        // Assert that an error is show in both fields and that no plant was created
        assertTrue(nameTextField.getStyleClass().contains(SettingsController.ERROR_STYLE_CLASS));
        assertTrue(nameTextField.getStyleClass().contains(SettingsController.ERROR_STYLE_CLASS));
        assertEquals(startingListSize, plantController.getPlantVBox().getChildren().size());
    }

    @RepeatedTest(value = 2) // Create plant with the same name and id as already existing plant
    void editDuplicatePlantTest() throws InterruptedException {
        TextField nameTextField = findNode("#fieldEditPlantName");
        TextField idTextField = findNode("#fieldEditPlantId");
        Button showEditButton = findNode("#btnEditPlantSidebar");
        Button saveEditButton = findNode("#btnSavePlantEdit");

        clickOn(plant1Element);
        clickOn(showEditButton);

        writeInTextField(nameTextField, plant2.getName());
        writeInTextField(idTextField, Integer.toString(plant2.getId()));

        clickOn(saveEditButton);

        assertTrue(nameTextField.getStyleClass().contains(SettingsController.ERROR_STYLE_CLASS));
        assertTrue(nameTextField.getStyleClass().contains(SettingsController.ERROR_STYLE_CLASS));

        assertNotEquals(plant2.getId(), plant1.getId());
        assertNotEquals(plant2.getName(), plant1.getName());
    }

    @RepeatedTest(value = 2)
    void switchSelectedPlantWhileEditing(){
        TextField nameTextField = findNode("#fieldEditPlantName");
        TextField idTextField = findNode("#fieldEditPlantId");
        Button showEditButton = findNode("#btnEditPlantSidebar");
        Button saveEditButton = findNode("#btnSavePlantEdit");

        String unsavedName = "Unsaved Name";

        clickOn(plant1Element);
        clickOn(showEditButton);
        writeInTextField(nameTextField, unsavedName);

        assertEquals(nameTextField.getText(), unsavedName);

        // Check that the nothing changes when selecting the same plant again
        clickOn(plant1Element);
        assertEquals(nameTextField.getText(), unsavedName);

        // Check that the changes have not been applied and that the fields has been reset to match the new plant
        clickOn(plant2Element);
        assertNotEquals(plant1.getName(), unsavedName);
        assertEquals(plant2.getName(), nameTextField.getText());
    }

    @RepeatedTest(value = 2)
    void deletePlantTest(){
        Button deletePlantButton = findNode("#btnDeletePlantSidebar");
        int originalListSize = plantController.getPlantVBox().getChildren().size();
        assertTrue(plantController.getPlantVBox().getChildren().contains(plant1Element));
        assertTrue(plantController.getPlantVBox().getChildren().contains(plant2Element));

        clickOn(plant1Element);
        clickOn(deletePlantButton);
        clickOn(DMSApplication.getMessage("PlantAdmin.Popup.Delete"));

        assertFalse(plantController.getPlantVBox().getChildren().contains(plant1Element));
        assertTrue(plantController.getPlantVBox().getChildren().contains(plant2Element));
        assertTrue(menuEditPlantButton.isDisabled());
        assertTrue(deletePlantButton.isDisabled());
    }

    @RepeatedTest(value = 2)
    void deletePlantWhileEditingTest(){
        Button deletePlantButton = findNode("#btnDeletePlantSidebar");
        TextField nameTextField = findNode("#fieldEditPlantName");
        Button showEditButton = findNode("#btnEditPlantSidebar");
        String unsavedName = "Unsaved Name";

        clickOn(plant1Element);
        clickOn(showEditButton);
        writeInTextField(nameTextField, unsavedName);

        assertEquals(unsavedName, nameTextField.getText());
        clickOn(deletePlantButton);
        clickOn(DMSApplication.getMessage("PlantAdmin.Popup.Delete"));

        assertFalse(findNode("#editPane").isVisible());
        assertTrue(showEditButton.isDisabled());
    }

    @RepeatedTest(value = 2)
    void cancelDeleteTest(){
        Button deletePlantButton = findNode("#btnDeletePlantSidebar");
        Button showEditButton = findNode("#btnEditPlantSidebar");

        clickOn(plant1Element);
        clickOn(deletePlantButton);
        clickOn(DMSApplication.getMessage("PlantAdmin.Popup.Cancel"));

        assertTrue(plantController.getPlantVBox().getChildren().contains(plant1Element));
        assertTrue(plant1Element.isSelected());
        assertFalse(deletePlantButton.isDisabled());
        assertFalse(showEditButton.isDisabled());
    }

    @RepeatedTest(value = 2)
    void closeDeletePopupTest() throws InterruptedException {
        Button deletePlantButton = findNode("#btnDeletePlantSidebar");
        Button showEditButton = findNode("#btnEditPlantSidebar");

        clickOn(plant1Element);

        assertTrue(plantController.getPlantVBox().getChildren().contains(plant1Element));
        assertFalse(deletePlantButton.isDisabled());

        clickOn(deletePlantButton);
        moveTo(DMSApplication.getMessage("PlantAdmin.Popup.Cancel"));
        // todo This is a janky way to close the popup. It will break if the size of the popup changes
        moveBy(35, -140);
        clickOn(MouseButton.PRIMARY);

        assertTrue(plant1Element.isSelected());
        boolean containsElement = false;
        for(Node plantElement : plantController.getPlantVBox().getChildren()){
            containsElement |= (((PlantElement) plantElement).getPlant().getName().equals(plant1Element.getPlant().getName()));
        }
        assertTrue(containsElement);
        assertFalse(deletePlantButton.isDisabled());
        assertFalse(showEditButton.isDisabled());
    }

    @RepeatedTest(value = 2)
    void showContextMenuTest() {
        rightClickOn(plant2Element);
        clickOnContextMenuItem(1); // Edit plant
        assertTrue(findNode("#editPane").isVisible());
        assertFalse(findNode("#createPane").isVisible());

        rightClickOn(plant1Element);
        clickOnContextMenuItem(0); // Create plant
        assertTrue(findNode("#createPane").isVisible());
        assertFalse(findNode("#editPane").isVisible());

        rightClickOn(plant2Element);
        clickOnContextMenuItem(2); // Delete plant
        clickOn(DMSApplication.getMessage("PlantAdmin.Popup.Delete"));

        assertFalse(plantController.getPlantVBox().getChildren().contains(plant2Element));
        assertTrue(plantController.getPlantVBox().getChildren().contains(plant1Element));
    }

    @RepeatedTest(value = 2)
    void deleteNewPlant(){
        PlantElement newPlantElement = createNewPlant(1265, "Test");
        assertTrue(plantController.getPlantVBox().getChildren().contains(newPlantElement));

        clickOn(newPlantElement);
        clickOn(deletePlantButton);
        clickOn(DMSApplication.getMessage("PlantAdmin.Popup.Delete"));

        assertFalse(plantController.getPlantVBox().getChildren().contains(newPlantElement));
    }

    @RepeatedTest(value = 2)
    void rightClickNewPlant(){
        TextField nameTextField = findNode("#fieldEditPlantName");
        String newPlantName = "Right click";
        PlantElement newPlantElement = createNewPlant(1265, newPlantName);
        assertTrue(plantController.getPlantVBox().getChildren().contains(newPlantElement));

        rightClickOn(newPlantElement);
        clickOnContextMenuItem(1); // Edit
        assertTrue(findNode("#editPane").isVisible());
        assertEquals(nameTextField.getText(), newPlantName);
    }

    // Creates a new plant and returns the plant element from the list
    private PlantElement createNewPlant(int id, String name) {
        TextField nameTextField = findNode("#fieldCreatePlantName");
        TextField idTextField = findNode("#fieldCreatePlantId");
        Button saveEditButton = findNode("#btnCreatePlant");

        writeInTextField(nameTextField, name);
        writeInTextField(idTextField, Integer.toString(id));

        clickOn(saveEditButton);

        for (Node plantElement : plantController.getPlantVBox().getChildren()) {
            if (((PlantElement) plantElement).getPlant().getId() == id)
                return (PlantElement) plantElement;
        }
        throw new RuntimeException("Could not create new plant and identify the corresponding plant element");
    }
}
