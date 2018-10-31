package gui.file_administration;

import directory.files.AbstractFile;
import directory.files.Folder;
import directory.plant.AccessModifier;
import directory.plant.Plant;
import gui.FileTreeGenerator;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.control.cell.CheckBoxTableCell;
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

        for (int i = 0; i < 15; i++) {
            plants.add(new Plant(1243 + i, "NAVN", new AccessModifier()));
        }

        // todo Fy for satan det er ulækkert det her
        factoryListView.setCellFactory(new Callback<ListView<Plant>, ListCell<Plant>>() {
            @Override
            public CheckBoxListCell<Plant> call(ListView<Plant> param) {
                CheckBoxListCell<Plant> cell = new CheckBoxListCell<>() {
                    @Override
                    public void updateItem(Plant item, boolean empty) {
                        setSelectedStateCallback(new Callback<Plant, ObservableValue<Boolean>>() {
                            @Override
                            public ObservableValue<Boolean> call(Plant item) {
                                BooleanProperty observable = new SimpleBooleanProperty();

                                observable.addListener((obs, wasSelected, isNowSelected) ->
                                        System.out.println("Check box for " + item + " changed from " + wasSelected + " to " + isNowSelected)
                                );
                                return observable;
                            }
                        });
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(item.getId() + " - " + item.getName());
                        } else {
                            setText(null);
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
