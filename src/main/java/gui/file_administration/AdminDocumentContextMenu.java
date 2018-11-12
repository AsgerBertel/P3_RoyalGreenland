package gui.file_administration;

import gui.file_overview.FileButton;
import gui.file_overview.FileOverviewController;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class AdminDocumentContextMenu extends ContextMenu{
    private FileOverviewController fileOverviewController;
    private FileButton fileButton;


    public AdminDocumentContextMenu(FileOverviewController fileOverviewController, FileButton fileButton) {
        this.fileOverviewController = fileOverviewController;
        this.fileButton = fileButton;
        initFolderContextMenu();
    }

    public void initFolderContextMenu() {


        MenuItem openDocument = new MenuItem("Open");
        openDocument.setOnAction(event -> openDocument());

        MenuItem renameDocument = new MenuItem("Rename");
        renameDocument.setOnAction(event -> {
        });

        MenuItem deleteDocument = new MenuItem("Delete");
        deleteDocument.setOnAction(event -> {
        });


        //  fileButton.setContextMenu(this.folderContextMenu(selectedItem));
        this.getItems().addAll(openDocument, renameDocument, deleteDocument);
    }

    public void openDocument() {
        fileOverviewController.open(fileButton);
    }


}


