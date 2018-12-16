package gui.custom_node;

import app.DMSApplication;
import controller.FileOverviewController;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class ReadOnlyDocumentContextMenu extends ContextMenu {
    private FileOverviewController fileOverviewController;
    private FileButton fileButton;

    public ReadOnlyDocumentContextMenu(FileOverviewController fileOverviewController, FileButton fileButton) {
        this.fileOverviewController = fileOverviewController;
        this.fileButton = fileButton;

        initFolderContextMenu();
    }

    private void initFolderContextMenu() {


        MenuItem openDocument = new MenuItem(DMSApplication.getMessage("FileOverview.ContextMenu.Open"));
        openDocument.setOnAction(event -> openDocument());
        this.getItems().addAll(openDocument);
    }

    private void openDocument() {
        fileOverviewController.open(fileButton);
    }
}