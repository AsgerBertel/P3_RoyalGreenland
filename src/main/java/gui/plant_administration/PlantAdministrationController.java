package gui.plant_administration;

import directory.plant.AccessModifier;
import directory.plant.Plant;
import directory.plant.PlantManager;
import gui.AlertBuilder;
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
    private TextField fieldCreatePlantName;

    @FXML
    private TextField fieldCreatePlantId;

    @FXML
    private TextField fieldEditPlantName;

    @FXML
    private TextField fieldEditPlantId;

    @FXML
    private Button btnDeletePlant;

    @FXML
    private Button btnEditPlantSidebar;

    @FXML
    private Text plantCountText;

    private DMSApplication dmsApplication;

    public VBox getPlantVBox() {
        return plantVBox;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Setting standard
        createPane.toFront();
        createPane.setVisible(true);
    }


    @Override
    public void initReference(DMSApplication dmsApplication) {
        this.dmsApplication = dmsApplication;
    }

    @Override
    public void update() {
        plantElements.clear();
        plantVBox.getChildren().clear();
        for (Plant plant : PlantManager.getInstance().getAllPlants()) {
            PlantElement plantElement = new PlantElement(plant, this);
            plantElement.setOnSelectedListener(() -> onPlantToggle(plantElement));
            plantElements.add(plantElement);
        }
        plantVBox.getChildren().addAll(plantElements);

        btnDeletePlant.setDisable(true);
        btnEditPlantSidebar.setDisable(true);

        plantCountText.setText("(" + plantElements.size() + ")");

    }

    //Select plant function
    private void onPlantToggle(PlantElement plantElement) {
        for (PlantElement element : plantElements) {
            element.setSelected(false);
        }
        plantElement.setSelected(true);
        btnDeletePlant.setDisable(false);
        btnEditPlantSidebar.setDisable(false);

        selectedPlantElement = plantElement;

        fieldEditPlantName.setText(selectedPlantElement.getPlant().getName());
        fieldEditPlantId.setText(Integer.toString(selectedPlantElement.getPlant().getId()));
        lblPlantEdited.setText("");
    }

    //Button function when "create plant" button in sidebar is pressed.
    @FXML
    void createPlantSidebar() {
        activatePane(createPane, editPane);
        lblPlantCreated.setText("");
        lblPlantCreated.setVisible(true);
    }

    //Button function when "edit plant" button in sidebar is pressed.
    @FXML
    void editPlantSidebar() {
        lblPlantEdited.setVisible(true);
        activatePane(editPane, createPane);

        fieldEditPlantName.setText(selectedPlantElement.getPlant().getName());
        fieldEditPlantId.setText("" + selectedPlantElement.getPlant().getId());
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
    void deletePlant() {
        deletePopUp();
        btnDeletePlant.setDisable(true);
        btnDeletePlant.setOpacity(0.5);
    }

    @FXML
    void onCreatePlant(ActionEvent event) {
        createPlant();
    }

    @FXML
    void onEditSave(ActionEvent event) {
        savePlantEdit();
    }

    //Create plant function gets text from user and creates a new plant. Adds them to
    //both thePlantManager and the ArrayList of plants.
    void createPlant() {
        try {
            Plant newPlant = new Plant(Integer.parseInt(fieldCreatePlantId.getText()), fieldCreatePlantName.getText(), new AccessModifier());
            for (PlantElement element : plantElements) {
                Plant oldPlant = element.getPlant();
                if (oldPlant.getName().equals(newPlant.getName()) || oldPlant.getId() == newPlant.getId()) {
                    lblPlantCreated.setText(DMSApplication.getMessage("PlantAdmin.IdAlreadyExists"));
                    lblPlantCreated.setVisible(true);
                    if(oldPlant.getId() == newPlant.getId()){
                        addErrorClass(fieldCreatePlantId);
                        fieldCreatePlantId.requestFocus();
                    }

                    if (oldPlant.getName().equals(newPlant.getName())) {
                        addErrorClass(fieldCreatePlantName);
                        fieldCreatePlantName.requestFocus();
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

            fieldCreatePlantName.setText("");
            fieldCreatePlantId.setText("");
            plantCountText.setText("(" + plantElements.size() + ")");

            LoggingTools.log(new LogEvent(DMSApplication.getMessage("Log.Plant") + " " + newPlant.getName() + ", " + newPlant.getId(), PLANT_CREATED));
        } catch (NumberFormatException e) {
            lblPlantCreated.setText(DMSApplication.getMessage("PlantAdmin.ErrorMessagePlantID"));
            addErrorClass(fieldEditPlantId);
        }
    }

    private void addErrorClass(Node node) {
        if (!node.getStyleClass().contains(SettingsController.ERROR_STYLE_CLASS))
            node.getStyleClass().add(SettingsController.ERROR_STYLE_CLASS);
    }

    private void removeErrorClass(Node node) {
        if (node.getStyleClass().contains(SettingsController.ERROR_STYLE_CLASS))
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
            if (selectedPlantElement != null) {
                Plant selectedPlant = selectedPlantElement.getPlant();
                oldName = selectedPlant.getName();
                oldID = selectedPlant.getId();


                newName = fieldEditPlantName.getText();
                newID = Integer.parseInt(fieldEditPlantId.getText());

                // Check if id or name already exists
                boolean valid = true;
                for (PlantElement plantElement : plantElements) {
                    Plant plant = plantElement.getPlant();
                    if (plant.getId() == newID || plant.getName().equals(newName)) {
                        // Continue if the duplicate is the same factory that is being edited
                        if (plant.getId() == oldID && plant.getName().equals(oldName))
                            continue;

                        valid = false;
                        // Otherwise indicate that id or name is already in use
                        if (plant.getId() == newID){
                            addErrorClass(fieldEditPlantId);
                        }

                        if (plant.getName().equals(newName)){
                            addErrorClass(fieldEditPlantName);
                        }
                    }
                }

                if(!valid)
                    return;

                // Save the change
                selectedPlant.setName(newName);
                selectedPlant.setId(newID);
                update();

                lblPlantEdited.setText(DMSApplication.getMessage("PlantAdmin.PlantEdited"));
                String logmsg = "(" + DMSApplication.getMessage("Log.Plant") + ": " + oldName + ", " + oldID + " )" + " -> " + " (" + DMSApplication.getMessage("Log.Plant") + ", " + newName + ", " + newID + " )";
                LoggingTools.log(new LogEvent(logmsg, PLANT_EDITED));
            } else {
                lblPlantEdited.setText("PlantAdmin.SelectPlant");
            }

            fieldEditPlantName.clear();
            fieldEditPlantId.clear();
        } catch (NumberFormatException e) {
            lblPlantEdited.setText(DMSApplication.getMessage("PlantAdmin.ErrorMessagePlantID"));
            addErrorClass(fieldEditPlantId);
        }
    }

    //Function to make it possible to press ENTER to create a plant.
    @FXML
    void keyPressedCreate(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            createPlant();
        } else {
            removeErrorClass(fieldCreatePlantName);
            removeErrorClass(fieldCreatePlantId);
        }
    }

    //Function to make it possible to press ENTER to edit a plant.
    @FXML
    void keyPressedEdit(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            savePlantEdit();
        } else {
            removeErrorClass(fieldEditPlantId);
            removeErrorClass(fieldEditPlantName);
        }

    }

    //Making Alert deletePopUp for delete plant function.
    public void deletePopUp() {
        btnDeletePressedPopup();
    }

    //Popup function to determine action when pressed "OK" or "Cancel".
    //Pressing yes, deletes the plant from the PlantManager and the Arraylist. Pressing no closes the Alert.
    public PlantElement btnDeletePressedPopup() {
        if (selectedPlantElement != null) {
            Alert deletePopup = AlertBuilder.deletePlantPopup();
            Optional<ButtonType> result = deletePopup.showAndWait();
            if (!result.isPresent())
                deletePopup.close();
            if (result.get() == ButtonType.OK) {
                plantElements.remove(selectedPlantElement);
                PlantManager.getInstance().deletePlant(selectedPlantElement.getPlant().getId());
                plantVBox.getChildren().remove(selectedPlantElement);
                btnDeletePlant.setDisable(true);
                plantCountText.setText("(" + plantElements.size() + ")");

                LoggingTools.log(new LogEvent(DMSApplication.getMessage("Log.Plant") + " " + selectedPlantElement.getPlant().getName() + ", " + selectedPlantElement.getPlant().getId(), PLANT_DELETED));
                return selectedPlantElement;
            }
            if (result.get() == ButtonType.CANCEL)
                deletePopup.close();
        }
        return null;
    }
}