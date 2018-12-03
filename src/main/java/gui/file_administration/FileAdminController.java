package gui.file_administration;

import directory.DirectoryCloner;
import directory.FileManager;
import directory.Settings;
import directory.files.AbstractFile;
import directory.files.Document;
import directory.files.Folder;
import directory.plant.Plant;
import directory.plant.PlantManager;
import gui.*;

import gui.log.LogEvent;
import gui.log.LogEventType;
import gui.log.LoggingTools;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.awt.*;
import java.io.IOException;
import javax.naming.InvalidNameException;
import java.io.File;
import java.net.URL;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
import java.util.*;
import java.util.List;

public class FileAdminController implements TabController {

    @FXML
    public Button saveChangesButton;
    public VBox changesVBox;
    public Text lastUpdatedText;
    public ScrollPane changesScrollPane;
    public Button deleteFileButton;
    private ArrayList<PlantCheckboxElement> plantElements = new ArrayList<>();
    private FileTreeDragAndDrop fileTreeDragAndDrop;

    private DMSApplication dmsApplication;

    @FXML
    public Text plantListTitle;
    @FXML
    private VBox plantVBox;
    @FXML
    private TreeView<AbstractFile> fileTreeView;
    @FXML
    private Text plantCountText;

    private ArrayList<Plant> plants = new ArrayList<>();
    private TreeItem<AbstractFile> rootItem = new TreeItem<>();

