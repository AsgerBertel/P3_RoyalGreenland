package Main;

import Directory.abstractDocFolder;
import Directory.ContextMenuHandler;
import Directory.DirectoryManager;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.nio.file.Path;
import java.nio.file.Paths;

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
    private TableColumn<abstractDocFolder, ImageView> tblImg;

    @FXML
    private TableColumn<abstractDocFolder, String> tblName;

    @FXML
    private Button btnReturn;

    @FXML
    public void initialize() {
        directoryManager.displayFiles(tblName, tblImg, tblFiles);
    }

    @FXML
    void clickElement(MouseEvent event) {
        fileContextMenu.getItems().clear();
        folderContextMenu.getItems().clear();

        abstractDocFolder chosenRow = (abstractDocFolder) tblFiles.getSelectionModel().getSelectedItem();
        if(chosenRow == null)
        {
            return;
        }

        String fileType = chosenRow.getFileType();

        TextField txttest = new TextField();

        if (fileType == "folder") {


            MenuItem openFolder = new MenuItem("Open");
            openFolder.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    currentPath = chosenRow.getPath();
                    directoryManager.openFolder(chosenRow.getPath());
                    directoryManager.displayFiles(tblName, tblImg, tblFiles);
                }
            });

            MenuItem renameFolder = new MenuItem("Rename");
            renameFolder.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    contextMenuHandler.renameFile(tblName, tblFiles);
                    directoryManager.displayFiles(tblName, tblImg, tblFiles);

                }
            });

            MenuItem createFolder = new MenuItem("New Folder");
            createFolder.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    contextMenuHandler.createFolder(tblFiles, currentPath, chosenRow.getPath());
                    directoryManager.displayFiles(tblName, tblImg, tblFiles);
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
                directoryManager.displayFiles(tblName, tblImg, tblFiles);
            }
        }
    }

    @FXML
    void prevDir(ActionEvent event) {
        try {
            if (!(currentPath.equals(rootDirectory))) {
                directoryManager.openPrevFolder(currentPath);
                directoryManager.displayFiles(tblName, tblImg, tblFiles);
                currentPath = currentPath.getParent();
            } else {
                System.out.println("you are in root directory");
            }
        } catch (Exception e) {
            System.out.println("shit");
        }
    }


}
