package gui;

import directory.plant.AccessModifier;
import directory.plant.Plant;
import directory.plant.PlantManager;
import gui.plant_administration.PlantAdministrationController;
import gui.settings.SettingsController;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import static org.junit.jupiter.api.Assertions.*;

public class PlantAdminTabTest extends GUITest {

    private Plant plant1 = new Plant(4321,"Testing factory 1", new AccessModifier());
    private Plant plant2 = new Plant(1234, "Testing Factory 2", new AccessModifier());
    private PlantElement plant1Element, plant2Element;
    private PlantAdministrationController plantController;
    private Button menuCreatePlantButton, menuEditPlantButton, deletePlantButton;

    @BeforeEach
    void loadTab() {
        PlantManager.getInstance().getAllPlants().clear();
        PlantManager.getInstance().getAllPlants().add(plant1);
        PlantManager.getInstance().getAllPlants().add(plant2);
        clickOn((ToggleButton)findNode("#administratePlantsButton"));
        plantController = (PlantAdministrationController) dmsApplication.getCurrentTab().getTabController();
        plant1Element = (PlantElement) plantController.getPlantVBox().getChildren().get(0);
        plant2Element = (PlantElement) plantController.getPlantVBox().getChildren().get(1);

        menuCreatePlantButton = findNode("#sidebarBtnCreatePlant");
        menuEditPlantButton = findNode("#btnEditPlantSidebar");
        deletePlantButton = findNode("#btnDeletePlant");
    }

    private void writeInTextField(TextField textField, String text){
        clickOn(textField);
        selectAllAndDelete();
        write(text);
    }

    @RepeatedTest(value = 2)
    void disabledButtonsTest() {
        assertFalse(menuCreatePlantButton.isDisabled());
        assertTrue(menuEditPlantButton.isDisabled());
        assertTrue(deletePlantButton.isDisabled());

        clickOn(plant1Element);
        assertFalse(menuCreatePlantButton.isDisabled());
        assertFalse(menuEditPlantButton.isDisabled());
        assertFalse(deletePlantButton.isDisabled());
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
        Plant newPlant = new Plant(newPlantID, newPlantName, new AccessModifier());

        TextField nameTextField = findNode("#fieldCreatePlantName");
        TextField idTextField = findNode("#fieldCreatePlantId");
        Button saveEditButton = findNode("#btnCreatePlant");

        writeInTextField(nameTextField, newPlantName);
        writeInTextField(idTextField, Integer.toString(newPlantID));

        clickOn(saveEditButton);

        assertEquals(2, plantController.getPlantVBox().getChildren().size());
        assertEquals(newPlant, ((PlantElement)plantController.getPlantVBox().getChildren().get(1)).getPlant());
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
        Button saveEditButton = findNode("#btnCreatePlant");

        clickOn(plant1Element);
        clickOn(showEditButton);

        writeInTextField(nameTextField, plant2.getName());
        writeInTextField(nameTextField, Integer.toString(plant2.getId()));

        clickOn(saveEditButton);

        assertTrue(nameTextField.getStyleClass().contains(SettingsController.ERROR_STYLE_CLASS));
        assertTrue(nameTextField.getStyleClass().contains(SettingsController.ERROR_STYLE_CLASS));

        assertNotEquals(plant2.getId(), plant1.getId());
        assertNotEquals(plant2.getName(), plant1.getName());
    }




}
