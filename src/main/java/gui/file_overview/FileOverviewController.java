package gui.file_overview;

import directory.*;
import directory.plant.AccessModifier;
import directory.files.AbstractFile;
import directory.files.Document;
import directory.files.Folder;
import directory.plant.Plant;
import directory.plant.PlantManager;
import gui.FileTreeGenerator;
import gui.TabController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
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
        update();
    }

    @Override
    public void update() {
        rootItem = FileTreeGenerator.generateTree(FileManager.getInstance().getAllContent().get(0));
        fileTreeView.setRoot(rootItem);
        PlantManager.getInstance().readFromJsonFile();
        plantList = FXCollections.observableList(PlantManager.getInstance().getAllPlants());
        drdPlant.setItems(plantList);


        /* todo if a plant is first selected in the file overview and then deleted, the drdplant should display prompt text again.
        if(!PlantManager.getInstance().getAllPlants().contains(drdPlant.getSelectionModel().getSelectedItem())){
            drdPlant.getPromptText();
        }*/
    }

    @FXML
    void getSelectedPlant(ActionEvent event) {
        fileExplorer = new FileExplorer((Folder) FileManager.getInstance().getAllContent().get(0), drdPlant.getSelectionModel().getSelectedItem());
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

    // Creates a FileButton from a File and adds
    private FileButton createFileButton(AbstractFile file) {
        FileButton filebutton = new FileButton(file);

        filebutton.getStyleClass().add("FileButton");
        filebutton.setContentDisplay(ContentDisplay.TOP);
        filebutton.setOnMouseClicked(event -> onFileButtonClick(event));
        // Add appropriate context menu
        if (file instanceof Folder) {
            filebutton.setContextMenu(new ReadOnlyFolderContextMenu(this, filebutton));
        } else {
            filebutton.setContextMenu(new ReadOnlyDocumentContextMenu(this, filebutton));
        }

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
    public void openPreviusFolder() {
        fileExplorer.navigateBack();
        updateDisplayedFiles();
    }

    public String PathDisplayCorrection() {
        int BracketCounter = 0;
        String NewString;
        if (getOperatingSystem() == "Windows")
            NewString = fileExplorer.getCurrentFolder().getPath().toString().replaceAll(File.separator + File.separator, " > ");
        else
            NewString = fileExplorer.getCurrentFolder().getPath().toString().replaceAll(File.separator, " > ");

        for (int i = 0; i < NewString.length(); i++) {
            if (NewString.charAt(i) == '\\')
                BracketCounter++;
        }
        if (BracketCounter > 2) {
            NewString = fileExplorer.getCurrentFolder().getPath().toString().replaceAll(fileExplorer.getCurrentFolder().getParentPath().toString(),".//") ;
        } else {
            NewString = NewString.substring(NewString.indexOf("Main Files"));

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


}
