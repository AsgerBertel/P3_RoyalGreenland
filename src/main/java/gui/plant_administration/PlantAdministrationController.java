package gui.plant_administration;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ResourceBundle;

public class PlantAdministrationController implements Initializable{

    @FXML
    private AnchorPane createPane;


    @FXML
    private AnchorPane editPane;

    @FXML
    private StackPane switchStackPane;
    ObservableList<Node> panes;

    @Override
    public void initialize(URL location, ResourceBundle resources){
        createPane.toFront();
        editPane.setDisable(true);
        editPane.setVisible(false);
    }


    @FXML
    void createPlantSidebar(ActionEvent event) {
        activateCreatePane(createPane, editPane);
    }

    @FXML
    void editPlantSidebar(ActionEvent event) {
        activateCreatePane(editPane, createPane);
    }

    private void activateCreatePane(AnchorPane activatedPane, AnchorPane disabledPane) {
        activatedPane.setVisible(true);
        activatedPane.setDisable(false);
        disabledPane.setVisible(false);
        disabledPane.setDisable(true);
        activatedPane.toFront();
    }


    @FXML
    void deletePlant(ActionEvent event) {

    }



    @FXML
    void createPlant(ActionEvent event) {

    }
    @FXML
    void editPlant(ActionEvent event){

    }



}
