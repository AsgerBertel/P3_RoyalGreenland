package gui.file_administration;

import directory.files.AbstractFile;
import directory.files.Folder;
import directory.plant.AccessModifier;
import directory.plant.Plant;
import gui.FileTreeGenerator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.util.Callback;

import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class FileAdminController implements Initializable {

    @FXML
    private ListView<Plant> factoryListView;
    private List<Plant> plants = new ArrayList<>();

    @FXML
    private TreeView<AbstractFile> fileTreeView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Folder rootFolder = new Folder(Paths.get(System.getProperty("user.dir") + "/Sample Files/Main Files")); // todo Fetch path from main
        TreeItem<AbstractFile> rootItem = FileTreeGenerator.generateTree(rootFolder);
        fileTreeView.setRoot(rootItem);

        for(int i = 0; i < 15; i++){
            plants.add(new Plant(1243 + i, "Navn", new AccessModifier()));
        }


        factoryListView.setCellFactory(new Callback<ListView<Plant>, ListCell<Plant>>() {

            @Override
            public ListCell<Plant> call(ListView<Plant> param) {
                ListCell<Plant> cell = new ListCell<Plant>() {

                    @Override
                    protected void updateItem(Plant item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {

                        } else {
                            setText("");
                        }
                    }
                };
                return cell;
            }
        });
        factoryListView.getItems().addAll(plants);


    }


    public void addDocument(ActionEvent actionEvent) {

    }

    public void createFolder(ActionEvent actionEvent) {
    }

    public void deleteFile(ActionEvent actionEvent) {
    }


}
