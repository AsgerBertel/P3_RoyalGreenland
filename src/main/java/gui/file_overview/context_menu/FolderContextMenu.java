
package gui.file_overview.context_menu;

import directory.files.AbstractFile;
import directory.files.Folder;
import gui.file_overview.FileButton;
import directory.FileExplorer;

import gui.file_overview.FileOverviewController;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class FolderContextMenu extends ContextMenu {
    FileOverviewController fileOverviewController;
    FileExplorer fileExplorer;
    Folder folder;
    Path currentPath;

    public FolderContextMenu(FileOverviewController fileOverviewController) {
        //  this.gui = gui;
        this.fileOverviewController = fileOverviewController;
        //this.fileExplorer = fileExplorer;
        // setFolderContextMenu(); //todo: Make method to get selected item
    }

    public void setFolderContextMenu(AbstractFile selectedItem) {

        if (Files.isDirectory(selectedItem.getPath())) {
            MenuItem openFolder = new MenuItem("Open");
            openFolder.setOnAction(event -> {
                openFolder();

            });

            MenuItem renameFolder = new MenuItem("Rename");
            renameFolder.setOnAction(event -> {

            });

            MenuItem createFolder = new MenuItem("New Folder");
            createFolder.setOnAction(event -> {

            });
            MenuItem deleteFolder = new MenuItem("Delete");
            deleteFolder.setOnAction(event -> {

                    }
            );
            MenuItem uploadFile = new MenuItem("Upload");
            deleteFolder.setOnAction(event -> {

                    }
            );

            //  fileButton.setContextMenu(this.folderContextMenu(selectedItem));
            this.getItems().addAll(openFolder, renameFolder, createFolder, uploadFile, deleteFolder);
        }

    }
    public void openFolder(){
        fileExplorer.navigateTo(folder);
    }


}
