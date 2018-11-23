package gui.file_administration;

import directory.FileManager;
import directory.files.AbstractFile;
import directory.files.Document;
import directory.files.Folder;
import directory.plant.Plant;
import directory.plant.PlantManager;
import gui.DMSApplication;
import gui.FileTreeUtil;
import gui.PlantCheckboxElement;

import gui.TabController;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.IOException;
import javax.naming.InvalidNameException;
import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class FileAdminController implements TabController {

    public ListView changesListView;
    public Button saveChangesButton;
    private ArrayList<PlantCheckboxElement> plantElements = new ArrayList<>();

    private ArrayList<Plant> plants = new ArrayList<>();

    private TreeItem<AbstractFile> rootItem;

    @FXML
    public Text plantListTitle;

    @FXML
    private VBox plantVBox;

    @FXML
    private TreeView<AbstractFile> fileTreeView;

    @FXML
    private Text plantCountText;

    // The document last selected in the FileTree
    private AbstractFile selectedFile;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setFactoryListDisabled(true);
        fileTreeView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> onTreeItemSelected(oldValue, newValue));
        fileTreeView.setShowRoot(false);
        fileTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> openFileTreeElement(newValue));
    }

    @Override
    public void update() {
        // Refresh file tree if the files have changed // todo test if functional
        TreeItem<AbstractFile> currentRoot = fileTreeView.getRoot();
        // todo This if statement doesnt work. It should only reload, if the content is changed or the root is null.
        // todo - It always reloads. - Philip
//        if(currentRoot == null || !((Folder)currentRoot.getValue()).getContents().equals(FileManager.getInstance().getMainFiles()))
        reloadFileTree();
        fileTreeView.getRoot().setExpanded(true);

        fileTreeView.setContextMenu(new AdminFilesContextMenu(this));
        reloadPlantList();
    }

    private void reloadFileTree(){
        rootItem = FileTreeUtil.generateTree(FileManager.getInstance().getMainFiles());
        fileTreeView.setRoot(rootItem);
        setFactoryListDisabled(true);
    }

    private void reloadPlantList() {
        plantElements.clear();
        plantVBox.getChildren().clear();

        plants.clear();
        plants.addAll(PlantManager.getInstance().getAllPlants());

        // Create all plant boxes and add them to the plantVBox
        for (Plant plant : plants) {
            PlantCheckboxElement checkBox = new PlantCheckboxElement(plant);
            checkBox.setOnSelectedListener(() -> onPlantToggle(checkBox));
            plantVBox.getChildren().add(checkBox);
            plantElements.add(checkBox);
        }

        plantCountText.setText("(" + plants.size() + ")");

        // Update selected plants according to the currently selected file
        onTreeItemSelected(null, fileTreeView.getSelectionModel().getSelectedItem());

        if(selectedFile == null) setFactoryListDisabled(true);
        else if(selectedFile instanceof Document) setFactoryListDisabled(false);
    }

    // Called after a plant is toggled on or off in plant checklist
    private void onPlantToggle(PlantCheckboxElement plantElement) {
        Plant plant = plantElement.getPlant();

        if (plantElement.isSelected()) {
            plant.getAccessModifier().addDocument(((Document) selectedFile).getID());
        } else {
            plant.getAccessModifier().removeDocument(((Document) selectedFile).getID());
        }
    }

    // Called when an item (containing an AbstractFile) is clicked in the FileTreeView
    public void onTreeItemSelected(TreeItem<AbstractFile> oldValue, TreeItem<AbstractFile> newValue) {
        if (newValue != null && newValue != oldValue) {
            AbstractFile chosenFile = newValue.getValue();
            clearPlantSelection();

            if (chosenFile instanceof Document) {
                selectedFile = chosenFile;
                setFactoryListDisabled(false);
                onDocumentSelected();
            } else if (chosenFile instanceof Folder) {
                setFactoryListDisabled(true);
                selectedFile = chosenFile;
            }
        }
    }

    // Disables clicking on elements in the factory list
    private void setFactoryListDisabled(boolean disabled) {
        for (PlantCheckboxElement element : plantElements)
            element.setDisable(disabled);
    }

    // Deselects all elements in the plant list
    private void clearPlantSelection() {
        for (PlantCheckboxElement element : plantElements)
            element.setSelected(false);
    }

    // Updates the plant list to reflect the AccessModifier of the chosen document
    private void onDocumentSelected() {
        Document document = (Document) selectedFile;
        for (PlantCheckboxElement element : plantElements) {
            if (element.getPlant().getAccessModifier().contains(document.getID()))
                element.setSelected(true);
        }
    }

    public void uploadDocument() {
        FileManager fileManager = FileManager.getInstance();

        File chosenFile = chooseFilePrompt(DMSApplication.getMessage("AdminFiles.PopUpUpload.ChooseDoc"));
        if(chosenFile == null){
            return;
        }else if(chosenFile.isDirectory()){
            // todo Show prompt telling user that they cannot upload directories
            return;
        }

        if (selectedFile instanceof Folder) {
            // Upload inside selected folder
            Document uploadedDoc = fileManager.uploadFile(chosenFile.toPath(), (Folder) selectedFile);
            fileTreeView.getSelectionModel().getSelectedItem().getChildren().add(FileTreeUtil.createTreeItem(uploadedDoc));
            fileManager.save();
        }else if (selectedFile instanceof Document) {
            // Upload as sibling to selected document
            Optional<Folder> parent = FileManager.findParent(selectedFile, FileManager.getInstance().getMainFilesRoot());
            if(parent.isPresent()){
                Document uploadedDoc = fileManager.uploadFile(chosenFile.toPath(), parent.get());
                fileTreeView.getSelectionModel().getSelectedItem().getParent().getChildren().add(FileTreeUtil.createTreeItem(uploadedDoc));
            }else{
                // Upload to root
                Document uploadedDoc = fileManager.uploadFile(chosenFile.toPath());
                fileTreeView.getRoot().getChildren().add(FileTreeUtil.createTreeItem(uploadedDoc));
            }
        } else if (selectedFile == null){
            // Upload to root
            Document uploadedDoc = fileManager.uploadFile(chosenFile.toPath());
            fileTreeView.getRoot().getChildren().add(FileTreeUtil.createTreeItem(uploadedDoc));
        }

        fileManager.save();
        //todo if file already exists, the old one is deleted but this can only happen once.
        //todo make some kind of counter to file name
    }

    // Prompts the user to choose a file (return null if cancelled)
    private File chooseFilePrompt(String message) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(message);
        File chosenFile = fileChooser.showOpenDialog(new Stage());
        if (chosenFile == null) return null;
        return chosenFile;
    }

    public void createFolder() {

        Optional<String> folderName = createFolderPopUP();
        Folder parent;
        if (folderName.isPresent()){
            if (selectedFile == null){
                String name = folderName.get();
                Folder fol = FileManager.getInstance().createFolder(name);
                fileTreeView.getRoot().getChildren().add(FileTreeUtil.generateTree(fol));
            }else if (selectedFile instanceof Folder) {
                String name = folderName.get();
                Folder fol = FileManager.getInstance().createFolder(name, (Folder) selectedFile);
                fileTreeView.getSelectionModel().getSelectedItem().getChildren().add(FileTreeUtil.generateTree(fol));
            }else if(selectedFile instanceof Document){
                String name = folderName.get();
                Optional<Folder> parentOpt = FileManager.findParent(selectedFile, FileManager.getInstance().getMainFilesRoot());

                if(parentOpt.isPresent()) {
                    parent = parentOpt.get();

                    Folder fol;
                    if (!parent.equals(FileManager.getInstance().getMainFilesRoot()))
                        fol = FileManager.getInstance().createFolder(name, parent);
                    else
                        fol = FileManager.getInstance().createFolder(name);
                    fileTreeView.getSelectionModel().getSelectedItem().getParent().getChildren().add(FileTreeUtil.generateTree(fol));
                }
            }
        }

        FileManager.getInstance().save();
    }

    public Optional<String> createFolderPopUP(){
        TextInputDialog txtInputDia = new TextInputDialog();
        txtInputDia.setTitle(DMSApplication.getMessage("AdminFiles.PopUp.CreateFolder"));
        txtInputDia.setHeaderText(DMSApplication.getMessage("AdminFiles.PopUp.CreateFolderInfo"));
        txtInputDia.getEditor().setPromptText(DMSApplication.getMessage("AdminFiles.PopUp.TypeFolderName"));
        txtInputDia.setGraphic(new ImageView("icons/menu/addfolder.png"));
        ((Button) txtInputDia.getDialogPane().lookupButton(ButtonType.OK)).setText(DMSApplication.getMessage("AdminFiles.PopUp.CreateFolder"));
        ((Button) txtInputDia.getDialogPane().lookupButton(ButtonType.CANCEL)).setText(DMSApplication.getMessage("AdminFiles.PopUp.Cancel"));

        return txtInputDia.showAndWait();
    }

    public void deleteFile() {
        TreeItem<AbstractFile> selectedItem = fileTreeView.getSelectionModel().getSelectedItem();
        FileManager.getInstance().deleteFile(selectedItem.getValue());
        selectedItem.getParent().getChildren().remove(selectedItem);
        update();
        FileManager.getInstance().save();
    }

    public void openFile(){
        if(selectedFile instanceof Folder){
            fileTreeView.getSelectionModel().getSelectedItem().setExpanded(true);
        }
        if(selectedFile instanceof Document) {
            Document doc = (Document)selectedFile;
            try {
                doc.openDocument();
            } catch (IOException e) {
                System.out.println("Could not open file");
                e.printStackTrace();
            }
        }
    }

    public void renameFile(){
        Optional<String> optName = renameFilePopUP();
        if(optName.isPresent()){
            String name = optName.get();
            if(selectedFile instanceof Document){
                Document doc = (Document)selectedFile;
                try {
                    doc.renameFile(name);
                } catch (InvalidNameException e) {
                    System.out.println("Could not rename file");
                    e.printStackTrace();
                }
            }
            if(selectedFile instanceof Folder){
                Folder fol = (Folder)selectedFile;
                fol.renameFile(name);
            }
            // Todo tree closes when it updates. - Philip
            update();
        }
        FileManager.getInstance().save();
    }

    public Optional<String> renameFilePopUP(){
        TextInputDialog txtInputDia = new TextInputDialog();
        txtInputDia.setTitle(DMSApplication.getMessage("AdminFiles.PopUpRename.RenameFile"));
        txtInputDia.setHeaderText(DMSApplication.getMessage("AdminFiles.PopUpRename.RenameFileInfo"));
        txtInputDia.getEditor().setPromptText(DMSApplication.getMessage("AdminFiles.PopUpRename.TypeNewName"));
        txtInputDia.setGraphic(new ImageView());
        ((Button) txtInputDia.getDialogPane().lookupButton(ButtonType.OK)).setText(DMSApplication.getMessage("AdminFiles.PopUpRename.NewName"));
        ((Button) txtInputDia.getDialogPane().lookupButton(ButtonType.CANCEL)).setText(DMSApplication.getMessage("AdminFiles.PopUpRename.Cancel"));

        return txtInputDia.showAndWait();
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

    
    public void onChangeMade(){

    }



}