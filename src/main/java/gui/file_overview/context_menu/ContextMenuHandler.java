package gui.file_overview.context_menu;
import directory.*;
import directory.files.AbstractFile;
import directory.files.Document;
import directory.files.Folder;
import gui.file_overview.context_menu.FolderContextMenu;
import javafx.event.ActionEvent;
import directory.files.AbstractFile;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import javax.naming.InvalidNameException;
import java.nio.file.Path;

public class ContextMenuHandler {
    private FileExplorer fileExplorer;

    public ContextMenuHandler(FileExplorer fileExplorer) {
        this.fileExplorer = fileExplorer;
    }

    public void openFolder(Folder newFolder, FileExplorer fileExplorer){
        fileExplorer.navigateTo(newFolder);
    }










    public void createFolder() {
        // todo Add new folder through filemanager
    }


   /* public void renameFile(TableColumn<AbstractFile, String> tblcName, TableView files) {
        TextField txtRename = new TextField();
        AbstractFile selectedFile = (AbstractFile) files.getSelectionModel().getSelectedItem();

        String ChosenRowName = selectedFile.getName();
        txtRename.setText(ChosenRowName);
        Path path = selectedFile.getPath().getParent();

        tblcName.setGraphic(txtRename);

        txtRename.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                try {
                    selectedFile.renameFile(txtRename.getText());
                } catch (InvalidNameException e) {
                    // todo Show error popup
                }
            }else if(event.getCode().equals(KeyCode.ESCAPE)){
                // todo cancel renaming - hide txtField
            }
        });
    }*/
    /* //todo split into multiple functions - Maybe have creation of context menu seperate from the corresponding event handling? Create context menu
       //todo maybe create seperate context menu classes for folder, document and empty?
        fileContextMenu.getItems().clear();

        } else {
            MenuItem openFile = new MenuItem("Open");
            MenuItem renameFile = new MenuItem("Rename");
            MenuItem deleteFile = new MenuItem("Delete");

            tblFiles.setContextMenu(fileContextMenu);
            fileContextMenu.getItems().addAll(openFile, renameFile, deleteFile);
        }*/

}
