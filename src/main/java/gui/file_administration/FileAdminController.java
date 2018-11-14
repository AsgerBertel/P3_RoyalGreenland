package gui.file_administration;

import directory.FileManager;
import directory.files.AbstractFile;
import directory.files.Document;
import directory.files.Folder;
import directory.plant.Plant;
import directory.plant.PlantManager;
import gui.FileTreeUtil;
import gui.PlantCheckboxElement;

import gui.TabController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.IOException;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
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
    private AbstractFile selectedFile;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rootFolder = (Folder) FileManager.getInstance().getAllContent().get(0);

        setFactoryListDisabled(true);

        rootItem = FileTreeUtil.generateTree(rootFolder);
        fileTreeView.setRoot(rootItem);
        fileTreeView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> onTreeItemSelected(oldValue, newValue));

        update();
    }

    @Override
    public void update() {
        plants.clear();
        plantElements.clear();
        plantVBox.getChildren().clear();

        plants.addAll(PlantManager.getInstance().getAllPlants());

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
            plant.getAccessModifier().addDocument(((Document) selectedFile).getID());
        } else {
            plant.getAccessModifier().removeDocument(((Document) selectedFile).getID());
        }
    }

    // Called when an item (containing an AbstractFile) is clicked in the FileTreeView
    public void onTreeItemSelected(TreeItem<AbstractFile> oldValue, TreeItem<AbstractFile> newValue) {
        if (newValue != null && newValue != oldValue) {
            AbstractFile chosenFile = newValue.getValue();
            clearPlantSelection();

            if (chosenFile instanceof Document) {
                selectedFile = chosenFile;
                setFactoryListDisabled(false);
                onDocumentSelected();
            } else if (chosenFile instanceof Folder) {
                setFactoryListDisabled(true);
                selectedFile = chosenFile;
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
    private void onDocumentSelected() {

        Document document = (Document) selectedFile;
        for (PlantCheckboxElement element : plantElements) {
            if (element.getPlant().getAccessModifier().contains(document.getID()))
                element.setSelected(true);
        }
    }

    public void addDocument(ActionEvent actionEvent) {
        JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

        int returnValue = jfc.showOpenDialog(null);
        // int returnValue = jfc.showSaveDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File uploadedFile = jfc.getSelectedFile();

            if (selectedFile instanceof Folder) {
                FileManager.getInstance().uploadFile(Paths.get(uploadedFile.getAbsolutePath()), (Folder) selectedFile);
                update();
            } else if (selectedFile instanceof Document) {
                System.out.println("popup med dokument valgt istedet for folder");
            }
        }

        //todo if they upload a file that already exists (or choose a file)
        //todo it should move the old file to archive and replace it with the new uploaded file
    }

    public void createFolder(ActionEvent actionEvent) {
        String folderName = JOptionPane.showInputDialog("Skriv navnet på folderen");

        FileManager.getInstance().createFolder((Folder)selectedFile, folderName);

    }

    public void deleteFile(ActionEvent actionEvent) throws IOException {
        FileManager.getInstance().deleteFile(selectedFile);
    }


}
