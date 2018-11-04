package gui.file_administration;

import directory.files.AbstractFile;
import directory.files.Folder;
import directory.plant.AccessModifier;
import directory.plant.Plant;
import gui.FileTreeGenerator;
import gui.PlantCheckboxElement;
import gui.PlantElement;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class FileAdminController implements Initializable {

    @FXML
    public Text plantListTitle;

    @FXML
    private VBox plantVBox;

    private List<Plant> plants = new ArrayList<>();

    @FXML
    private TreeView<AbstractFile> fileTreeView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Folder rootFolder = new Folder(Paths.get(System.getProperty("user.dir") + "/Sample Files/Main Files")); // todo Fetch path from some class
        TreeItem<AbstractFile> rootItem = FileTreeGenerator.generateTree(rootFolder);
        fileTreeView.setRoot(rootItem);

        for (int i = 0; i < 15; i++) {
            plants.add(new Plant(1243 + i, "NUUK", new AccessModifier()));
            plantVBox.getChildren().add(new PlantCheckboxElement(plants.get(i)));
        }



    }


    public void addDocument(ActionEvent actionEvent) {

    }

    public void createFolder(ActionEvent actionEvent) {
    }

    public void deleteFile(ActionEvent actionEvent) {
    }


}
