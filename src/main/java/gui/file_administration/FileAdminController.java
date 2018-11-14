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
        setFactoryListDisabled(true);
        fileTreeView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> onTreeItemSelected(oldValue, newValue));
    }

    @Override
    public void update() {
        // Refresh file tree if the files have changed // todo test if functional
        TreeItem<AbstractFile> currentRoot = fileTreeView.getRoot();
        if(currentRoot == null || !currentRoot.getValue().equals(FileManager.getInstance().getAllContent().get(0)))
            reloadFileTree();



        reloadPlantList();
    }

    private void reloadFileTree(){
        System.out.println("test");
        rootFolder = (Folder) FileManager.getInstance().getAllContent().get(0);

        rootItem = FileTreeUtil.generateTree(rootFolder);
        fileTreeView.setRoot(rootItem);

        setFactoryListDisabled(true);
    }

    private void reloadPlantList(){
        plantElements.clear();
        plantVBox.getChildren().clear();

        plants.clear();
        plants.addAll(PlantManager.getInstance().getAllPlants());

        // Create all plant boxes and add them to the plantVBox
        for (Plant plant : plants) {
            PlantCheckboxElement checkBox = new PlantCheckboxElement(plant);
            checkBox.setOnSelectedListener(() -> onPlantToggle(checkBox));
            plantVBox.getChildren().add(checkBox);
            plantElements.add(checkBox);
        }

        plantCountText.setText("(" + plants.size() + ")");

        // Update selected plants according to the currently selected file
        onTreeItemSelected(null, fileTreeView.getSelectionModel().getSelectedItem());

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
                try {
                    FileManager.getInstance().uploadFile(Paths.get(uploadedFile.getAbsolutePath()), (Folder) selectedFile);
                } catch (IOException e) {
                    System.out.println("could not upload file");
                    e.printStackTrace();
                }
                update();
            } else if (selectedFile instanceof Document) {
                System.out.println("popup med dokument valgt istedet for folder");
            }
        }

        //todo if file already exists, the old one is deleted but this can only happen once.
        //todo make some kind of counter to file name
    }

    public void createFolder(ActionEvent actionEvent) {

        if (selectedFile instanceof Folder){
            String folderName = JOptionPane.showInputDialog("Skriv navnet på folderen");
            if(folderName != null) {
                FileManager.getInstance().createFolder((Folder) selectedFile, folderName);
            }
        } else {
            JOptionPane.showMessageDialog(new JFrame(), "Vælg en folder");
        }

    }

    public void deleteFile(ActionEvent actionEvent) throws IOException {
        FileManager.getInstance().deleteFile(selectedFile);
        TreeItem<AbstractFile> selectedItem = fileTreeView.getSelectionModel().getSelectedItem();
        selectedItem.getParent().getChildren().remove(selectedItem);
    }


}
