
package gui.file_administration;
import gui.DMSApplication;
import gui.file_overview.FileButton;
import gui.file_overview.FileOverviewController;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class AdminFilesContextMenu extends ContextMenu {
    private FileAdminController fileAdminController;

    public AdminFilesContextMenu(FileAdminController fileAdminController) {
        this.fileAdminController = fileAdminController;
        initFolderContextMenu();
    }

    public void initFolderContextMenu() {
        MenuItem openFile = new MenuItem(DMSApplication.getMessage("AdminFiles.ContextMenu.Open"));
        openFile.setOnAction(event -> openFile());

        MenuItem renameFile = new MenuItem(DMSApplication.getMessage("AdminFiles.ContextMenu.Rename"));
        renameFile.setOnAction(event ->  renameFile());

        MenuItem createFolder = new MenuItem(DMSApplication.getMessage("AdminFiles.ContextMenu.NewFolder"));
        createFolder.setOnAction(event -> createFolder());

        MenuItem deleteFile = new MenuItem(DMSApplication.getMessage("AdminFiles.ContextMenu.Delete"));
        deleteFile.setOnAction(event -> deleteFile());

        MenuItem uploadFile = new MenuItem(DMSApplication.getMessage("AdminFiles.ContextMenu.Upload"));
        uploadFile.setOnAction(event -> uploadFile());

        //  fileButton.setContextMenu(this.folderContextMenu(selectedItem));
        this.getItems().addAll(openFile, renameFile, createFolder, deleteFile, uploadFile);
    }

    public void renameFile() {
        fileAdminController.renameFile();
    }

    public void openFile(){
        fileAdminController.openFile();
    }

    public void createFolder(){
        fileAdminController.createFolder();
    }

    public void deleteFile(){
        fileAdminController.deleteFile();
    }

    public void uploadFile(){
        fileAdminController.uploadDocument();
    }

}
