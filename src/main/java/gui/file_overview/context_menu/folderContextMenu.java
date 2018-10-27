/*
package gui.file_overview.context_menu;

import directory.files.AbstractFile;
import gui.file_overview.context_menu.ContextMenuHandler;
import directory.FileExplorer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.context_menu;
import javafx.scene.control.MenuItem;
import gui.file_overview.FileOverviewController;

import javax.naming.Context;
import java.nio.file.Files;
import java.nio.file.Path;

public class folderContextMenu extends context_menu {
    FileOverviewController gui;
    FileExplorer fileExplorer;
    ContextMenuHandler contextMenuHandler;
    Path currentPath;

    folderContextMenu(FileOverviewController gui, FileExplorer fileExplorer) {
        this.gui = gui;
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
        gui.updateDisplayedFiles();
    }

    private void renameFolder(AbstractFile selectedItem) {
        contextMenuHandler.renameFile(tblName, tblFiles); //Opdateres med magnus og mads' ændringer
        gui.updateDisplayedFiles();
    }

    private void createFolder(AbstractFile selectedItem) {
        contextMenuHandler.createFolder(tblFiles, currentPath, selectedItem.getPath());
        gui.updateDisplayedFiles();
    }

    private void deleteFolder(AbstractFile selectedItem) {
        System.out.println("delete File");
    }
}
*/