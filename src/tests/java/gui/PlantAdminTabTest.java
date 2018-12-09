package gui;

import directory.plant.AccessModifier;
import directory.plant.Plant;
import directory.plant.PlantManager;
import gui.plant_administration.PlantAdministrationController;
import javafx.scene.control.Button;
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
    void createPlantTest() {
        clickOn(menuCreatePlantButton);



    }







}
