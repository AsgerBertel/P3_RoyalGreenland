package Main;

import Directory.AbstractDocFolder;
import Directory.DirectoryManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.nio.file.Path;

public class Controller {
    DirectoryManager directoryManager = new DirectoryManager();
    @FXML
    private Button btnTest;

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
/*
        // Column 0 (Icon)
        TableColumn<AbstractDocFolder, ImageView> iconColumn = new TableColumn<>("Icon");
        iconColumn.setCellValueFactory(new PropertyValueFactory<>("image"));

        // Column 1 (Name)
        TableColumn<AbstractDocFolder, ImageView> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        this.tblFiles.getColumns().addAll(iconColumn, nameColumn);*/

    }

    @FXML
    void test() {


        directoryManager.CreateFolder("C:\\p3_folders/", txtFolderName.getText());
    }
    @FXML
    void Display( ) {


        directoryManager.DisplayFiles(tblName, tblImg, tblFiles);
    }

}
