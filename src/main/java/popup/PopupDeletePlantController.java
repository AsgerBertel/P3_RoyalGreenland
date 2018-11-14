package popup;

import directory.plant.PlantManager;
import gui.PlantElement;
import gui.plant_administration.PlantAdministrationController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

import static gui.DMSApplication.fxmlPath;
public class PopupDeletePlantController {


    Stage stage;
    Parent root;
    PlantAdministrationController plantAdmin;
    public PopupDeletePlantController(){
        stage = new Stage();
        stage.setTitle("Delete plant");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath + "PopupDeletePlant.fxml"));
        fxmlLoader.setController(this);
        try {
            root = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setScene(new Scene(root));
        stage.show();
    }



    public void deletePlantController(PlantAdministrationController plantAdmin){
         this.plantAdmin = plantAdmin;
         return;
    }

    @FXML
    PlantElement btnYesPressed(ActionEvent event) {
    deletePlantController(plantAdmin);
        ArrayList<PlantElement> plantElements = plantAdmin.getPlantElements();
        for (PlantElement element : plantElements) {
            if (element.isSelected()) {
                plantAdmin.getPlantElements().remove(element);
                PlantManager.getInstance().deletePlant(element.getPlant().getId());
                plantAdmin.getPlantVBox().getChildren().remove(element);
                stage.close();
                return element;
            }
        }
        return null;
    }

    @FXML
    void btnNoPressed(MouseEvent event){
        stage.close();
        return;
    }

}
