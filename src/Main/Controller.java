package Main;

import Directory.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Controller {
    ContextMenu folderContextMenu = new ContextMenu();
    ContextMenu fileContextMenu = new ContextMenu();
    ContextMenuHandler contextMenuHandler = new ContextMenuHandler();
    DirectoryManager directoryManager = new DirectoryManager();

    private Path rootDirectory = Paths.get("C:\\");
    private FileExplorer fileExplorer;

    @FXML
    private TextField txtFolderName;
    @FXML
    private TableView tblFiles;
    @FXML
    private TableColumn<AbstractFile, ImageView> tblImg;
    @FXML
    private TableColumn<AbstractFile, String> tblName;

    @FXML
    private Button btnReturn;

    @FXML
    public void initialize() {
        fileExplorer = new FileExplorer(new Folder(rootDirectory), new AccessModifier()); // todo Add appropriate accessmodifier
        updateDisplayedFiles();
    }

    @FXML // Called when an tableview object is (left)clicked
    void clickElement(MouseEvent event) {
        AbstractFile chosenRow = (AbstractFile) tblFiles.getSelectionModel().getSelectedItem();

        if (event.getClickCount() == 2) {
            if (chosenRow instanceof Folder) { // todo does this work after cast to (AbstractFile) ?
                fileExplorer.navigateTo((Folder) chosenRow);
                updateDisplayedFiles();
            }
        }


    }

    @FXML
    void prevDir(ActionEvent event) {
        fileExplorer.navigateBack();
        updateDisplayedFiles();
    }

    //
    public void updateDisplayedFiles(){
        tblFiles.getItems().clear();

        ObservableList<AbstractFile> shownFiles = FXCollections.observableArrayList(fileExplorer.getShownFiles());

        //tblImg.setCellValueFactory(new PropertyValueFactory<>()) // todo add image to file display
        tblName.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().getName()));
        tblFiles.setItems(shownFiles);
    }


}
