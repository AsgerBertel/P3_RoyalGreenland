package gui.file_overview;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class ReadOnlyFolderContextMenu extends ContextMenu {
    private FileOverviewController fileOverviewController;
    private FileButton fileButton;


    public ReadOnlyFolderContextMenu(FileOverviewController fileOverviewController, FileButton fileButton) {
        this.fileOverviewController = fileOverviewController;
        this.fileButton = fileButton;

        initFolderContextMenu();
    }

    public void initFolderContextMenu() {


        MenuItem openFolder = new MenuItem("Open");
        openFolder.setOnAction(event -> openFolder());
        //  fileButton.setContextMenu(this.folderContextMenu(selectedItem));
        this.getItems().addAll(openFolder);
    }

    public void openFolder() {
        fileOverviewController.open(fileButton);
    }
}


