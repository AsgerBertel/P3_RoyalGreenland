
package gui.file_overview.context_menu;

import directory.files.AbstractFile;
import directory.files.Folder;
import gui.file_overview.FileButton;
import gui.file_overview.context_menu.ContextMenuHandler;
import directory.FileExplorer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import gui.file_overview.FileOverviewController;

import javax.naming.Context;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class FolderContextMenu extends ContextMenu {
    //FileOverviewController gui;
    FileExplorer fileExplorer;
    ContextMenuHandler contextMenuHandler = new ContextMenuHandler(fileExplorer);
    Folder folder;
    Path currentPath;

    public FolderContextMenu(FileButton fileButton, FileExplorer fileExplorer,Folder folder) {
      //  this.gui = gui;
        this.folder = folder;
        this.fileExplorer = fileExplorer;
        setFolderContextMenu(fileButton.getFile()); //todo: Make method to get selected item
    }

    public void setFolderContextMenu(AbstractFile selectedItem) {

        if (Files.isDirectory(selectedItem.getPath())) {
            MenuItem openFolder = new MenuItem("Open");
            openFolder.setOnAction(event -> {
                contextMenuHandler.openFolder(folder, fileExplorer);
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


}
