package gui.custom_node;

import app.DMSApplication;
import controller.PlantAdministrationController;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;


class PlantContextMenu extends ContextMenu {
    private PlantAdministrationController plantAdministrationController;

    public PlantContextMenu(PlantAdministrationController plantAdministrationController) {
        this.plantAdministrationController = plantAdministrationController;
        initPlantContextMenu();
    }

    private void initPlantContextMenu() {
        MenuItem createPlant = new MenuItem(DMSApplication.getMessage("PlantAdmin.ContextMenu.CreatePlant"));
        createPlant.setOnAction(event -> createPlant());

        MenuItem editPlant = new MenuItem(DMSApplication.getMessage("PlantAdmin.ContextMenu.EditPlant"));
        editPlant.setOnAction(event ->  editPlant());

        MenuItem deletePlant = new MenuItem(DMSApplication.getMessage("PlantAdmin.ContextMenu.DeletePlant"));
        deletePlant.setOnAction(event -> deletePlant());

        //  fileButton.setContextMenu(this.folderContextMenu(selectedItem));
        this.getItems().addAll(createPlant,editPlant,deletePlant);
    }

    private void createPlant() { plantAdministrationController.createPlantSidebar(); }

    private void editPlant(){ plantAdministrationController.editPlantSidebar(); }

    private void deletePlant(){ plantAdministrationController.deletePlant(); }


}
