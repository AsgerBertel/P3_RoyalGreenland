package Main;

import Directory.AbstractDocFolder;
import Directory.DirectoryManager;
import Directory.Folder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.nio.file.Path;
import java.util.ArrayList;

public class Controller {
    DirectoryManager directoryManager = new DirectoryManager();

    @FXML
    private TextField txtFolderName;

    @FXML
    private TableView tblFiles;
    @FXML
    private TableColumn<AbstractDocFolder,ImageView> tblImg;

    @FXML
    private TableColumn<AbstractDocFolder, String > tblName;


    @FXML
    public void initialize() {
        directoryManager.DisplayFiles(tblName, tblImg, tblFiles);
    }

    @FXML
    void test() {

        directoryManager.CreateFolder("C:\\p3_folders/", txtFolderName.getText());
    }

   /* @FXML
    void ContextMenu(){
      /*  ContextMenu context = new ContextMenu();
        tblFiles.setContextMenu(context);
        context.getItems().addAll( directoryManager.ContextMenuItems(tblFiles));
    }*/

    @FXML
    void clickElement(MouseEvent event) {

        ContextMenu context = new ContextMenu();
        tblFiles.setContextMenu(context);
        context.getItems().addAll( directoryManager.ContextMenuItems(tblFiles));

        if(event.getClickCount() == 2){
            AbstractDocFolder chosenRow = (AbstractDocFolder) tblFiles.getSelectionModel().getSelectedItem();
            if(chosenRow.getFileType() == "folder"){
                directoryManager.openFolder(chosenRow.getPath());
                directoryManager.DisplayFiles(tblName,tblImg,tblFiles);
            }
        }
    }
}
