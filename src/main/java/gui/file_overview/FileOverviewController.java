package gui.file_overview;

import app.ApplicationMode;
import directory.*;
import directory.files.AbstractFile;
import directory.files.Document;
import directory.files.Folder;
import directory.plant.AccessModifier;
import directory.plant.Plant;
import directory.plant.PlantManager;
import gui.AlertBuilder;
import gui.DMSApplication;
import gui.FileTreeUtil;
import gui.TabController;
import gui.log.LoggingErrorTools;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.input.*;
import javafx.scene.layout.FlowPane;
import json.AppFilesManager;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class FileOverviewController implements TabController {

    private FileExplorer fileExplorer;

    private ArrayList<Plant> plantList;
    private Plant selectedPlant;
    private Plant universalPlant = new Plant(-1, "All plants", new AccessModifier()); // todo sprog "All plants"

    @FXML
    private TreeView<AbstractFile> fileTreeView;
    private TreeItem<AbstractFile> rootItem;
    private ArrayList<AbstractFile> filesList;

    List<AbstractFile> filesToShow;

    @FXML
    private FlowPane flpFileView;
    @FXML
    private Label lblVisualPath;
    @FXML
    private ComboBox<Plant> drdPlant;

    private DMSApplication dmsApplication;

    @FXML // Called upon loading the fxml and constructing the gui
    public void initialize(URL location, ResourceBundle resources) {
        fileTreeView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) openFileTreeElement(fileTreeView.getSelectionModel().getSelectedItem());
        });

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
        if (DMSApplication.getApplicationMode() == ApplicationMode.ADMIN) {
            plantList = AppFilesManager.loadPublishedFactoryList();
            filesList = AppFilesManager.loadPublishedFileList();
        } else {
            plantList = AppFilesManager.loadLocalFactoryList();
            filesList = AppFilesManager.loadLocalFileList();
        }

        reloadPlantDropDown();
        reloadFileTree();
        reloadFileExplorer();
    }

    private void reloadPlantDropDown(){
        ArrayList<Plant> selectablePlants = new ArrayList<>();
        selectablePlants.add(universalPlant);
        selectablePlants.addAll(PlantManager.getInstance().getAllPlants());
        drdPlant.setItems(FXCollections.observableList(selectablePlants));

        if(selectedPlant == null){
            drdPlant.getSelectionModel().select(universalPlant);
            selectedPlant = universalPlant;
        }else{
            drdPlant.getSelectionModel().select(selectedPlant);
        }
    }

    private void reloadFileExplorer() {
        fileExplorer = new FileExplorer(filesList, selectedPlant);
        updateDisplayedFiles();
    }

    private void reloadFileTree() {
        rootItem = FileTreeUtil.generateTree(filesList, selectedPlant.getAccessModifier());
        fileTreeView.setRoot(rootItem);
    }

    @FXML
    void onPlantSelected(ActionEvent event) {
        Plant selectedPlant = drdPlant.getSelectionModel().getSelectedItem();
        // Create fileExplorer that matches the accessModifier of the selected plant
        fileExplorer = new FileExplorer(FileManager.getInstance().getMainFiles(), selectedPlant);
        if (selectedPlant.equals(universalPlant)) {
            fileExplorer = new FileExplorer(FileManager.getInstance().getMainFiles());
            rootItem = FileTreeUtil.generateTree(FileManager.getInstance().getMainFiles());
            fileTreeView.setRoot(rootItem);
            updateDisplayedFiles();
            return;
        }

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
        lblVisualPath.setMaxWidth(550);
        // Make sure, that the text is cut from the left.
        lblVisualPath.setTextOverrun(OverrunStyle.LEADING_ELLIPSIS);
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
    public void open(FileButton fileButton) {
        if (fileButton.getFile() instanceof Folder) {
            fileExplorer.navigateTo((Folder) fileButton.getFile());
            updateDisplayedFiles();
        } else {
            try {
                Desktop.getDesktop().open(Settings.getServerDocumentsPath().resolve(fileButton.getFile().getOSPath()).toFile());
            } catch (IOException e) {
                LoggingErrorTools.log(e);
                AlertBuilder.IOExceptionPopUp();
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
        ArrayList<String> pathSteps = new ArrayList<>();
        String newString = "";

        String path = fileExplorer.getCurrentPath();

        if (getOperatingSystem() == "Windows") {
            path = fileExplorer.getCurrentPath().replaceAll(File.separator + File.separator, "/");
        } else {
            path = fileExplorer.getCurrentPath().replaceAll(File.separator, " / ");
        }

        String tempString = "";
        for (int i = 0; i < path.length(); ++i) {
            if (path.charAt(i) != '/') {
                tempString = tempString + path.charAt(i);
            } else {
                pathSteps.add(tempString);
                tempString = "";
            }
            if (i == path.length() - 1 && pathSteps.size() == 0) {
                pathSteps.add(tempString);
            } else if (i == path.length() - 1) {
                pathSteps.add(tempString);
            }
        }

        int bracketCount = pathSteps.size();

        String tmpString = "";

        if (bracketCount > 3) {
            for (int i = pathSteps.size() - 3; i < pathSteps.size(); ++i) {
                if (i == pathSteps.size() - 3) {
                    newString = newString + "...";
                }
                newString = newString + " / " + pathSteps.get(i);
            }
        } else {
            for (int i = 0; i < pathSteps.size(); ++i) {
                newString = newString + " / " + pathSteps.get(i);
            }
        }

        return newString;
    }

    public String getOperatingSystem() {
        String OS = System.getProperty("os.name");
        if (OS.startsWith("Windows"))
            return "Windows";
        else
            return "MacOS";
    }

    public void openFileTreeElement(TreeItem<AbstractFile> newValue) {

        AbstractFile file = newValue.getValue();
        if (file instanceof Document) {
            Document doc = (Document) file;
            try {
                Desktop.getDesktop().open(Settings.getServerDocumentsPath().resolve(doc.getOSPath()).toFile());
            } catch (IOException e) {
                System.out.println("Could not open file");
                e.printStackTrace();
            }
        }
    }

    //todo dUpLiCaTe CoDe
}
