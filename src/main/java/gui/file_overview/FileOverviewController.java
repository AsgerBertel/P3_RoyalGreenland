package gui.file_overview;

import directory.*;
import directory.files.AbstractFile;
import directory.files.Document;
import directory.files.Folder;
import gui.file_overview.context_menu.FolderContextMenu;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileOverviewController {

    DirectoryManager directoryManager = new DirectoryManager();

    private Path rootDirectory = Paths.get("C:\\");
    private FileExplorer fileExplorer;
    private Document document;
    private Folder folder;
    @FXML
    private FlowPane flpFileExplorer;
    @FXML
    private Button btnReturn;

    @FXML // Called upon loading the fxml and constructing the gui
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
    public void updateDisplayedFiles() {
        flpFileExplorer.getChildren().clear();
        List<AbstractFile> filesToShow = fileExplorer.getShownFiles();
        for (int i = 0; i < filesToShow.size(); i++) {
            FileButton filebutton = new FileButton(filesToShow.get(i));
            filebutton.getStyleClass().add("FileButton");
            filebutton.setContentDisplay(ContentDisplay.TOP);
            filebutton.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    openFolder(filebutton);
                }
                else {
                    folder = new Folder(filebutton.getFile().getPath());
                    FolderContextMenu folderContextMenu = new FolderContextMenu(this);
                    folderContextMenu.setFolderContextMenu(folder);
                    filebutton.setContextMenu(folderContextMenu);
                }
            });
            flpFileExplorer.getChildren().add(filebutton);
        }
        // Opens the folder that is double clicked and displays its content

    }


    public void openFolder(FileButton fileButton) {

        if (Files.isDirectory(fileButton.getFile().getPath())) {
            folder = new Folder(fileButton.getFile().getPath());
            fileExplorer.navigateTo(folder);
            updateDisplayedFiles();
        } else {
            fileButton.setFile(document);
        }
    }
}
