package gui.file_overview;

import directory.*;
import directory.plant.AccessModifier;
import directory.files.AbstractFile;
import directory.files.Document;
import directory.files.Folder;
import directory.plant.Plant;
import gui.FileTreeGenerator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FileOverviewController {

    private Path rootDirectory = Paths.get(System.getProperty("user.dir") + "/Sample Files/Main Files");
    private FileExplorer fileExplorer;

    @FXML
    private FlowPane flpFileView;
    @FXML
    private Button btnReturn;
    @FXML
    private Label lblVisualPath;
    @FXML
    private VBox vboxTop;
    @FXML
    private Pane PaneLine;
    // todo temporary
    private FileManager fileManager;
    @FXML
    private TreeView<AbstractFile> fileTreeView;

    @FXML // Called upon loading the fxml and constructing the gui
    public void initialize() {
        System.out.println(System.getProperty("user.dir"));
        fileExplorer = new FileExplorer(new Folder(rootDirectory), new Plant(1564, "utøya", new AccessModifier())); // todo Add appropriate accessModifier
        updateDisplayedFiles();

        fileManager = new FileManager();
        TreeItem<AbstractFile> rootItem = FileTreeGenerator.generateTree(new Folder(rootDirectory));
        fileTreeView.setRoot(rootItem); // todo Add appropriate accessModifier
    }


    @FXML
    void prevDir(ActionEvent event) {
        fileExplorer.navigateBack();
        updateDisplayedFiles();
    }

    // Updates the window to show the current files from the file explorer
    private void updateDisplayedFiles() {
        // Remove all currently shown files
        flpFileView.getChildren().clear();

        List<AbstractFile> filesToShow = fileExplorer.getShownFiles();
        for (AbstractFile file : filesToShow) {
            FileButton fileButton = createFileButton(file);
            flpFileView.getChildren().add(fileButton);
        }
        lblVisualPath.setText(PathDisplayCorrection());


    }

    // Creates a FileButton from a File and adds
    private FileButton createFileButton(AbstractFile file) {
        FileButton filebutton = new FileButton(file);

        filebutton.getStyleClass().add("FileButton");
        filebutton.setContentDisplay(ContentDisplay.TOP);

        filebutton.setOnMouseClicked(event -> onFileButtonClick(event));
        // Add appropriate context menu
        if (file instanceof Folder) {
            filebutton.setContextMenu(new FolderContextMenu(this, filebutton));
        } else {
            // todo set document context menu
        }

        return filebutton;
    }


    private void onFileButtonClick(MouseEvent event) {
        FileButton clickedButton = (FileButton) event.getSource();
        AbstractFile file = clickedButton.getFile();
        if (event.getClickCount() == 2)
            open(clickedButton);
        else if (file instanceof Document) {
            // todo temp
        }
    }

    // Opens the folder that is double clicked and displays its content
    public void open(FileButton fileButton) {

        if (fileButton.getFile() instanceof Folder) {
            fileExplorer.navigateTo((Folder) fileButton.getFile());
            updateDisplayedFiles();
        } else {

             try {
                ((Document) fileButton.getFile()).openDocument();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void openPreviusFolder() {
        fileExplorer.navigateBack();
        updateDisplayedFiles();
    }

    public String PathDisplayCorrection() {
        String NewString = fileExplorer.getCurrentFolder().getPath().toString().replaceAll("\\\\", " > ");

         NewString= NewString.substring(NewString.indexOf("Main Files"));
        return NewString;
    }

}
