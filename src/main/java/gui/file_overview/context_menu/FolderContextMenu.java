
package gui.file_overview.context_menu;

import directory.files.AbstractFile;
import directory.files.Folder;
import gui.file_overview.FileButton;
import directory.FileExplorer;

import gui.file_overview.FileOverviewController;
import javafx.event.Event;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class FolderContextMenu extends ContextMenu {
    private FileOverviewController fileOverviewController;
    private FileButton fileButton;


    public FolderContextMenu(FileOverviewController fileOverviewController, FileButton fileButton) {
        this.fileOverviewController = fileOverviewController;
        this.fileButton = fileButton;
        initFolderContextMenu(); // todo throw exception if document and not folder
    }

    public void initFolderContextMenu() {


        MenuItem openFolder = new MenuItem("Open");
        openFolder.setOnAction(event -> openFolder());

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

    public void openFolder() {
        fileOverviewController.open(fileButton);
    }


}
