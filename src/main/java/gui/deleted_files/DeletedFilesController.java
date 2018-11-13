package gui.deleted_files;

import directory.FileExplorer;
import directory.FileManager;
import directory.files.AbstractFile;
import directory.plant.Plant;
import directory.plant.PlantManager;
import gui.FileTreeGenerator;
import gui.TabController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DeletedFilesController implements TabController {

    private FileExplorer fileExplorer;

    private ObservableList<Plant> plantList;

    private TreeItem<AbstractFile> rootItem;

    List<AbstractFile> filesToShow;

    @FXML
    private VBox leftSideVbox;

    @FXML
    private TreeView<AbstractFile> fileTreeView;

    @FXML
    private VBox vboxTop;

    @FXML
    private Button btnReturn;

    @FXML
    private Label lblVisualPath;

    @FXML
    private ScrollPane scpFileView;

    @FXML
    private FlowPane flpFileView;

    @FXML
    void getSelectedPlant(ActionEvent event) {

    }

    @FXML
    void openPreviusFolder(ActionEvent event) {

    }

    @Override
    public void update() {
        rootItem = FileTreeGenerator.generateTree(FileManager.getInstance().getArchive().get(0));
        fileTreeView.setRoot(rootItem);
    }

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        update();
    }
}
