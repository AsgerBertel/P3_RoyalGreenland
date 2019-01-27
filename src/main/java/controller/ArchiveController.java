package controller;

import app.DMSApplication;
import model.managing.FileExplorer;
import model.managing.FileManager;
import model.managing.SettingsManager;
import model.AbstractFile;
import model.Document;
import model.Folder;
import gui.*;
import gui.custom_node.FileButton;
import log.LoggingErrorTools;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;


import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ArchiveController implements TabController {

    private FileExplorer fileExplorer;
    private DMSApplication dmsApplication;
    private TreeItem<AbstractFile> rootItem;
    private List<AbstractFile> filesToShow;

    @FXML
    private VBox leftSideVbox;
    @FXML
    private TreeView<AbstractFile> fileTreeView;
    @FXML
    private VBox vboxTop;
    @FXML
    private Button btnReturn;
    @FXML
    private Button btnRestore;
    @FXML
    private Label lblVisualPath;
    @FXML
    private ScrollPane scpFileView;
    @FXML
    private FlowPane flpFileView;
    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        fileTreeView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2)
                fileTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> openFileTreeElement(newValue));
        });
        fileTreeView.setRoot(new TreeItem<>());

        update();
        addToolTip();
    }

    @Override
    public void initReference(DMSApplication dmsApplication) {
        this.dmsApplication = dmsApplication;
    }

    @Override
    public void update() {
        TreeState oldTreeState = new TreeState(fileTreeView);
        rootItem = FileTreeUtil.generateTree(FileManager.getInstance().getArchiveFiles());
        fileTreeView.setRoot(rootItem);
        oldTreeState.replicateTreeExpansion(rootItem);
        fileTreeView.setShowRoot(false);
    }

    @FXML
    private void updateDisplayedFiles() {
        // Remove all currently shown io
        flpFileView.getChildren().clear();

        filesToShow = fileExplorer.getShownFiles();
        for (AbstractFile file : filesToShow) {
            FileButton fileButton = createFileButton(file);
            flpFileView.getChildren().add(fileButton);
        }
        lblVisualPath.setText(PathDisplayCorrection());
    }

    private FileButton createFileButton(AbstractFile file) {
        FileButton filebutton = new FileButton(file);

        filebutton.getStyleClass().add("FileButton");
        filebutton.setContentDisplay(ContentDisplay.TOP);
        filebutton.setOnMouseClicked(this::onFileButtonClick);

        // Add appropriate context menu
        return filebutton;
    }

    private void onFileButtonClick(MouseEvent event) {
        FileButton clickedButton = (FileButton) event.getSource();
        if (event.getClickCount() == 2)
            open(clickedButton);
    }

    private void open(FileButton fileButton) {
        if (fileButton.getFile() instanceof Folder) {
            fileExplorer.navigateTo((Folder) fileButton.getFile());
            updateDisplayedFiles();
        } else {

            try {
                Desktop.getDesktop().open(SettingsManager.getServerArchivePath().resolve(fileButton.getFile().getPath()).toFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String PathDisplayCorrection() {
        return fileExplorer.getCurrentPath();
    }

    public String getOperatingSystem() {
        String OS = System.getProperty("os.name");
        if (OS.startsWith("Windows"))
            return "Windows";
        else
            return "MacOS";
    }

    public void restoreFile(ActionEvent event) {
        TreeItem<AbstractFile> selectedItem = fileTreeView.getSelectionModel().getSelectedItem();
        AbstractFile selectedFile = selectedItem.getValue();
        try {
            FileManager.getInstance().restoreFile(selectedFile);
        } catch (IOException e) {
            e.printStackTrace();
            AlertBuilder.IOExceptionPopUp();
            LoggingErrorTools.log(e);
        }
        FileManager fileManager = FileManager.getInstance();
        update();
    }

    private void openFileTreeElement(TreeItem<AbstractFile> newValue) {
        fileTreeView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                AbstractFile file = newValue.getValue();
                if (file instanceof Document) {
                    try {
                        Desktop.getDesktop().open(SettingsManager.getServerArchivePath().resolve(file.getOSPath()).toFile());
                    } catch (IOException e) {
                        LoggingErrorTools.log(e);
                        AlertBuilder.IOExceptionPopUp();
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    private void addToolTip() {
        Tooltip restoreTooltip = new Tooltip(DMSApplication.getMessage("Archive.Tooltip.Restore"));

        Tooltip.install(btnRestore, restoreTooltip);

    }
}