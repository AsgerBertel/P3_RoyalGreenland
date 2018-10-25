package Directory;

import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import javax.naming.InvalidNameException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ContextMenuHandler {

    public void createFolder(TableView<AbstractFile> files, Path currentPath, Path selectedPath) {
        // todo Add new folder through filemanager
    }


    public void renameFile(TableColumn<AbstractFile, String> tblcName, TableView files) {
        TextField txtRename = new TextField();
        AbstractFile selectedFile = (AbstractFile) files.getSelectionModel().getSelectedItem();

        String ChosenRowName = selectedFile.getName();
        txtRename.setText(ChosenRowName);
        Path path = selectedFile.getPath().getParent();

        tblcName.setGraphic(txtRename);

        txtRename.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                try {
                    selectedFile.renameFile(txtRename.getText());
                } catch (InvalidNameException e) {
                    // todo Show error popup
                }
            }else if(event.getCode().equals(KeyCode.ESCAPE)){
                // todo cancel renaming - hide txtField
            }
        });
    }

    /* //todo split into multiple functions - Maybe have creation of context menu seperate from the corresponding event handling? Create context menu
       //todo maybe create seperate context menu classes for folder, document and empty?
        fileContextMenu.getItems().clear();
        folderContextMenu.getItems().clear();


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
        }*/

}
