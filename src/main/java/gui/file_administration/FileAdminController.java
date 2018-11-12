package gui.file_administration;

import directory.FileManager;
import directory.files.AbstractFile;
import directory.files.Document;
import directory.files.Folder;
import directory.plant.Plant;
import directory.plant.PlantManager;
import gui.FileTreeGenerator;
import gui.PlantCheckboxElement;

import gui.TabController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class FileAdminController implements TabController {

    private Folder rootFolder;

    private ArrayList<PlantCheckboxElement> plantElements = new ArrayList<>();

    private ArrayList<Plant> plants = new ArrayList<>();

    private TreeItem<AbstractFile> rootItem;

    @FXML
    public Text plantListTitle;

    @FXML
    private VBox plantVBox;

    @FXML
    private TreeView<AbstractFile> fileTreeView;

    @FXML
    private Text plantCountText;

    // The document last selected in the FileTree
    private Document selectedDocument;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rootFolder = (Folder) FileManager.getInstance().getAllContent().get(0);

        setFactoryListDisabled(true);

        rootItem = FileTreeGenerator.generateTree(rootFolder);
        fileTreeView.setRoot(rootItem);
        fileTreeView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> onTreeItemSelected(oldValue, newValue));

        update();
    }

    @Override
    public void update() {
        plants.clear();
        plantElements.clear();
        plants = PlantManager.getInstance().readFromJsonFile().getAllPlants();


        plantVBox.getChildren().clear();

        for (Plant plant : plants) {
            PlantCheckboxElement checkBox = new PlantCheckboxElement(plant);
            checkBox.setOnSelectedListener(() -> onPlantToggle(checkBox));
            plantVBox.getChildren().add(checkBox);
            plantElements.add(checkBox);
        }

        plantCountText.setText("(" + plants.size() + ")");

        onTreeItemSelected(null, fileTreeView.getSelectionModel().getSelectedItem());

        setFactoryListDisabled(true);

    }

    // Called after a plant is toggled on or off in plant checklist
    private void onPlantToggle(PlantCheckboxElement plantElement) {
        Plant plant = plantElement.getPlant();

        if (plantElement.isSelected()) {
            plant.getAccessModifier().addDocument(selectedDocument.getID());
        } else {
            plant.getAccessModifier().removeDocument(selectedDocument.getID());
        }

        PlantManager.getInstance().updateJsonFile();
    }

    // Called when an item (containing an AbstractFile) is clicked in the FileTreeView
    public void onTreeItemSelected(TreeItem<AbstractFile> oldValue, TreeItem<AbstractFile> newValue) {
        if (newValue != null && newValue != oldValue) {
            AbstractFile chosenFile = newValue.getValue();
            clearPlantSelection();

            if (chosenFile instanceof Document) {
                setFactoryListDisabled(false);
                onDocumentSelected((Document) chosenFile);
            } else if (chosenFile instanceof Folder) {
                setFactoryListDisabled(true);
                selectedDocument = null;
            }
        }
    }

    // Disables clicking on elements in the factory list
    private void setFactoryListDisabled(boolean disabled) {
        for (PlantCheckboxElement element : plantElements)
            element.setDisable(disabled);
    }

    // Deselects all elements in the plant list
    private void clearPlantSelection() {
        for (PlantCheckboxElement element : plantElements)
            element.setSelected(false);
    }

    // Updates the plant list to reflect the AccessModifier of the chosen document
    private void onDocumentSelected(Document document) {
        selectedDocument = document;

        for (PlantCheckboxElement element : plantElements) {
            if (element.getPlant().getAccessModifier().contains(selectedDocument.getID()))
                element.setSelected(true);
        }
    }

    public void addDocument(ActionEvent actionEvent) {

    }

    public void createFolder(ActionEvent actionEvent) {
    }

    public void deleteFile(ActionEvent actionEvent) {
    }

}
