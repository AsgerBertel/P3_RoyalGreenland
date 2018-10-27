/*
package controller.file_overview.context_menu;

import directory.files.AbstractFile;
import controller.file_overview.context_menu.ContextMenuHandler;
import directory.FileExplorer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.context_menu;
import javafx.scene.control.MenuItem;
import controller.file_overview.FileOverviewController;

import java.nio.file.Files;
import java.nio.file.Path;

public class folderContextMenu extends context_menu {
    FileOverviewController controller;
    FileExplorer fileExplorer;
    ContextMenuHandler contextMenuHandler;
    Path currentPath;

    folderContextMenu(FileOverviewController controller, FileExplorer fileExplorer) {
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