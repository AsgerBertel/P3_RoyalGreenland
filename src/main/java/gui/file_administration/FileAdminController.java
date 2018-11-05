package gui.file_administration;

import directory.files.AbstractFile;
import directory.files.Document;
import directory.files.Folder;
import directory.plant.AccessModifier;
import directory.plant.Plant;
import gui.FileTreeGenerator;
import gui.PlantCheckboxElement;
import gui.PlantElement;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

public class FileAdminController implements Initializable {

    @FXML
    public Text plantListTitle;
    @FXML
    private VBox plantVBox;
    private ArrayList<PlantCheckboxElement> plantElements = new ArrayList<>();

    private List<Plant> plants = new ArrayList<>();

    @FXML
    private TreeView<AbstractFile> fileTreeView;

    private Document selectedDocument;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Folder rootFolder = new Folder(Paths.get(System.getProperty("user.dir") + "/Sample Files/Main Files")); // todo Fetch path from some class

        TreeItem<AbstractFile> rootItem = FileTreeGenerator.generateTree(rootFolder);
        fileTreeView.setRoot(rootItem);
        fileTreeView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> onTreeItemSelected(oldValue, newValue));

        for (int i = 0; i < 15; i++) {
            Plant p = new Plant(1243 + i, "NUUK", new AccessModifier());
            PlantCheckboxElement plantCheckboxElement = new PlantCheckboxElement(p);
            plantCheckboxElement.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> onPlantToggle(plantCheckboxElement));
            plantElements.add(plantCheckboxElement);
        }

        plantVBox.getChildren().addAll(plantElements);
        setFactoryListDisabled(true);
    }


    public void onPlantToggle(PlantCheckboxElement plantElement){
        System.out.println("Test");
    }

    public void onTreeItemSelected(TreeItem<AbstractFile> oldValue, TreeItem<AbstractFile> newValue){
        if(newValue != null && newValue != oldValue){
            AbstractFile chosenFile = newValue.getValue();
            clearPlantSelection();

            if(chosenFile instanceof Document){
                setFactoryListDisabled(false);
                onDocumentSelected((Document) chosenFile);
            }else if(chosenFile instanceof Folder){
                setFactoryListDisabled(true);
            }
        }
    }

    private void setFactoryListDisabled(boolean disabled){
        for(PlantCheckboxElement element : plantElements)
            element.setDisable(disabled);
    }

    private void clearPlantSelection(){
        for(PlantCheckboxElement element : plantElements)
            element.setSelected(false);
    }

    private void onDocumentSelected(Document document){
        selectedDocument = document;
    }

    public void addDocument(ActionEvent actionEvent) {

    }

    public void createFolder(ActionEvent actionEvent) {
    }

    public void deleteFile(ActionEvent actionEvent) {
    }


}
