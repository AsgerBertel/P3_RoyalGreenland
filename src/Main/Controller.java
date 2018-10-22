package Main;

import Directory.AbstractDocFolder;
import Directory.ContextMenuHandler;
import Directory.DirectoryManager;
import Directory.Folder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Controller {
    ContextMenu folderContextMenu = new ContextMenu();
    ContextMenu fileContextMenu = new ContextMenu();
    ContextMenuHandler contextMenuHandler = new ContextMenuHandler();
    DirectoryManager directoryManager = new DirectoryManager();
    private Path rootDirectory = Paths.get("C:\\p3_folders/");
    private Path currentPath = rootDirectory;
    @FXML
    private TextField txtFolderName;

    @FXML
    private TableView tblFiles;
    @FXML
    private TableColumn<AbstractDocFolder, ImageView> tblImg;

    @FXML
    private TableColumn<AbstractDocFolder, String> tblName;

    @FXML
    private Button btnReturn;

    @FXML
    public void initialize() {
        directoryManager.DisplayFiles(tblName, tblImg, tblFiles);
    }

    @FXML
    void clickElement(MouseEvent event) {
        fileContextMenu.getItems().clear();
        folderContextMenu.getItems().clear();
        AbstractDocFolder chosenRow = (AbstractDocFolder) tblFiles.getSelectionModel().getSelectedItem();

        String fileType = chosenRow.getFileType();


        if (fileType == "folder") {


            MenuItem openFolder = new MenuItem("Open");
            openFolder.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    currentPath = chosenRow.getPath();
                    directoryManager.openFolder(chosenRow.getPath());
                    directoryManager.DisplayFiles(tblName, tblImg, tblFiles);
                }
            });
            MenuItem renameFolder = new MenuItem("Rename");
            renameFolder.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    System.out.println("Rename");
                }
            });

            MenuItem createFolder = new MenuItem("New Folder");
            createFolder.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    contextMenuHandler.CreateFolder(tblFiles, currentPath,chosenRow.getPath());
                    directoryManager.DisplayFiles(tblName, tblImg, tblFiles);
                }
            });
            MenuItem uploadFile = new MenuItem("Upload File");
            uploadFile.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    System.out.println("Upload File");
                }
            });
            MenuItem deleteFolder = new MenuItem("Delete");
            deleteFolder.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    System.out.println("Delete");
                }
            });
            tblFiles.setContextMenu(folderContextMenu);
            folderContextMenu.getItems().addAll(openFolder, renameFolder, createFolder, uploadFile, deleteFolder);
        } else {
            MenuItem openFile = new MenuItem("Open");
            MenuItem renameFile = new MenuItem("Rename");
            MenuItem deleteFile = new MenuItem("Delete");

            tblFiles.setContextMenu(fileContextMenu);
            fileContextMenu.getItems().addAll(openFile, renameFile, deleteFile);
        }


        if (event.getClickCount() == 2) {
            if (chosenRow.getFileType() == "folder") {
                currentPath = chosenRow.getPath();
                directoryManager.openFolder(chosenRow.getPath());
                directoryManager.DisplayFiles(tblName, tblImg, tblFiles);
            }
        }
    }

    @FXML
    void prevDir(ActionEvent event) {
        try {
            if (!(currentPath.equals(rootDirectory))) {
                directoryManager.openPrevFolder(currentPath);
                directoryManager.DisplayFiles(tblName, tblImg, tblFiles);
                currentPath = currentPath.getParent();
            } else {
                System.out.println("you are in root directory");
            }
        } catch (Exception e) {
            System.out.println("shit");
        }
    }


}
