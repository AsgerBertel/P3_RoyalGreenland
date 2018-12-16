
package gui.custom_node;
import app.DMSApplication;
import controller.FileAdminController;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class AdminFilesContextMenu extends ContextMenu {
    private FileAdminController fileAdminController;

    public AdminFilesContextMenu(FileAdminController fileAdminController) {
        this.fileAdminController = fileAdminController;
        initFolderContextMenu();
    }

    private void initFolderContextMenu() {
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
        this.getItems().addAll(openFile, renameFile, deleteFile, uploadFile,createFolder);
    }

    private void renameFile() {
        fileAdminController.renameFile();
    }

    private void openFile(){
        fileAdminController.openFile();
    }

    private void createFolder(){
        fileAdminController.createFolder();
    }

    private void deleteFile(){
        fileAdminController.deleteFile();
    }

    private void uploadFile(){
        fileAdminController.uploadDocument();
    }

}
