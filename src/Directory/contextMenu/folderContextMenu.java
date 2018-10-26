/*
package Directory.contextMenu;

import Directory.AbstractFile;
import Directory.ContextMenuHandler;
import Directory.FileExplorer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import main.Controller;

import java.nio.file.Files;
import java.nio.file.Path;

public class folderContextMenu extends ContextMenu {
    Controller controller;
    FileExplorer fileExplorer;
    ContextMenuHandler contextMenuHandler;
    Path currentPath;

    folderContextMenu(Controller controller, FileExplorer fileExplorer) {
        this.controller = controller;
        this.fileExplorer = fileExplorer;
        setFolderContextMenu(null); //todo: Make method to get selected item
    }

    public void setFolderContextMenu(AbstractFile selectedItem) {
        if (selectedItem == null) {
            return;
        }
        if (Files.isDirectory(selectedItem.getPath())) {
            MenuItem openFolder = new MenuItem("Open");
            openFolder.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    currentPath = selectedItem.getPath();
                    fileExplorer.getShownFiles();
                    controller.updateDisplayedFiles();
                }
            });

            MenuItem renameFolder = new MenuItem("Rename");
            renameFolder.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    contextMenuHandler.renameFile(tblName, tblFiles); //Opdateres med magnus og mads' ændringer
                    controller.updateDisplayedFiles();

                }
            });
            MenuItem createFolder = new MenuItem("New Folder");
            createFolder.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    contextMenuHandler.createFolder(tblFiles, currentPath, selectedItem.getPath());
                    controller.updateDisplayedFiles();
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
            tblFiles.setContextMenu(this.folderContextMenu(selectedItem);
            this.getItems().addAll(openFolder, renameFolder, createFolder, uploadFile, deleteFolder);
        }

    }
}
*/