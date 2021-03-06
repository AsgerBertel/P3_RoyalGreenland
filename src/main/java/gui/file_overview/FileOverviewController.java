package gui.file_overview;

import directory.*;
import directory.files.AbstractFile;
import directory.files.Document;
import directory.files.Folder;
import directory.plant.AccessModifier;
import directory.plant.Plant;
import directory.plant.PlantManager;
import gui.DMSApplication;
import gui.FileTreeUtil;
import gui.TabController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.input.*;
import javafx.scene.layout.FlowPane;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;

public class FileOverviewController implements TabController {

    //private Path rootDirectory = Paths.get(System.getProperty("user.dir") + "/Sample Files/Main Files");
    private FileExplorer fileExplorer;

    private ObservableList<Plant> plantList;

    private TreeItem<AbstractFile> rootItem;

    List<AbstractFile> filesToShow;

    @FXML
    private FlowPane flpFileView;

    @FXML
    private Label lblVisualPath;
    @FXML
    private TreeView<AbstractFile> fileTreeView;
    @FXML
    private ComboBox<Plant> drdPlant;

    private DMSApplication dmsApplication;

    @FXML // Called upon loading the fxml and constructing the gui
    public void initialize(URL location, ResourceBundle resources) {
        fileTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> openFileTreeElement(newValue));
        plantList = FXCollections.observableList(PlantManager.getInstance().getAllPlants());
        drdPlant.setItems(plantList);
        fileTreeView.setShowRoot(false);
        fileExplorer = new FileExplorer(FileManager.getInstance().getMainFiles());
        updateDisplayedFiles();
    }

    @Override
    public void initReference(DMSApplication dmsApplication) {
        this.dmsApplication = dmsApplication;
    }

    @Override
    public void update() {
        // Refresh file tree if the files have changed // todo removed after path changes - reimplement - Magnus
        reloadFileTree();

        plantList = FXCollections.observableList(PlantManager.getInstance().getAllPlants());
        drdPlant.setItems(plantList);
    }

    private void reloadFileTree() {
        rootItem = FileTreeUtil.generateTree(FileManager.getInstance().getMainFiles());
        fileTreeView.setRoot(rootItem);
    }

    @FXML
    void onPlantSelected(ActionEvent event) {
        Plant selectedPlant = drdPlant.getSelectionModel().getSelectedItem();
        // Create fileExplorer that matches the accessModifier of the selected plant
        fileExplorer = new FileExplorer(FileManager.getInstance().getMainFiles(), selectedPlant);

        if (selectedPlant != null) {
            AccessModifier accessModifier = selectedPlant.getAccessModifier();
            rootItem = FileTreeUtil.generateTree(FileManager.getInstance().getMainFiles(), accessModifier);

        } else {
            rootItem = FileTreeUtil.generateTree(FileManager.getInstance().getMainFiles(), new AccessModifier());
        }

        fileTreeView.setRoot(rootItem);

        updateDisplayedFiles();
    }

    // Updates the window to show the current files from the file explorer
    private void updateDisplayedFiles() {
        // Remove all currently shown files
        flpFileView.getChildren().clear();

        filesToShow = fileExplorer.getShownFiles();
        for (AbstractFile file : filesToShow) {
            FileButton fileButton = createFileButton(file);
            flpFileView.getChildren().add(fileButton);
        }
        lblVisualPath.setText(PathDisplayCorrection());
    }

    // Creates a FileButton from a File
    private FileButton createFileButton(AbstractFile file) {
        FileButton filebutton = new FileButton(file);

        filebutton.getStyleClass().add("FileButton");
        filebutton.setContentDisplay(ContentDisplay.TOP);
        filebutton.setOnMouseClicked(this::onFileButtonClick);

        // Add appropriate context menu
        if (file instanceof Folder)
            filebutton.setContextMenu(new ReadOnlyFolderContextMenu(this, filebutton));
        else
            filebutton.setContextMenu(new ReadOnlyDocumentContextMenu(this, filebutton));

        return filebutton;
    }

    private void onFileButtonClick(MouseEvent event) {
        FileButton clickedButton = (FileButton) event.getSource();
        if (event.getClickCount() == 2)
            open(clickedButton);
    }

    // Opens the folder that is double clicked and displays its content
    public void open(FileButton fileButton) { // todo Duplicate code. Deduplicate that shit - Magnus
        if (fileButton.getFile() instanceof Folder) {
            fileExplorer.navigateTo((Folder) fileButton.getFile());
            updateDisplayedFiles();
        } else {

            try {
                Desktop.getDesktop().open(Paths.get(Settings.getServerDocumentsPath() + fileButton.getFile().getOSPath()).toFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @FXML
    public void openPreviousFolder() {
        fileExplorer.navigateBack();
        updateDisplayedFiles();
    }

    public String PathDisplayCorrection() {
        int BracketCounter = 0;
        String NewString;
        if (getOperatingSystem() == "Windows") {
            NewString = fileExplorer.getCurrentPath().replaceAll(File.separator + File.separator, " > ");
            NewString = NewString.replaceAll("Sample files > Server >", "");
        } else {
            NewString = fileExplorer.getCurrentPath().replaceAll(File.separator, " > ");
            NewString = NewString.replaceAll("Sample files > Server >", "");
        }


        for (int i = 0; i < NewString.length(); i++) {
            if (NewString.charAt(i) == '>')
                BracketCounter++;
        }

        if (BracketCounter > 3) {// todo make better  - Magnus
            NewString = "../Skulle måske vise mere end bare den sidste folder"; //fileExplorer.getCurrentFolder().getName();
        } else {
            // todo This no longer works after paths are reworked.
            // NewString = NewString.substring(NewString.indexOf("Main Files"));
        }
        return NewString;
    }

    public String getOperatingSystem() {
        String OS = System.getProperty("os.name");
        if (OS.startsWith("Windows"))
            return "Windows";
        else
            return "MacOS";
    }

    public void openFileTreeElement(TreeItem<AbstractFile> newValue) {
        fileTreeView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                AbstractFile file = newValue.getValue();

                if (file instanceof Document) {
                    try {
                        Desktop.getDesktop().open(Paths.get(Settings.getServerDocumentsPath() + newValue.getValue().getOSPath()).toFile());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
