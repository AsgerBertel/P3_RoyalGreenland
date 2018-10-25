package Directory;

import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ContextMenuHandler {

    public void createFolder(TableView<AbstractFile> files, Path currentPath, Path selectedPath) {
        if (selectedPath == null) {
            Path filename = Paths.get(currentPath + "/" + "New Folder");
            if (!Files.exists(filename)) {

                try {
                    Files.createDirectory(filename);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("Directory created");
            } else {

                System.out.println("Directory already exists");
            }
        } else {
            Path filename = Paths.get(selectedPath + "/" + "New Folder");
            if (!Files.exists(filename)) {

                try {
                    Files.createDirectory(filename);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("Dirxectory created");
            } else {

                System.out.println("Directory already exists");
            }
        }
    }

    public void renameFile(TableColumn<AbstractFile, String> tblcName, TableView files) {
        TextField txtRename = new TextField();
        AbstractFile chosenRow = (AbstractFile) files.getSelectionModel().getSelectedItem();

        String ChosenRowName = chosenRow.getName();
        txtRename.setText(ChosenRowName);
        Path path = chosenRow.getPath().getParent();

        tblcName.setGraphic(txtRename);

        txtRename.setOnKeyPressed(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    File newFileName = new File(path + "/" + txtRename.getText());
                    File oldFIleName = new File(chosenRow.getPath().toString());
                    oldFIleName.renameTo(newFileName);
                }
            }
        });
    }

    /* //todo add context menu somewhere
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
        }*/ // todo reimplement context menu elsewhere

}