    // The document last selected in the FileTree
    private AbstractFile selectedFile;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setFactoryListDisabled(true);
        fileTreeView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> onTreeItemSelected(oldValue, newValue));
        fileTreeView.setRoot(rootItem);
        fileTreeView.setShowRoot(true);


        fileTreeView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) openFileTreeElement(fileTreeView.getSelectionModel().getSelectedItem());
        });

        //todo make enter button open work
        fileTreeView.setOnKeyPressed(event -> {
            if (event.getCode().getCode() == 13) openFileTreeElement(fileTreeView.getSelectionModel().getSelectedItem());
        });
        fileTreeView.setContextMenu(new AdminFilesContextMenu(this));
        changesScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        fileTreeView.setCellFactory(new FileTreeDragAndDrop(this));
        watchRootFiles(Paths.get(Settings.getServerDocumentsPath()));
    }

    @Override
    public void initReference(DMSApplication dmsApplication) {
        this.dmsApplication = dmsApplication;
    }

    @Override
    public void update() {
        reloadFileTree();
        reloadPlantList();
        reloadChangesList();
    }


    private void reloadFileTree() {
        // Copy current item expansion state
        TreeState oldTreeState = new TreeState(fileTreeView);

        // Error here
        rootItem = FileTreeUtil.generateTree(FileManager.getInstance().getMainFilesRoot());
        oldTreeState.replicateTreeExpansion(rootItem);
        fileTreeView.setRoot(rootItem);
        selectedFile = null;
        fileTreeView.getRoot().setExpanded(true);
        setFactoryListDisabled(true);
        deleteFileButton.setDisable(true);
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

        if (selectedFile == null) setFactoryListDisabled(true);
        else if (selectedFile instanceof Document) setFactoryListDisabled(false);
    }

    // Called after a plant is toggled on or off in plant checklist
    private void onPlantToggle(PlantCheckboxElement plantElement) {
        Plant plant = plantElement.getPlant();

        if (plantElement.isSelected())
            plant.getAccessModifier().addDocument(((Document) selectedFile).getID());
        else
            plant.getAccessModifier().removeDocument(((Document) selectedFile).getID());
    }

    // Called when an item (containing an AbstractFile) is clicked in the FileTreeView
    public void onTreeItemSelected(TreeItem<AbstractFile> oldValue, TreeItem<AbstractFile> newValue) {
        if (newValue != null && newValue != oldValue) {
            AbstractFile chosenFile = newValue.getValue();
            clearPlantSelection();
            deleteFileButton.setDisable(false);

            if (chosenFile instanceof Document) {
                selectedFile = chosenFile;
                setFactoryListDisabled(false);
                onDocumentSelected();
            } else if (chosenFile instanceof Folder) {
                setFactoryListDisabled(true);
                selectedFile = chosenFile;
            }
        } else {
            deleteFileButton.setDisable(true);
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
        if (chosenFile == null) {
            return;
        } else if (chosenFile.isDirectory()) {
            // todo Show prompt telling user that they cannot upload directories
            return;
        }

        Path path = Paths.get(Settings.getServerDocumentsPath() + selectedFile.getOSPath() + File.separator + chosenFile.getName());

        if (Files.exists(path)){
            int i = OverwriteFilePopUP();
            if(i == 1){
                Optional<AbstractFile> oldFile = FileManager.getInstance().findInMainFiles(path);
                FileManager.getInstance().deleteFile(oldFile.get());
            } else if (i == 0){

                Optional<AbstractFile> oldFile = FileManager.getInstance().findInMainFiles(path);
                Optional<String> newName = renameFilePopUP();
                String newNameExt = newName.get() + "." + ((Document)oldFile.get()).getFileExtension();

                try {
                    FileManager.getInstance().renameFile(oldFile.get(), newNameExt);
                } catch (InvalidNameException e) {
                    e.printStackTrace();
                }
            } else {
                return;
            }
        }

        if (selectedFile instanceof Folder) {
            // Upload inside selected folder
            Document uploadedDoc = fileManager.uploadFile(chosenFile.toPath(), (Folder) selectedFile);
            fileManager.save();
        } else if (selectedFile instanceof Document) {
            // Upload as sibling to selected document
            Optional<Folder> parent = FileManager.findParent(selectedFile, FileManager.getInstance().getMainFilesRoot());
            if (parent.isPresent()) {
                Document uploadedDoc = fileManager.uploadFile(chosenFile.toPath(), parent.get());
            } else {
                // Upload to root
                Document uploadedDoc = fileManager.uploadFile(chosenFile.toPath());
            }

        } else if (selectedFile == null) {
            // Upload to root
            Document uploadedDoc = fileManager.uploadFile(chosenFile.toPath(), fileManager.getMainFilesRoot());
        }

        fileManager.save();
        update();
    }

    public int OverwriteFilePopUP() {
        Alert txtInputDia = new Alert(Alert.AlertType.CONFIRMATION);
        txtInputDia.setTitle(DMSApplication.getMessage("FileManager.PopUpOverwrite.Warning"));
        txtInputDia.setHeaderText(DMSApplication.getMessage("FileManager.PopUpOverwrite.Info"));
        ButtonType buttonTypeOverwrite = new ButtonType(DMSApplication.getMessage("FileManager.PopUpOverwrite.Overwrite"));
        ButtonType buttonTypeKeep = new ButtonType(DMSApplication.getMessage("FileManager.PopUpOverwrite.Keep"));
        ButtonType buttonTypeCancel = new ButtonType(DMSApplication.getMessage("FileManager.PopUpOverwrite.Cancel"), ButtonBar.ButtonData.CANCEL_CLOSE);
        txtInputDia.getButtonTypes().setAll(buttonTypeOverwrite, buttonTypeKeep, buttonTypeCancel);

        Optional<ButtonType> result = txtInputDia.showAndWait();

        if (result.get() == buttonTypeOverwrite) {
            return 1;
        } else if (result.get() == buttonTypeKeep) {
            return 0;
        }

        return -1;
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
        FileManager fileManager = FileManager.getInstance();
        Optional<String> folderName = createFolderPopUP();
        if (folderName.isPresent()) {
            if (selectedFile == null) {
                String name = folderName.get();
                Folder fol = null;
                try {
                    fol = FileManager.getInstance().createFolder(name);
                } catch (InvalidNameException e) {
                    e.printStackTrace(); // todo add exception handling
                } catch (IOException e) {
                    e.printStackTrace();
                    fileTreeView.getRoot().getChildren().add(FileTreeUtil.generateTree(fol));
                    LoggingTools.log(new LogEvent(name, LogEventType.CREATED));
                }
            } else if (selectedFile instanceof Folder) {
                String name = folderName.get();
                try {
                    Folder fol = FileManager.getInstance().createFolder(name, (Folder) selectedFile);
                } catch (InvalidNameException e) {
                    e.printStackTrace(); // todo Show alert that folder already exists
                } catch (IOException e) {
                    e.printStackTrace(); // todo add exception handling
                }
                LoggingTools.log(new LogEvent(name, LogEventType.CREATED));
            } else if (selectedFile instanceof Document) {
                String name = folderName.get();
                Optional<Folder> parent = FileManager.findParent(selectedFile, fileManager.getMainFilesRoot());


                if (parent.isPresent())
                    try { // todo add exception handling
                        fileManager.createFolder(name, parent.get());
                    } catch (InvalidNameException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                else {
                    try { // todo add exception handling
                        fileManager.createFolder(name);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InvalidNameException e) {
                        e.printStackTrace();
                    }
                }

                LoggingTools.log(new LogEvent(name, LogEventType.CREATED));
            }
        }
        FileManager.getInstance().save();
        update();
    }

    public Optional<String> createFolderPopUP() {
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
        FileManager.getInstance().save();
        update();
    }

    public void openFile() {
        if (selectedFile instanceof Folder) {
            fileTreeView.getSelectionModel().getSelectedItem().setExpanded(true);
        }
        if (selectedFile instanceof Document) {
            Document doc = (Document) selectedFile;
            try {
                Desktop.getDesktop().open(Paths.get(Settings.getServerDocumentsPath() + doc.getOSPath()).toFile());
            } catch (IOException e) {
                System.out.println("Could not open file");
                e.printStackTrace();
            }
        }
    }

    public void renameFile() {
        Optional<String> optName = renameFilePopUP();
        if (optName.isPresent()) {
            String name = optName.get();
            if (selectedFile instanceof Document) {
                Document doc = (Document) selectedFile;
                name = name + "." + doc.getFileExtension();
                try {
                    FileManager.getInstance().renameFile(doc, name);
                } catch (InvalidNameException e) {
                    System.out.println("Could not rename file");
                    e.printStackTrace();
                    // todo show alert
                    return;
                }
            }
            if (selectedFile instanceof Folder) {
                Folder fol = (Folder) selectedFile;
                try {
                    FileManager.getInstance().renameFile(fol,name);
                } catch (InvalidNameException e) {
                    e.printStackTrace();
                }
            }

            update();
        }
        FileManager.getInstance().save();
    }

    public Optional<String> renameFilePopUP() {
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
        if (fileTreeView.getSelectionModel().getSelectedItem() != null) {
            AbstractFile file = newValue.getValue();
            if (file instanceof Document) {
                    openFile();
            }
        }
    }


    /* ---- Changelist ---- */
    private synchronized void reloadChangesList() {
        changesVBox.getChildren().clear();
        List<LogEvent> unpublishedChanges = LoggingTools.getAllUnpublishedEvents();
        if (unpublishedChanges.size() <= 0) {
            saveChangesButton.setDisable(true);
            return;
        } else {
            saveChangesButton.setDisable(false);
        }

        for (LogEvent logEvent : unpublishedChanges)
            changesVBox.getChildren().add(new ChangeBox(logEvent));

        lastUpdatedText.setText(LoggingTools.getLastPublished());
    }

    public void onPublishChanges() {
        try {
            DirectoryCloner.publishFiles();
            LoggingTools.log(new LogEvent(LoggingTools.getAllUnpublishedEvents().size() + " " + DMSApplication.getMessage("Log.Changes"), LogEventType.CHANGES_PUBLISHED));
            update();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private synchronized ListCell<LogEvent> createLogEventListCell() {
        return new ListCell<LogEvent>() {
            @Override
            protected void updateItem(LogEvent item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getEventString());
                }
            }
        };
    }

    /**
     * Watches directory for changes, Listener only reacts on changes and calls update() incase invoked.
     * Thread sleeps for 0,2 hereafter, for good measure.
     * @param root path to directory to watch
     */
    private void watchRootFiles(Path root) {
        FileManager fileManager = FileManager.getInstance();
        Thread monitorThread;
        File directory = new File(root.toString());
        FileAlterationObserver observer = new FileAlterationObserver(directory);
        observer.addListener(new FileAlterationListener() {
            @Override
            public void onStart(FileAlterationObserver fileAlterationObserver) { }
            @Override
            public void onDirectoryCreate(File file) { }
            @Override
            public void onDirectoryChange(File file) { }
            @Override
            public void onDirectoryDelete(File file) { }
            @Override
            public void onFileCreate(File file) { }
            @Override
            public void onFileChange(File file) {
                // Don't register changes to temporary word files
                if (!(file.getName().charAt(0) == '~') || Files.exists(file.toPath())) {
                    Optional<AbstractFile> changedFile = fileManager.findInMainFiles(file.toPath());

                    if (changedFile.isPresent() && changedFile.get() instanceof Document) {
                        ((Document) changedFile.get()).setLastModified(LocalDateTime.now());
                        Platform.runLater(() -> {
                            LoggingTools.log(new LogEvent(changedFile.get().getName(), LogEventType.CHANGED));
                            update();
                        });
                    }
                }
            }
            @Override
            public void onFileDelete(File file) { }
            @Override
            public void onStop(FileAlterationObserver fileAlterationObserver) { }
        });
        try {
            observer.initialize();
        } catch (Exception e) {
            e.printStackTrace();
        }
        monitorThread = new Thread(() -> {
            while(true) {
                try {
                    observer.checkAndNotify();
                    Thread.sleep(200);
                } catch (InterruptedException e) { // todo error handling 10hif9s -kristian
                    e.printStackTrace();
                }
            }
        });
        monitorThread.setDaemon(true);
        monitorThread.start();
    }

}