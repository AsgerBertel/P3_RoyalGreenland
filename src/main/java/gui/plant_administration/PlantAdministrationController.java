package gui.plant_administration;

import directory.plant.AccessModifier;
import directory.plant.Plant;
import directory.plant.PlantManager;
import gui.DMSApplication;
import gui.PlantElement;
import gui.TabController;
import gui.log.LogEvent;
import gui.log.LoggingTools;
import gui.settings.SettingsController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

import static gui.log.LogEventType.*;

public class PlantAdministrationController implements TabController {

    ArrayList<PlantElement> plantElements = new ArrayList<>();

    PlantElement selectedPlantElement = null;

    @FXML
    private AnchorPane createPane;

    @FXML
    private VBox plantVBox;

    @FXML
    private AnchorPane editPane;

    @FXML
    private Label lblPlantEdited;

    @FXML
    private Label lblPlantCreated;

    @FXML
    private TextField field_CreatePlantName;

    @FXML
    private TextField field_CreatePlantId;

    @FXML
    private TextField field_EditPlantName;

    @FXML
    private TextField field_EditPlantId;

    @FXML
    private Button btnDeletePlant;

    @FXML
    private Text plantCountText;

    public VBox getPlantVBox() {
        return plantVBox;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Setting standard
        createPane.toFront();
        createPane.setVisible(true);
        btnDeletePlant.setDisable(true);
    }


    @Override
    public void update() {
        plantElements.clear();
        plantVBox.getChildren().clear();

        // plantManager.updateFromServer(); todo necessary? This method didn't work before so it's now removed - Magnus
        for (Plant plant : PlantManager.getInstance().getAllPlants()) {
            PlantElement plantElement = new PlantElement(plant);
            plantElement.setOnSelectedListener(() -> onPlantToggle(plantElement));
            plantElements.add(plantElement);
        }
        plantVBox.getChildren().addAll(plantElements);

        plantCountText.setText("(" + plantElements.size() + ")");
    }
    //Select plant function
    private void onPlantToggle(PlantElement plantElement) {
        for (PlantElement element : plantElements) {
            element.setSelected(false);
        }
        plantElement.setSelected(true);
        btnDeletePlant.setDisable(false);
        btnDeletePlant.setStyle("-fx-opacity: 1");

        selectedPlantElement = plantElement;
    }

    //Button function when "create plant" button in sidebar is pressed.
    @FXML
    void createPlantSidebar(ActionEvent event) {
        activatePane(createPane, editPane);
        lblPlantCreated.setText("");
        lblPlantCreated.setVisible(true);
    }
    //Button function when "edit plant" button in sidebar is pressed.
    @FXML
    void editPlantSidebar(ActionEvent event) {
        lblPlantEdited.setText(DMSApplication.getMessage("PlantAdmin.LblEditPlant"));
        lblPlantEdited.setVisible(true);
        activatePane(editPane, createPane);

        field_EditPlantName.setText(selectedPlantElement.getPlant().getName());
        field_EditPlantId.setText("" + selectedPlantElement.getPlant().getId());
    }


    //Function to switch between panes. Used to switch "Create pane" and "edit pane".
    private void activatePane(AnchorPane activatedPane, AnchorPane disabledPane) {
        activatedPane.setVisible(true);
        activatedPane.setDisable(false);
        disabledPane.setVisible(false);
        disabledPane.setDisable(true);
        activatedPane.toFront();
    }

    //Button function when "Delete plant" button in sidebar is pressed.
    @FXML
    void deletePlant(ActionEvent event) {
        deletePopUp();
        btnDeletePlant.setDisable(true);
        btnDeletePlant.setOpacity(0.5);
    }

    @FXML
    void btnCreatePlant(ActionEvent event) {
        createPlant();
    }

    @FXML
    void btnEditPlant(ActionEvent event) {
        savePlantEdit();
    }
    //Create plant function gets text from user and creates a new plant. Adds them to
    //both thePlantManager and the ArrayList of plants.
    void createPlant() {
        try {
            Plant newPlant = new Plant(Integer.parseInt(field_CreatePlantId.getText()), field_CreatePlantName.getText(), new AccessModifier());
            for (PlantElement element : plantElements) {
                Plant oldPlant = element.getPlant();
                if (oldPlant.getName().equals(oldPlant.getName()) || oldPlant.getId() == newPlant.getId()) {
                    lblPlantCreated.setText(DMSApplication.getMessage("PlantAdmin.IdAlreadyExists"));
                    lblPlantCreated.setVisible(true);
                    if(oldPlant.getName().equals(newPlant.getName())){
                        addErrorClass(field_CreatePlantName);
                    }else{
                        addErrorClass(field_CreatePlantId);
                    }
                    return;
                }
            }
            PlantElement newPlantElement = new PlantElement(newPlant);
            PlantManager.getInstance().addPlant(newPlant);
            plantElements.add(newPlantElement);
            newPlantElement.setOnSelectedListener(() -> onPlantToggle(newPlantElement));
            plantVBox.getChildren().add(newPlantElement);
            lblPlantCreated.setText(DMSApplication.getMessage("PlantAdmin.PlantCreated"));
            lblPlantCreated.setVisible(true);
            field_CreatePlantName.setText("");
            field_CreatePlantId.setText("");
            plantCountText.setText("(" + plantElements.size() + ")");

            LoggingTools.log(new LogEvent( DMSApplication.getMessage("Log.Plant") + " " + newPlant.getName() + ", " + newPlant.getId(), PLANT_CREATED));
        } catch (NumberFormatException e) {
            lblPlantCreated.setText(DMSApplication.getMessage("PlantAdmin.ErrorMessagePlantID"));
        }
    }

