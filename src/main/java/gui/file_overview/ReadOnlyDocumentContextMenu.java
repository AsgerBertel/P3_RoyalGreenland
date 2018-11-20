package gui.file_overview;

import gui.DMSApplication;
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

    public void initFolderContextMenu() {


        MenuItem openDocument = new MenuItem(DMSApplication.getMessage("FileOverview.ContextMenu.Open"));
        openDocument.setOnAction(event -> openDocument());
        this.getItems().addAll(openDocument);
    }

    public void openDocument() {
        fileOverviewController.open(fileButton);
    }
}