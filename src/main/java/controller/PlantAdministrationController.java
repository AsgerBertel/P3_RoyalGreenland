package controller;

import model.AccessModifier;
import model.Plant;
import model.managing.PlantManager;
import gui.AlertBuilder;
import app.DMSApplication;
import gui.custom_node.PlantElement;
import log.LogEvent;
import log.LogManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

import static log.LogEventType.*;

public class PlantAdministrationController implements TabController {

    @FXML
    private Button btnSavePlantEdit;
    public Button btnCreatePlant;

    private final ArrayList<PlantElement> plantElements = new ArrayList<>();

    private PlantElement selectedPlantElement = null;

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
    private Button btnDeletePlantSidebar;

    @FXML
    private Button btnEditPlantSidebar;
    @FXML
    private Button btnCreatePlantSidebar;


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
        addToolTip();
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
            if(selectedPlantElement != null && plantElement.getPlant().getName().equals(selectedPlantElement.getPlant().getName()))
                plantElement.setSelected(true);
        }
        plantVBox.getChildren().addAll(plantElements);

        btnDeletePlantSidebar.setDisable(true);
        btnEditPlantSidebar.setDisable(true);

        plantCountText.setText("(" + plantElements.size() + ")");
    }

    //Select plant function
    private void onPlantToggle(PlantElement plantElement) {
        if(selectedPlantElement != null && selectedPlantElement.equals(plantElement))
            return;

        for (PlantElement element : plantElements) {
            element.setSelected(false);
        }
        plantElement.setSelected(true);
        btnDeletePlantSidebar.setDisable(false);
        btnEditPlantSidebar.setDisable(false);

        selectedPlantElement = plantElement;

        fieldEditPlantName.setText(selectedPlantElement.getPlant().getName());
        fieldEditPlantId.setText(Integer.toString(selectedPlantElement.getPlant().getId()));
        lblPlantEdited.setText("");

        btnSavePlantEdit.setDisable(true);
    }

    //Button function when "create plant" button in sidebar is pressed.
    @FXML
    public void createPlantSidebar() {
        activatePane(createPane, editPane);
        lblPlantCreated.setText("");
        lblPlantCreated.setVisible(true);
    }

    //Button function when "edit plant" button in sidebar is pressed.
    @FXML
    public void editPlantSidebar() {
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
    public void deletePlant() {
        if(confirmDeletePopup()){
            plantElements.remove(selectedPlantElement);
            PlantManager.getInstance().deletePlant(selectedPlantElement.getPlant().getId());
            plantVBox.getChildren().remove(selectedPlantElement);
            btnDeletePlantSidebar.setDisable(true);
            plantCountText.setText("(" + plantElements.size() + ")");

            LogManager.log(new LogEvent(DMSApplication.getMessage("Log.Plant") + " " + selectedPlantElement.getPlant().getName() + ", " + selectedPlantElement.getPlant().getId(), PLANT_DELETED));
            activatePane(createPane, editPane);

            btnDeletePlantSidebar.setDisable(true);
            btnEditPlantSidebar.setDisable(true);
            selectedPlantElement = null;
        }
    }

    //Making Alert confirmDeletePopup for delete plant function.
    private boolean confirmDeletePopup() {
        if (selectedPlantElement != null) {
            Alert deletePopup = AlertBuilder.deletePlantPopUp();
            Optional<ButtonType> result = deletePopup.showAndWait();
            if (!result.isPresent()){
                deletePopup.close();
                return false;
            }
            return result.get() == ButtonType.OK;
        }
        return false;
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
    private void createPlant() {
        try {
            // Create plant from the input given in the text fields
            Plant newPlant = new Plant(Integer.parseInt(fieldCreatePlantId.getText()), fieldCreatePlantName.getText(), new AccessModifier());

            // Check all plants for potential duplicates
            for (PlantElement element : plantElements) {
                Plant oldPlant = element.getPlant();
                if (oldPlant.getName().equals(newPlant.getName()) || oldPlant.getId() == newPlant.getId()) {
                    lblPlantCreated.setText(DMSApplication.getMessage("PlantAdmin.IdAlreadyExists"));
                    lblPlantCreated.setVisible(true);

                    // Add error class to text fields to indicate error to the user
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

            // Create PlantElement and add it to the view
            PlantElement newPlantElement = new PlantElement(newPlant);
            PlantManager.getInstance().addPlant(newPlant);
            plantElements.add(newPlantElement);
            newPlantElement.setOnSelectedListener(() -> onPlantToggle(newPlantElement));
            plantVBox.getChildren().add(newPlantElement);
            lblPlantCreated.setText(DMSApplication.getMessage("PlantAdmin.PlantCreated"));
            lblPlantCreated.setVisible(true);

            // Reset create plant text fields
            fieldCreatePlantName.setText("");
            fieldCreatePlantId.setText("");
            plantCountText.setText("(" + plantElements.size() + ")");

            LogManager.log(new LogEvent(DMSApplication.getMessage("Log.Plant") + " " + newPlant.getName() + ", " + newPlant.getId(), PLANT_CREATED));
            update();
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
        node.getStyleClass().remove(SettingsController.ERROR_STYLE_CLASS);
    }

    //Edit plant function checks if a plant is selected. If so, replaces the current name and
    // ID with new values.
    private void savePlantEdit() {
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

                        // Add error class to indicate duplicate name or id to the user
                        valid = false;
                        if (plant.getId() == newID)
                            addErrorClass(fieldEditPlantId);

                        if (plant.getName().equals(newName))
                            addErrorClass(fieldEditPlantName);
                    }
                }

                if(!valid)
                    return;

                // Save the changes
                selectedPlant.setName(newName);
                selectedPlant.setId(newID);

                btnSavePlantEdit.setDisable(true);
                update();

                lblPlantEdited.setText(DMSApplication.getMessage("PlantAdmin.PlantEdited"));
                String logmsg = "(" + DMSApplication.getMessage("Log.Plant") + ": " + oldName + ", " + oldID + " )" + " -> " + " (" + DMSApplication.getMessage("Log.Plant") + ", " + newName + ", " + newID + " )";
                LogManager.log(new LogEvent(logmsg, PLANT_EDITED));
            } else {
                lblPlantEdited.setText(DMSApplication.getMessage("PlantAdmin.SelectPlant"));
            }
        } catch (NumberFormatException e) {
            lblPlantEdited.setText(DMSApplication.getMessage("PlantAdmin.ErrorMessagePlantID"));
            addErrorClass(fieldEditPlantId);
        }
    }

    private boolean inputHasError(){
        if(editPane.isVisible()){
            return fieldEditPlantId.getStyleClass().contains(SettingsController.ERROR_STYLE_CLASS) ||
                    fieldEditPlantName.getStyleClass().contains(SettingsController.ERROR_STYLE_CLASS);
        }else{
            return fieldCreatePlantId.getStyleClass().contains(SettingsController.ERROR_STYLE_CLASS) ||
                    fieldCreatePlantName.getStyleClass().contains(SettingsController.ERROR_STYLE_CLASS);
        }
    }

    //Function to make it possible to press ENTER to create a plant.
    @FXML
    void keyPressedCreate(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            createPlant();
        } else {
            validateInputs();
        }
    }

    //Function to make it possible to press ENTER to edit a plant.
    @FXML
    void keyPressedEdit(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            savePlantEdit();
        } else {
            validateInputs();
            btnSavePlantEdit.setDisable(inputHasError());
        }
    }

    // Validates the inputs in the text fields.
    // Add error indication if the input is invalid. Remove error if input is valid.
    private void validateInputs(){
        TextField nameTextField, idTextField;
        Button saveButton;

        if(editPane.isVisible()){
            nameTextField = fieldEditPlantName;
            idTextField = fieldEditPlantId;
        }else{
            nameTextField = fieldCreatePlantName;
            idTextField = fieldCreatePlantId;
        }

        removeErrorClass(nameTextField);
        removeErrorClass(idTextField);

        if(nameTextField.getText().isEmpty())
            addErrorClass(nameTextField);

        if(idTextField.getText().isEmpty() || !idTextField.getText().matches("[0-9]+"))
            addErrorClass(idTextField);
    }


    public PlantElement previousSelectedPlant() {
        for (PlantElement element : plantElements) {
            if (element.isSelected())
                return element;
        }
        return null;
    }

    private void addToolTip() {
        Tooltip deletePlantTooltip = new Tooltip(DMSApplication.getMessage("PlantAdmin.Tooltip.DeletePlant"));
        Tooltip newPlantTooltip = new Tooltip(DMSApplication.getMessage("PlantAdmin.Tooltip.CreatePlant"));
        Tooltip editPlantTooltip = new Tooltip(DMSApplication.getMessage("PlantAdmin.Tooltip.EditPlant"));
        Tooltip.install(btnDeletePlantSidebar, deletePlantTooltip);
        Tooltip.install(btnCreatePlantSidebar, newPlantTooltip);
        Tooltip.install(btnEditPlantSidebar, editPlantTooltip);
    }
}