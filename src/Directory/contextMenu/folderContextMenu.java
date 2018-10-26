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

import javax.naming.Context;
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
                    openFolder(selectedItem);
                }
            });

            MenuItem renameFolder = new MenuItem("Rename");
            renameFolder.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    renameFolder(selectedItem);
                }
            });

            MenuItem createFolder = new MenuItem("New Folder");
            createFolder.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    createFolder(selectedItem);
                }
            });
            MenuItem deleteFolder = new MenuItem("Delete");
            deleteFolder.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    deleteFolder(AbstractFile selectedItem);
                }
            }
        });

        tblFiles.setContextMenu(this.folderContextMenu(selectedItem);
        this.getItems().addAll(openFolder, renameFolder, createFolder, uploadFile, deleteFolder);
    }

}

    private void openFolder(AbstractFile selectedItem) {
        currentPath = selectedItem.getPath();
        fileExplorer.getShownFiles();
        controller.updateDisplayedFiles();
    }

    private void renameFolder(AbstractFile selectedItem) {
        contextMenuHandler.renameFile(tblName, tblFiles); //Opdateres med magnus og mads' ændringer
        controller.updateDisplayedFiles();
    }

    private void createFolder(AbstractFile selectedItem) {
        contextMenuHandler.createFolder(tblFiles, currentPath, selectedItem.getPath());
        controller.updateDisplayedFiles();
    }

    private void deleteFolder(AbstractFile selectedItem) {
        System.out.println("delete File");
    }
}
*/