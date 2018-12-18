package controller;

import app.ApplicationMode;
import model.managing.FileExplorer;
import model.managing.SettingsManager;
import gui.AlertBuilder;
import app.DMSApplication;
import gui.FileTreeUtil;
import gui.custom_node.FileButton;
import gui.custom_node.ReadOnlyDocumentContextMenu;
import gui.custom_node.ReadOnlyFolderContextMenu;
import log.LoggingErrorTools;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.*;
import javafx.scene.layout.FlowPane;
import io.json.AppFilesManager;
import model.AbstractFile;
import model.Document;
import model.Folder;
import model.Plant;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class FileOverviewController implements TabController {
    @FXML
    public Button btnReturn;
    @FXML
    public Label lastUpdateLabel;
    private FileExplorer fileExplorer;
    private List<AbstractFile> filesToShow;

    private FileButton selectedFileExplorer = null;

    private ArrayList<Plant> plantList;
    private Plant selectedPlant;
    private final Plant universalPlant = new Plant(-1, DMSApplication.getMessage("PlantAdmin.UniversalPlantName"), null);

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

        selectedFileExplorer = null;

        if(DMSApplication.getApplicationMode() == ApplicationMode.ADMIN){
            lastUpdateLabel.setVisible(false);
        }else{
            lastUpdateLabel.setVisible(true);
            lastUpdateLabel.setText(DMSApplication.getMessage("FileOverview.LastUpdated") + " " + AppFilesManager.getLastLocalUpdateTime());
        }

        reloadPlantDropDown();
        reloadFileTree();
        reloadFileExplorer();
    }


    private void reloadPlantDropDown(){
        EventHandler<ActionEvent> eventHandler = drdPlant.getOnAction();
        // Temporarily disable select listener in order to set default plant without triggering the onPlantSelected() method
        drdPlant.setOnAction(null);

        ArrayList<Plant> selectablePlants = new ArrayList<>();
        selectablePlants.add(universalPlant);
        selectablePlants.addAll(plantList);
        drdPlant.setItems(FXCollections.observableList(selectablePlants));

        if(selectedPlant == null){
            // Select the plant with access to all files as default
            drdPlant.getSelectionModel().select(universalPlant);
            selectedPlant = universalPlant;
        }else{
            // Select the plant that the user had selected before the reload
            drdPlant.getSelectionModel().select(selectedPlant);
        }
        // Add listener back in
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

    // Updates the window to show the current io from the file explorer
    private void updateDisplayedFiles() {
        // Remove all currently shown io
        flpFileView.getChildren().clear();

        filesToShow = fileExplorer.getShownFiles();
        for (AbstractFile file : filesToShow) {
            FileButton fileButton = createFileButton(file);
            Tooltip tooltip = new Tooltip(fileButton.getText());
            Tooltip.install(fileButton, tooltip);
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
        selectedFileExplorer = (FileButton) event.getSource();
        if (event.getClickCount() == 2)
            open(selectedFileExplorer);
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

    // Corrects the display so that it is not too long to fit in the textfield
    private String PathDisplayCorrection() {
        ArrayList<String> pathSteps = new ArrayList<>();
        String newString = "";

        String path;

        if (getOperatingSystem().equals("Windows")) {
            path = fileExplorer.getCurrentPath().replaceAll(File.separator + File.separator, "/");
        } else {
            path = fileExplorer.getCurrentPath().replaceAll(File.separator, " / ");
        }

        // Separate all folders and documents into substrings
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
        // If the path contains more than 3 folders/files only the last three of those will be display
        if (bracketCount > 3) {
            for (int i = pathSteps.size() - 3; i < pathSteps.size(); ++i) {
                if (i == pathSteps.size() - 3) {
                    newString = newString + "...";
                }
                newString = newString + " / " + pathSteps.get(i);
            }
        } else {
            // Otherwise show the entire path
            for (int i = 0; i < pathSteps.size(); ++i) {
                newString = newString + " / " + pathSteps.get(i);
            }
        }

        return newString;
    }

    private String getOperatingSystem() {
        String OS = System.getProperty("os.name");
        if (OS.startsWith("Windows"))
            return "Windows";
        else
            return "Unknown";
    }

    private void openFileTreeElement(TreeItem<AbstractFile> newValue) {
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

    public FileExplorer getFileExplorer() {
        return fileExplorer;
    }

    public FileButton getSelectedFileExplorer() {
        return selectedFileExplorer;
    }
}