    private void addErrorClass(Node node){
        if(node.getStyleClass().contains(SettingsController.ERROR_STYLE_CLASS))
            node.getStyleClass().add(SettingsController.ERROR_STYLE_CLASS);
    }

    private void removeErrorClass(Node node){
        if(!node.getStyleClass().contains(SettingsController.ERROR_STYLE_CLASS))
            node.getStyleClass().remove(SettingsController.ERROR_STYLE_CLASS);
    }

    //Edit plant function checks if a plant is selected. If so, replaces the current name and
    // ID with new values.
    void savePlantEdit() {
        String oldName;
        String newName;
        int oldID;
        int newID;

        try {
            //boolean isElementSelected = false;
            if(selectedPlantElement != null){
                Plant selectedPlant = selectedPlantElement.getPlant();
                oldName = selectedPlant.getName();
                oldID = selectedPlant.getId();

                newName = field_EditPlantName.getText();
                newID = Integer.parseInt(field_EditPlantId.getText());
                selectedPlant.setName(newName);
                selectedPlant.setId(newID);
                update();
                lblPlantEdited.setText(DMSApplication.getMessage("PlantAdmin.PlantEdited"));
                String logmsg = "(" + DMSApplication.getMessage("Log.Plant") + ": " + oldName +  ", " + oldID + " )" + " -> " + " (" + DMSApplication.getMessage("Log.Plant") + ", " + newName + ", " + newID + " )";
                LoggingTools.log(new LogEvent(logmsg, PLANT_EDITED));
            } else{
                lblPlantEdited.setText("PlantAdmin.SelectPlant");
            }

            field_EditPlantName.clear();
            field_EditPlantId.clear();
        } catch(NumberFormatException e) {
            lblPlantEdited.setText(DMSApplication.getMessage("PlantAdmin.ErrorMessagePlantID"));
        }

    }
    //Function to make it possible to press ENTER to create a plant.
    @FXML
    void keyPressedCreate(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            createPlant();
        }
    }
    //Function to make it possible to press ENTER to edit a plant.
    @FXML
    void keyPressedEdit(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            savePlantEdit();
        }
    }
    //Making Alert deletePopUp for delete plant function.
    public void deletePopUp() {
        Alert popup = new Alert(Alert.AlertType.CONFIRMATION, DMSApplication.getMessage("PlantAdmin.Popup.DeleteTitle"));
        btnDeletePressedPopup(popup);
    }
    //Popup function to determine action when pressed "OK" or "Cancel".
    //Pressing yes, deletes the plant from the PlantManager and the Arraylist. Pressing no closes the Alert.
    public PlantElement btnDeletePressedPopup(Alert popup) {
        if(selectedPlantElement != null){
                popup.setTitle(DMSApplication.getMessage("PlantAdmin.Popup.DeleteTitle"));
                popup.setHeaderText(DMSApplication.getMessage("PlantAdmin.Popup.Info"));
                popup.setContentText(DMSApplication.getMessage("PlantAdmin.Popup.YouSure"));
                ((Button) popup.getDialogPane().lookupButton(ButtonType.OK)).setText(DMSApplication.getMessage("PlantAdmin.Popup.Delete"));
                ((Button) popup.getDialogPane().lookupButton(ButtonType.CANCEL)).setText(DMSApplication.getMessage("PlantAdmin.Popup.Cancel"));
                Optional<ButtonType> result = popup.showAndWait();
                if (!result.isPresent())
                    popup.close();
                if (result.get() == ButtonType.OK) {
                    plantElements.remove(selectedPlantElement);
                    PlantManager.getInstance().deletePlant(selectedPlantElement.getPlant().getId());
                    plantVBox.getChildren().remove(selectedPlantElement);
                    btnDeletePlant.setDisable(true);
                    btnDeletePlant.setStyle("-fx-opacity: 0.5");
                    plantCountText.setText("(" + plantElements.size() + ")");

                    LoggingTools.log(new LogEvent(DMSApplication.getMessage("Log.Plant") + " " + selectedPlantElement.getPlant().getName() + ", " + selectedPlantElement.getPlant().getId(), PLANT_DELETED));
                    return selectedPlantElement;
                }
                if (result.get() == ButtonType.CANCEL) {
                    popup.close();
                }
        }
        return null;
    }
}