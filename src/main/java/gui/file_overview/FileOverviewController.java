package gui.file_overview;

import app.ApplicationMode;
import directory.*;
import directory.files.AbstractFile;
import directory.files.Document;
import directory.files.Folder;
import directory.plant.Plant;
import gui.AlertBuilder;
import gui.DMSApplication;
import gui.FileTreeUtil;
import gui.TabController;
import gui.log.LoggingErrorTools;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
    private List<AbstractFile> filesToShow;

    private ArrayList<Plant> plantList;
    private Plant selectedPlant;
    private Plant universalPlant = new Plant(-1, DMSApplication.getMessage("PlantAdmin.UniverselPlantName"), null);

    @FXML
    private TreeView<AbstractFile> fileTreeView;
    private TreeItem<AbstractFile> rootItem;
    private ArrayList<AbstractFile> filesList;

    @FXML
    private FlowPane flpFileView;
    @FXML
    private Label lblVisualPath;
    @FXML
    private ComboBox<Plant> drdPlant;

    @FXML // Called upon loading the fxml and constructing the gui
    public void initialize(URL location, ResourceBundle resources) {
        fileTreeView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) openFileTreeElement(fileTreeView.getSelectionModel().getSelectedItem());
        });

        fileTreeView.setShowRoot(false);
    }

    @Override
    public void initReference(DMSApplication dmsApplication) {}

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
        EventHandler<ActionEvent> eventHandler = drdPlant.getOnAction();
        drdPlant.setOnAction(null);
        ArrayList<Plant> selectablePlants = new ArrayList<>();
        selectablePlants.add(universalPlant);
        selectablePlants.addAll(plantList);
        drdPlant.setItems(FXCollections.observableList(selectablePlants));


        if(selectedPlant == null){
            drdPlant.getSelectionModel().select(universalPlant);
            selectedPlant = universalPlant;
        }else{
            drdPlant.getSelectionModel().select(selectedPlant);
        }
        drdPlant.setOnAction(eventHandler);
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
        selectedPlant = drdPlant.getSelectionModel().getSelectedItem();
        update();
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

    // Called when a fileButton is double clicked
    public void open(FileButton fileButton) {
        if (fileButton.getFile() instanceof Folder) {
            fileExplorer.navigateTo((Folder) fileButton.getFile());
            updateDisplayedFiles();
        } else {
            try {
                Desktop.getDesktop().open(SettingsManager.getServerDocumentsPath().resolve(fileButton.getFile().getOSPath()).toFile());
            } catch (IOException e) {
                LoggingErrorTools.log(e);
                AlertBuilder.IOExceptionPopUp();
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void openPreviousFolder() {
        if(fileExplorer.navigateBack()){
            updateDisplayedFiles();
        }
    }

    public String PathDisplayCorrection() {
        ArrayList<String> pathSteps = new ArrayList<>();
        String newString = "";

        String path;

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
                Desktop.getDesktop().open(SettingsManager.getServerDocumentsPath().resolve(doc.getOSPath()).toFile());
            } catch (IOException e) {
                System.out.println("Could not open file");
                e.printStackTrace();
            }
        }
    }
}
