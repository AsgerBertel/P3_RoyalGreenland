package gui.plant_administration;

import directory.plant.AccessModifier;
import directory.plant.Plant;
import directory.plant.PlantManager;
import gui.DMSApplication;
import gui.PlantElement;
import gui.TabController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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

public class PlantAdministrationController implements TabController {

    @FXML
    private AnchorPane createPane;

    public VBox getPlantVBox() {
        return plantVBox;
    }

    @FXML
    private VBox plantVBox;

    ArrayList<PlantElement> plantElements = new ArrayList<>();

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

        for (Plant plant : PlantManager.getInstance().readFromJsonFile().getAllPlants()) {
            PlantElement plantElement = new PlantElement(plant);
            plantElement.setOnSelectedListener(() -> onPlantToggle(plantElement));
            plantElements.add(plantElement);
        }
        plantVBox.getChildren().addAll(plantElements);

        plantCountText.setText("(" + plantElements.size() + ")");
    }

    private void onPlantToggle(PlantElement plantElement) {
        for (PlantElement element : plantElements) {
            element.setSelected(false);
        }
        plantElement.setSelected(true);
        btnDeletePlant.setDisable(false);
        btnDeletePlant.setStyle("-fx-opacity: 1");
    }


    @FXML
    void createPlantSidebar(ActionEvent event) {
        activatePane(createPane, editPane);
        lblPlantCreated.setText("");
        lblPlantCreated.setVisible(true);
    }

    @FXML
    void editPlantSidebar(ActionEvent event) {
        lblPlantEdited.setText("Select a plant to be edited");
        lblPlantEdited.setVisible(true);
        activatePane(editPane, createPane);
    }

    private void activatePane(AnchorPane activatedPane, AnchorPane disabledPane) {
        activatedPane.setVisible(true);
        activatedPane.setDisable(false);
        disabledPane.setVisible(false);
        disabledPane.setDisable(true);
        activatedPane.toFront();
    }


    public ArrayList<PlantElement> getPlantElements() {
        return plantElements;
    }

    @FXML
    void deletePlant(ActionEvent event) {
        popup();
        btnDeletePlant.setDisable(true);
        btnDeletePlant.setOpacity(0.5);
    }

    @FXML
    void btnCreatePlant(ActionEvent event) {
        createPlant();
    }

    @FXML
    void btnEditPlant(ActionEvent event) {
        editPlant();
    }

    void createPlant() {
        Plant plant = new Plant(Integer.parseInt(field_CreatePlantId.getText()), field_CreatePlantName.getText(), new AccessModifier());
        for (PlantElement element : plantElements) {
            if (element.getPlant().equals(plant)) {
                lblPlantCreated.setText("Plant name or ID already exists");
                lblPlantCreated.setVisible(true);
                return;
            }
        }
        PlantElement newPlantElement = new PlantElement(plant);
        PlantManager.getInstance().addPlant(plant);
        plantElements.add(newPlantElement);
        newPlantElement.setOnSelectedListener(() -> onPlantToggle(newPlantElement));
        plantVBox.getChildren().add(newPlantElement);
        lblPlantCreated.setText("Plant created");
        lblPlantCreated.setVisible(true);
        field_CreatePlantName.setText("");
        field_CreatePlantId.setText("");
        plantCountText.setText(" (" + plantElements.size() + ")");
    }

    void editPlant() {
        boolean isElementSelected = false;
        for (PlantElement element : plantElements) {
            if (element.isSelected()) {
                element.getPlant().setName(field_EditPlantName.getText());
                element.getPlant().setId(Integer.parseInt(field_EditPlantId.getText()));
                element.updateText();
                isElementSelected = true;
            }
        }
        if (!isElementSelected) {
            lblPlantEdited.setText("A plant has to be selected first.");
        }
        field_EditPlantName.clear();
        field_EditPlantId.clear();
    }

    @FXML
    void keyPressedCreate(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            createPlant();
        }
    }

    @FXML
    void keyPressedEdit(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            editPlant();
        }
    }

    public void popup() {
        Alert popup = new Alert(Alert.AlertType.CONFIRMATION, DMSApplication.getMessage("PlantAdmin.DeletePopup.DeleteTitle"));
        btnPressedPopup(popup);
    }

    public PlantElement btnPressedPopup(Alert popup) {
        for (PlantElement element : plantElements) {
            if (element.isSelected()) {
                popup.setTitle(DMSApplication.getMessage("PlantAdmin.DeletePopup.DeleteTitle"));
                popup.setHeaderText(DMSApplication.getMessage("PlantAdmin.DeletePopup.Info"));
                popup.setContentText(DMSApplication.getMessage("PlantAdmin.DeletePopup.YouSure"));
                Optional<ButtonType> result = popup.showAndWait();
                if(!result.isPresent())
                    popup.close();
                if (result.get() == ButtonType.OK) {
                    plantElements.remove(element);
                    PlantManager.getInstance().deletePlant(element.getPlant().getId());
                    plantVBox.getChildren().remove(element);
                    btnDeletePlant.setDisable(true);
                    btnDeletePlant.setStyle("-fx-opacity: 0.5");
                    plantCountText.setText(" (" + plantElements.size() + ")");
                    return element;
                }
                if(result.get() == ButtonType.CANCEL){
                    popup.close();
                }
            }
        }
        return null;
    }
}