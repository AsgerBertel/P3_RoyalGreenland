package gui.file_overview;

import directory.*;
import directory.files.AbstractFile;
import directory.files.Document;
import directory.files.Folder;
import directory.plant.AccessModifier;
import directory.plant.Plant;
import directory.plant.PlantManager;
import gui.FileTreeUtil;
import gui.TabController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.FlowPane;

import java.io.File;
import java.io.IOException;
import java.net.URL;
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

    @FXML // Called upon loading the fxml and constructing the gui
    public void initialize(URL location, ResourceBundle resources) {
        fileTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> openFileTreeElement(newValue));
        plantList = FXCollections.observableList(PlantManager.getInstance().getAllPlants());
        drdPlant.setItems(plantList);
        fileTreeView.setShowRoot(false);
    }

    @Override
    public void update() {
        // Refresh file tree if the files have changed // todo test if functional
        TreeItem<AbstractFile> treeRoot = fileTreeView.getRoot();
        if(treeRoot == null || !treeRoot.getValue().equals(FileManager.getInstance().getAllContent().get(0))){
            reloadFileTree();
        }

        plantList = FXCollections.observableList(PlantManager.getInstance().getAllPlants());
        drdPlant.setItems(plantList);
    }

    private void reloadFileTree(){
        Folder rootFolder = (Folder) FileManager.getInstance().getAllContent().get(0);
        rootItem = FileTreeUtil.generateTree(rootFolder);
        fileTreeView.setRoot(rootItem);
    }

    @FXML
    void onPlantSelected(ActionEvent event) {
        fileExplorer = new FileExplorer((Folder) FileManager.getInstance().getAllContent().get(0), drdPlant.getSelectionModel().getSelectedItem());
        AccessModifier accessModifier = (fileExplorer.getSelectedPlant() == null) ? null : fileExplorer.getSelectedPlant().getAccessModifier();
        rootItem = FileTreeUtil.generateTree((Folder) FileManager.getInstance().getAllContent().get(0), accessModifier);
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
    public void openPreviousFolder() {
        if(drdPlant.getSelectionModel().getSelectedItem() != null){
            fileExplorer.navigateBack(FileManager.getInstance().getAllContent());
            updateDisplayedFiles();
        }
    }

    public String PathDisplayCorrection() {
        int BracketCounter = 0;
        String NewString;
        if (getOperatingSystem() == "Windows")
            NewString = fileExplorer.getCurrentFolder().getPath().toString().replaceAll(File.separator + File.separator, " > ");
        else
            NewString = fileExplorer.getCurrentFolder().getPath().toString().replaceAll(File.separator, " > ");

        for (int i = 0; i < NewString.length(); i++) {
            if (NewString.charAt(i) == '>')
                BracketCounter++;
        }

        if (BracketCounter > 3) {
            NewString = "../" + fileExplorer.getCurrentFolder().getName();
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
                        ((Document) file).openDocument();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


}
