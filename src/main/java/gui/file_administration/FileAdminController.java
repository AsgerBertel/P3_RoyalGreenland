package gui.file_administration;


import directory.update.DirectoryCloner;
import directory.FileManager;
import directory.SettingsManager;
import directory.files.AbstractFile;
import directory.files.Document;
import directory.files.Folder;
import directory.plant.Plant;
import directory.plant.PlantManager;
import gui.*;

import gui.log.LogEvent;
import gui.log.LogEventType;
import gui.log.LoggingErrorTools;
import gui.log.LogManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.awt.*;
import java.io.IOException;
import java.io.File;
import java.net.URL;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class FileAdminController implements TabController {

    @FXML
    public Button publishChangesButton;
    public VBox changesVBox;
    public Text lastUpdatedText;
    public ScrollPane changesScrollPane;
    @FXML
    public Button deleteFileButton;
    private ArrayList<PlantCheckboxElement> plantElements = new ArrayList<>();
    private FileTreeDragAndDrop fileTreeDragAndDrop;
    @FXML
    private Button createFolderButton;
    @FXML
    private Button uploadButton;
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

    private AtomicBoolean running = new AtomicBoolean();
    private Thread monitorThread;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        setFactoryListDisabled(true);
        fileTreeView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> onTreeItemSelected(oldValue, newValue));
        fileTreeView.setRoot(rootItem);
        fileTreeView.setShowRoot(true);
        addToolTip();
        setContextMenu();
        fileTreeView.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER)
                openFileTreeElement(fileTreeView.getSelectionModel().getSelectedItem());
        });


        changesScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        fileTreeView.setCellFactory(new FileTreeDragAndDrop(this));
        watchRootFiles(SettingsManager.getServerDocumentsPath());
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

        if (plantElement.isSelected()) {
            plant.getAccessModifier().addDocument(((Document) selectedFile).getID());
            LogManager.log(new LogEvent(plant.getName(), selectedFile.getName(), LogEventType.PLANT_ACCESS_GIVEN));
        } else {
            plant.getAccessModifier().removeDocument(((Document) selectedFile).getID());
            LogManager.log(new LogEvent(plant.getName(), selectedFile.getName(), LogEventType.PLANT_ACCESS_REMOVED));
        }

        reloadChangesList();
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
            AlertBuilder.uploadDocumentPopUp();
            return;
        }
        if (selectedFile == null)
            selectedFile = fileManager.getMainFilesRoot();


        Path dstPath;
        Folder parent;
        if (selectedFile instanceof Document) {
            Optional<Folder> parentOpt = FileManager.findParent(selectedFile, FileManager.getInstance().getMainFilesRoot());
            if (parentOpt.isPresent()) {
                dstPath = SettingsManager.getServerDocumentsPath().resolve(parentOpt.get().getOSPath()).resolve(chosenFile.getName());
                parent = parentOpt.get();
            } else {
                dstPath = SettingsManager.getServerDocumentsPath().resolve(chosenFile.getName());
                parent = FileManager.getInstance().getMainFilesRoot();
            }
        } else {
            dstPath = SettingsManager.getServerDocumentsPath().resolve(selectedFile.getOSPath()).resolve(chosenFile.getName());
            parent = (Folder) selectedFile;
        }


        if (FileManager.getInstance().fileExists(dstPath)) {
            int i = OverwriteFilePopUP();
            if (i == 1) {
                Optional<AbstractFile> oldFile = FileManager.getInstance().findInMainFiles(dstPath);
                FileManager.getInstance().deleteFile(oldFile.get());
            } else if (i != 0) {
                return;
            }
        }

        Document uploadedDoc = fileManager.uploadFile(chosenFile.toPath(), parent);

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
                try {
                    FileManager.getInstance().createFolder(name);
                } catch (FileAlreadyExistsException e) {
                    e.printStackTrace();
                    AlertBuilder.fileAlreadyExistsPopUp();
                } catch (IOException e) {
                    e.printStackTrace();
                    AlertBuilder.IOExceptionPopUp();
                    LoggingErrorTools.log(e);
                }
            } else if (selectedFile instanceof Folder) {
                String name = folderName.get();
                try {
                    FileManager.getInstance().createFolder(name, (Folder) selectedFile);
                } catch (FileAlreadyExistsException e) {
                    e.printStackTrace();
                    AlertBuilder.fileAlreadyExistsPopUp();
                } catch (IOException e) {
                    e.printStackTrace();
                    AlertBuilder.IOExceptionPopUp();
                    LoggingErrorTools.log(e);
                }
                LogManager.log(new LogEvent(name, LogEventType.CREATED));
            } else if (selectedFile instanceof Document) {
                String name = folderName.get();
                Optional<Folder> parent = FileManager.findParent(selectedFile, fileManager.getMainFilesRoot());

                if (parent.isPresent())
                    try {
                        fileManager.createFolder(name, parent.get());
                    } catch (FileAlreadyExistsException e) {
                        e.printStackTrace();
                        AlertBuilder.fileAlreadyExistsPopUp();
                    } catch (IOException e) {
                        e.printStackTrace();
                        AlertBuilder.IOExceptionPopUp();
                        LoggingErrorTools.log(e);
                    }

                else {
                    try {
                        fileManager.createFolder(name);

                    } catch (FileAlreadyExistsException e) {
                        e.printStackTrace();
                        AlertBuilder.fileAlreadyExistsPopUp();
                    } catch (IOException e) {
                        e.printStackTrace();
                        AlertBuilder.IOExceptionPopUp();
                        LoggingErrorTools.log(e);
                    }
                }
                LogManager.log(new LogEvent(name, LogEventType.CREATED));
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
        if (!(selectedFile.getOSPath().toString().equals(""))) {
            TreeItem<AbstractFile> selectedItem = fileTreeView.getSelectionModel().getSelectedItem();
            FileManager.getInstance().deleteFile(selectedItem.getValue());
            FileManager.getInstance().save();
            update();
        }
    }

    public void openFile() {
        if (selectedFile instanceof Folder) {
            fileTreeView.getSelectionModel().getSelectedItem().setExpanded(true);
        }
        if (selectedFile instanceof Document) {
            Document doc = (Document) selectedFile;
            try {
                Desktop.getDesktop().open(SettingsManager.getServerDocumentsPath().resolve(doc.getOSPath()).toFile());
            } catch (IOException e) {
                LoggingErrorTools.log(e);
                AlertBuilder.IOExceptionPopUp();
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
                } catch (FileAlreadyExistsException e) {
                    e.printStackTrace();
                    AlertBuilder.fileAlreadyExistsPopUp();
                    return;
                }
            }
            if (selectedFile instanceof Folder) {
                Folder fol = (Folder) selectedFile;
                try {
                    FileManager.getInstance().renameFile(fol, name);
                } catch (FileAlreadyExistsException e) {
                    AlertBuilder.fileAlreadyExistsPopUp();
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
        List<LogEvent> unpublishedChanges = LogManager.getAllUnpublishedEvents();
        if (unpublishedChanges.size() <= 0) {
            publishChangesButton.setDisable(true);
            return;
        } else {
            publishChangesButton.setDisable(false);
        }

        for (LogEvent logEvent : unpublishedChanges)
            changesVBox.getChildren().add(new ChangeBox(logEvent));
    }

    public void onPublishChanges() {
        try {
            DirectoryCloner.publishFiles();
            LogManager.log(new LogEvent(LogManager.getAllUnpublishedEvents().size() + " " + DMSApplication.getMessage("Log.Changes"), LogEventType.CHANGES_PUBLISHED));
            update();
            lastUpdatedText.setText(LogManager.getLastPublished());
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
     * Watches directory for changes, Listener only reacts on changes and calls update() in case invoked.
     * Thread sleeps for 0,2 hereafter, for good measure.
     *
     * @param root path to directory to watch
     */
    private void watchRootFiles(Path root) {
        running.set(true);
        FileManager fileManager = FileManager.getInstance();
        File directory = new File(root.toString());
        FileAlterationObserver observer = new FileAlterationObserver(directory);
        observer.addListener(new FileAlterationListener() {
            @Override
            public void onStart(FileAlterationObserver fileAlterationObserver) {
            }

            @Override
            public void onDirectoryCreate(File file) {
            }

            @Override
            public void onDirectoryChange(File file) {
            }

            @Override
            public void onDirectoryDelete(File file) {
            }

            @Override
            public void onFileCreate(File file) {
            }

            @Override
            public void onFileChange(File file) {
                // Don't register changes to temporary word files
                if (!(file.getName().charAt(0) == '~') || Files.exists(file.toPath())) {
                    Optional<AbstractFile> changedFile = fileManager.findInMainFiles(file.toPath());

                    if (changedFile.isPresent() && changedFile.get() instanceof Document) {
                        ((Document) changedFile.get()).setLastModified(LocalDateTime.now());
                        Platform.runLater(() -> {
                            LogManager.log(new LogEvent(changedFile.get().getName(), LogEventType.CHANGED));
                            update();
                        });
                    }
                }
            }

            @Override
            public void onFileDelete(File file) {
            }

            @Override
            public void onStop(FileAlterationObserver fileAlterationObserver) {
            }
        });
        try {
            observer.initialize();
        } catch (Exception e) {
            LoggingErrorTools.log(e); // todo maybe Alert? -kristian
            e.printStackTrace();
        }
        monitorThread = new Thread(() -> {
            while (running.get()) {
                try {
                    observer.checkAndNotify();
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    AlertBuilder.interruptedExceptionShutdownPopUp("FileMonitor Thread");
                    LoggingErrorTools.log(e, 22);
                    e.printStackTrace();
                    System.exit(22);
                }
            }
        });
        monitorThread.setName("FileMonitorThread");
        monitorThread.setDaemon(true);
        monitorThread.start();
    }

    private void addToolTip() {
        Tooltip archiveToolTip = new Tooltip(DMSApplication.getMessage("AdminFiles.Tooltip.Arkiver"));
        Tooltip newFolderTooltip = new Tooltip(DMSApplication.getMessage("AdminFiles.Tooltip.CreateFolder"));
        Tooltip newDocumentTooltip = new Tooltip(DMSApplication.getMessage("AdminFiles.Tooltip.UploadFile"));
        Tooltip.install(deleteFileButton, archiveToolTip);
        Tooltip.install(createFolderButton, newFolderTooltip);
        Tooltip.install(uploadButton, newDocumentTooltip);
    }
    private void setContextMenu(){
        AdminFilesContextMenu adminFilesContextMenu = new AdminFilesContextMenu(this);
        fileTreeView.setContextMenu(adminFilesContextMenu);
        fileTreeView.setOnMouseClicked(event -> {
            fileTreeView.setContextMenu(adminFilesContextMenu);
            if (selectedFile != null) {
                if (selectedFile.getOSPath().toString().equals("")) {
                    if (adminFilesContextMenu.getItems().size() == 5) {
                        adminFilesContextMenu.getItems().remove(3);
                        adminFilesContextMenu.getItems().remove(2);
                        adminFilesContextMenu.getItems().remove(1);
                    }
                } else
                    fileTreeView.setContextMenu(new AdminFilesContextMenu(this));
                if (event.getClickCount() == 2) openFileTreeElement(fileTreeView.getSelectionModel().getSelectedItem());
            }
        });
    }
    public void stopRunning() {
        running.set(false);
    }
    public void startRunning() {
        if(!running.get())
            watchRootFiles(SettingsManager.getServerDocumentsPath());
    }
}