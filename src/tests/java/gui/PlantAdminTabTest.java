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
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PlantAdminTabTest extends GUITest {

    private Plant standardPlant = new Plant(1548,"Testing factory", new AccessModifier());
    private PlantElement standardPlantElement;
    private PlantAdministrationController plantController;
    private Button menuCreatePlantButton, menuEditPlantButton, deletePlantButton;

    @BeforeEach
    void setTab() throws InterruptedException {
        PlantManager.getInstance().getAllPlants().clear();
        PlantManager.getInstance().getAllPlants().add(standardPlant);
        clickOn((ToggleButton)findNode("#administratePlantsButton"));
        plantController = (PlantAdministrationController) dmsApplication.getCurrentTab().getTabController();
        standardPlantElement = (PlantElement) plantController.getPlantVBox().getChildren().get(0);

        menuCreatePlantButton = (Button) findNode("#sidebarBtnCreatePlant");
        menuEditPlantButton = (Button) findNode("#btnEditPlantSidebar");
        deletePlantButton = (Button) findNode("#btnDeletePlant");
    }

    @RepeatedTest(value = 2)
    void disabledButtonsTest() {
        assertFalse(menuCreatePlantButton.isDisabled());
        assertTrue(menuEditPlantButton.isDisabled());
        assertTrue(deletePlantButton.isDisabled());

        clickOn(standardPlantElement);

        assertFalse(menuCreatePlantButton.isDisabled());
        assertFalse(menuEditPlantButton.isDisabled());
        assertFalse(deletePlantButton.isDisabled());
    }

    @RepeatedTest(value = 2)
    void createPlantMenuButtonTest() {
        clickOn(standardPlantElement);
        clickOn(menuEditPlantButton);
        clickOn(menuCreatePlantButton);
        assertTrue(findNode("#createPane").isVisible());
        assertFalse(findNode("#editPane").isVisible());
    }

    @RepeatedTest(value = 2)
    void editPlantMenuButtonTest() {
        clickOn(standardPlantElement);
        clickOn(menuEditPlantButton);
        assertTrue(findNode("#editPane").isVisible());
        assertEquals(((TextField) findNode("#fieldEditPlantName")).getText(), standardPlant.getName());
        assertEquals(((TextField) findNode("#fieldEditPlantId")).getText(), Integer.toString(standardPlant.getId()));
    }

    @RepeatedTest(value = 2)
    void editPlantTest() {
        String originalPlantName = standardPlant.getName();
        String newPlantName = "*/(€$£{!? Test name";
        int originalPlantID = standardPlant.getId();
        int newPlantID = 7643;

        clickOn(standardPlantElement);
        clickOn(menuEditPlantButton);

        clickOn((TextField) findNode("#fieldEditPlantName"));
        selectAllAndDelete();
        write(newPlantName);

        clickOn((TextField) findNode("#fieldEditPlantId"));
        selectAllAndDelete();
        write(Integer.toString(newPlantID));

        // Assert the values have not yet changed
        assertEquals(originalPlantName, standardPlant.getName());
        assertEquals(originalPlantID, standardPlant.getId());

        // Save changes and assert that the changes have now been applied
        clickOn((Button) findNode("#btnSavePlantEdit"));
        assertEquals(newPlantName, standardPlant.getName());
        assertEquals(newPlantID, standardPlant.getId());
    }

    @RepeatedTest(value = 2)
    void createPlantTest() {
        String newPlantName = "Test Factory";
        int newPlantID = 1000;
        Plant newPlant = new Plant(newPlantID, newPlantName, new AccessModifier());

        TextField nameTextField = findNode("#fieldCreatePlantName");
        TextField idTextField = findNode("#fieldCreatePlantId");
        Button saveEditButton = findNode("#btnCreatePlant");

        clickOn(nameTextField);
        write(newPlantName);
        clickOn(idTextField);
        write(Integer.toString(newPlantID));

        clickOn(saveEditButton);

        assertEquals(2, plantController.getPlantVBox().getChildren().size());
        assertEquals(newPlant, ((PlantElement)plantController.getPlantVBox().getChildren().get(1)).getPlant());
    }

    @RepeatedTest(value = 2) // Create plant with the same name and id as already existing plant
    void createDuplicatePlantTest() throws InterruptedException {
        String newPlantName = standardPlant.getName();
        int newPlantID = standardPlant.getId();

        TextField nameTextField = findNode("#fieldCreatePlantName");
        TextField idTextField = findNode("#fieldCreatePlantId");
        Button saveEditButton = findNode("#btnCreatePlant");

        clickOn(standardPlantElement);
        clickOn(menuCreatePlantButton);

        clickOn(nameTextField);
        selectAllAndDelete();
        write(newPlantName);

        clickOn(idTextField);
        selectAllAndDelete();
        write(Integer.toString(newPlantID));

        clickOn(saveEditButton);

        // Assert that an error is show in both fields and that no plant was created
        assertTrue(nameTextField.getStyleClass().contains(SettingsController.ERROR_STYLE_CLASS));
        assertTrue(nameTextField.getStyleClass().contains(SettingsController.ERROR_STYLE_CLASS));
        assertEquals(1, plantController.getPlantVBox().getChildren().size());
    }







}
