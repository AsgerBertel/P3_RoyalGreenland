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
import javafx.fxml.FXML;
import javafx.scene.control.*;

import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import javax.naming.InvalidNameException;
import java.io.File;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public class FileAdminController implements TabController {

    @FXML
    public Button saveChangesButton;
    public VBox changesVBox;
    public Text lastUpdatedText;
    public ScrollPane changesScrollPane;
    public Button deleteFileButton;
    private ArrayList<PlantCheckboxElement> plantElements = new ArrayList<>();

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

    // Watcher used for monitoring files and checking for changes
    private WatchService watchService;
    private ArrayList<WatchKey> watchKeys = new ArrayList<>();
    private Thread watchThread;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setFactoryListDisabled(true);
        fileTreeView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> onTreeItemSelected(oldValue, newValue));
        fileTreeView.setRoot(rootItem);
        fileTreeView.setShowRoot(false);
        fileTreeView.setOnMouseClicked(event -> {if(event.getClickCount() == 2) openFileTreeElement(fileTreeView.getSelectionModel().getSelectedItem());});
        fileTreeView.setContextMenu(new AdminFilesContextMenu(this));
        changesScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        try {
            watchService = FileSystems.getDefault().newWatchService();
        } catch (IOException e) {
            e.printStackTrace();
            // todo look into (javadocs) why this might throw a ClosedFileSystem exception and handle appropriately
        }

    }

    @Override
    public void update() {
        // Refresh file tree if the files have changed // todo test if functional
        TreeItem<AbstractFile> currentRoot = fileTreeView.getRoot();
        // todo This if statement doesnt work. It should only reload, if the content is changed or the root is null.
        // todo - It always reloads. - Philip
//      if(currentRoot == null || !((Folder)currentRoot.getValue()).getContents().equals(FileManager.getInstance().getMainFiles()))
        reloadFileTree();
        reloadPlantList();
        updateChangesList();
        updateFileWatcher();
    }


    private void reloadFileTree() {
        // Copy current item expansion state
        TreeState oldTreeState = new TreeState(fileTreeView);

        rootItem = FileTreeUtil.generateTree(FileManager.getInstance().getMainFiles());
        oldTreeState.replicateTreeExpansion(rootItem);
        fileTreeView.setRoot(rootItem);
        selectedFile = null;

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
        } else {
            plant.getAccessModifier().removeDocument(((Document) selectedFile).getID());
        }
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
        }else{
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
            Document uploadedDoc = fileManager.uploadFile(chosenFile.toPath());
        }

        fileManager.save();
        update();
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
        FileManager fileManager = FileManager.getInstance();
        Optional<String> folderName = createFolderPopUP();
        if (folderName.isPresent()) {
            if (selectedFile == null) {
                String name = folderName.get();
                Folder fol = null;
                try {
                    fol = FileManager.getInstance().createFolder(name);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InvalidNameException e) {
                    e.printStackTrace(); // todo add exception handling
                }
                fileTreeView.getRoot().getChildren().add(FileTreeUtil.generateTree(fol));
                LoggingTools.log(new LogEvent(name, LogEventType.CREATED));
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
                    try{ // todo add exception handling
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
                Desktop.getDesktop().open(Paths.get(doc.getOSPath().toString()).toFile());
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
            if(selectedFile instanceof Document){
                Document doc = (Document)selectedFile;
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
                fol.renameFile(name);
            }
            // Todo tree closes when it updates. - Philip
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
        AbstractFile file = newValue.getValue();

        if (file instanceof Document) {
            try {
                Desktop.getDesktop().open(Paths.get(Settings.getServerDocumentsPath() + file.getOSPath()).toFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /* ---- Changelist ---- */
    private synchronized void updateChangesList() {
        changesVBox.getChildren().clear();
        List<LogEvent> unpublishedChanges = LoggingTools.getAllUnpublishedEvents();
        if(unpublishedChanges.size() <= 0){
            saveChangesButton.setDisable(true);
            return;
        }else{
            saveChangesButton.setDisable(false);
        }

        for(LogEvent logEvent : unpublishedChanges)
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

    // Recursively applies a watcher to every directory within the file tree root
    private void updateFileWatcher() {
        Path root = Paths.get(Settings.getServerDocumentsPath());

        // Remove current watch keys
        for(WatchKey key : watchKeys)
            key.cancel();

        try {
            Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    watchKeys.add(dir.register(watchService, ENTRY_MODIFY));
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            // todo - Handle exception. sorry. - Magnus
        }

        startWatchThread();
    }

    private void startWatchThread() {
        // Don't start watcher thread if it's already running
        if(watchThread != null && watchThread.isAlive()) return;

        watchThread = new Thread(this::run);
        watchThread.setDaemon(true);
        watchThread.start();
    }


    private void run() {
        WatchKey key;
        FileManager fileManager = FileManager.getInstance();
        try {
            while (null != (key = watchService.take())) {
                for (WatchEvent<?> event : key.pollEvents()) {
                    @SuppressWarnings("unchecked") // This watchService only generates keys from paths
                    WatchEvent<Path> we = (WatchEvent<Path>) event;

                    // Get the path of the parent folder whose child was changed
                    Path path = (Path) key.watchable();

                    // Add the file name of the changed file to the path
                    Path fileName = we.context();
                    path = path.resolve(fileName);

                    // Don't register changes to temporary word files
                    if(fileName.toString().charAt(0) == '~' || !Files.exists(path))
                        continue;

                    Optional<AbstractFile> changedFile = fileManager.findInMainFiles(path);

                    if (changedFile.isPresent() && changedFile.get() instanceof Document){
                        ((Document) changedFile.get()).setLastModified(LocalDateTime.now());
                        Platform.runLater(() -> {
                            LoggingTools.log(new LogEvent(changedFile.get().getName(), LogEventType.CHANGED));
                            updateChangesList();
                        });
                    }

                }
                /* On saving a file the filesystem occasionally registers two changes instead of one. These occur within
                * a very short time frame. To ensure that only one change is registered the listener is paused for a
                * short while. */
                Thread.sleep(100);
                // Reset the key to start listening for changes on this file again
                key.reset();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}