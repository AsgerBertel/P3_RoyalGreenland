package gui.file_overview.context_menu;

import directory.files.AbstractFile;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import javax.naming.InvalidNameException;
import java.nio.file.Path;

public class ContextMenuHandler {

    public void createFolder(TableView<AbstractFile> files, Path currentPath, Path selectedPath) {
        // todo Add new folder through filemanager
    }


    public void renameFile(TableColumn<AbstractFile, String> tblcName, TableView files) {
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
    }
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
