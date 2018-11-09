package gui.plant_administration;

import directory.plant.AccessModifier;
import directory.plant.Plant;
import gui.PlantElement;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class PlantAdministrationController implements Initializable{

    @FXML
    private AnchorPane createPane;
    @FXML
    private VBox plantVBox;

    ArrayList<PlantElement> plantElements = new ArrayList<>();

    @FXML
    private AnchorPane editPane;

    @FXML
    private Label btnPlantEdited;

    @FXML
    private Label btnPlantCreated;

    @FXML
    private TextField field_CreatePlantName;

    @FXML
    private TextField field_CreatePlantId;

    @FXML
    private TextField field_EditPlantName;

    @FXML
    private TextField field_EditPlantId;



    @Override
    public void initialize(URL location, ResourceBundle resources){
        //Setting standard
        createPane.toFront();
        editPane.setDisable(true);
        editPane.setVisible(false);


        for (int i = 0; i < 15; i++) {
            Plant p = new Plant(1243 + i, "NUUK", new AccessModifier());
            PlantElement plantElement = new PlantElement(p);
            plantElement.setOnSelectedListener(() -> onPlantToggle(plantElement));
            plantElements.add(plantElement);
        }
        plantVBox.getChildren().addAll(plantElements);

    }

    private void onPlantToggle(PlantElement plantElement) {
        for(PlantElement element:plantElements){
            element.setSelected(false);
        }
        plantElement.setSelected(true);

    }


    @FXML
    void createPlantSidebar(ActionEvent event) {
        activatePane(createPane, editPane);
        btnPlantCreated.setText("");
        btnPlantCreated.setVisible(true);
    }

    @FXML
    void editPlantSidebar(ActionEvent event) {
        btnPlantEdited.setText("Select a plant to be edited");
        btnPlantEdited.setVisible(true);
        activatePane(editPane, createPane);
    }

    private void activatePane(AnchorPane activatedPane, AnchorPane disabledPane) {
        activatedPane.setVisible(true);
        activatedPane.setDisable(false);
        disabledPane.setVisible(false);
        disabledPane.setDisable(true);
        activatedPane.toFront();
    }


    @FXML
    PlantElement deletePlant(ActionEvent event) {

        for(PlantElement element: plantElements){
            if(element.isSelected()){
                plantElements.remove(element);
                plantVBox.getChildren().remove(element);
                return element;
            }
        }
        return null;
    }



    @FXML
    void createPlant(ActionEvent event) {
        Plant plant = new Plant(Integer.parseInt(field_CreatePlantId.getText()), field_CreatePlantName.getText(), new AccessModifier());
        for(PlantElement element: plantElements){
            if(element.getPlant().equals(plant)){
                btnPlantCreated.setText("Plant name or ID already exists");
                return;
            }
        }
        PlantElement newPlantElement = new PlantElement(new Plant(Integer.parseInt(field_CreatePlantId.getText()), field_CreatePlantName.getText(), new AccessModifier()));
        plantElements.add(newPlantElement);
        plantVBox.getChildren().add(newPlantElement);
        btnPlantCreated.setText("A new plant has been created.");
    }

    @FXML
    void editPlant(ActionEvent event){
        boolean isElementSelected = false;
        for(PlantElement element: plantElements){
            if(element.isSelected()) {
                element.getPlant().setName(field_EditPlantName.getText());
                element.getPlant().setId(Integer.parseInt(field_EditPlantId.getText()));
                element.updateText();
                isElementSelected = true;
            }
        }
        if(!isElementSelected){
            btnPlantEdited.setText("A plant has to be selected first.");
        }
    }
}
