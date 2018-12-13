package gui.plant_administration;

import gui.DMSApplication;
import gui.PlantElement;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;


public class PlantContextMenu extends ContextMenu {
    private PlantAdministrationController plantAdministrationController;

    public PlantContextMenu(PlantAdministrationController plantAdministrationController) {
        this.plantAdministrationController = plantAdministrationController;
        initPlantContextMenu();
    }

    public void initPlantContextMenu() {
        MenuItem createPlant = new MenuItem(DMSApplication.getMessage("PlantAdmin.ContextMenu.CreatePlant"));
        createPlant.setOnAction(event -> createPlant());

        MenuItem editPlant = new MenuItem(DMSApplication.getMessage("PlantAdmin.ContextMenu.EditPlant"));
        editPlant.setOnAction(event ->  editPlant());

        MenuItem deletePlant = new MenuItem(DMSApplication.getMessage("PlantAdmin.ContextMenu.DeletePlant"));
        deletePlant.setOnAction(event -> deletePlant());

        //  fileButton.setContextMenu(this.folderContextMenu(selectedItem));
        this.getItems().addAll(createPlant,editPlant,deletePlant);
    }

    public void createPlant() { plantAdministrationController.createPlantSidebar(); }

    public void editPlant(){ plantAdministrationController.editPlantSidebar(); }

    public void deletePlant(){ plantAdministrationController.deletePlant(); }


}
