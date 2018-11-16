
package gui.file_administration;
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
        MenuItem openFile = new MenuItem("Open");
        openFile.setOnAction(event -> openFile());

        MenuItem renameFile = new MenuItem("Rename");
        renameFile.setOnAction(event ->  renameFile());

        MenuItem createFile = new MenuItem("New Folder");
        createFile.setOnAction(event -> createFolder());

        MenuItem deleteFile = new MenuItem("Delete");
        deleteFile.setOnAction(event -> deleteFile());

        MenuItem uploadFile = new MenuItem("Upload");
        deleteFile.setOnAction(event -> uploadFile());

        //  fileButton.setContextMenu(this.folderContextMenu(selectedItem));
        this.getItems().addAll(openFile, renameFile, createFile, uploadFile, deleteFile);
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

    }

}
