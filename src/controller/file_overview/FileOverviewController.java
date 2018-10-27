package controller.file_overview;

import controller.file_overview.context_menu.ContextMenuHandler;
import directory.*;
import directory.files.AbstractFile;
import directory.files.Folder;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileOverviewController {

    DirectoryManager directoryManager = new DirectoryManager();

    private Path rootDirectory = Paths.get("C:\\");
    private FileExplorer fileExplorer;


    @FXML
    private Button btnReturn;

    @FXML // Called upon loading the fxml and constructing the controller
    public void initialize() {
        fileExplorer = new FileExplorer(new Folder(rootDirectory), new AccessModifier()); // todo Add appropriate accessModifier
        updateDisplayedFiles();
    }

    @FXML
    void prevDir(ActionEvent event) {
        fileExplorer.navigateBack();
        updateDisplayedFiles();
    }

    // Updates the window to show the current files from the file explorer
    public void updateDisplayedFiles(){
        List<AbstractFile> filesToShow = fileExplorer.getShownFiles();
        // todo
    }


}
