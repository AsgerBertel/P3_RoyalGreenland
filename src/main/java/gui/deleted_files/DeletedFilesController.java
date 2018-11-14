package gui.deleted_files;

import directory.FileExplorer;
import directory.FileManager;
import directory.files.AbstractFile;
import directory.files.Document;
import directory.files.Folder;
import gui.FileTreeUtil;
import gui.TabController;
import gui.file_overview.FileButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class DeletedFilesController implements TabController {

    private FileExplorer fileExplorer;

    private TreeItem<AbstractFile> rootItem;

    List<AbstractFile> filesToShow;

    @FXML
    private VBox leftSideVbox;

    @FXML
    private TreeView<AbstractFile> fileTreeView;

    @FXML
    private VBox vboxTop;

    @FXML
    private Button btnReturn;

    @FXML
    private Label lblVisualPath;

    @FXML
    private ScrollPane scpFileView;

    @FXML
    private FlowPane flpFileView;

    @Override
    public void update() {
        rootItem = FileTreeUtil.generateTree((Folder) FileManager.getInstance().getArchive().get(0));
        fileTreeView.setRoot(rootItem);
        fileTreeView.setShowRoot(false);
    }

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        fileTreeView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2)
                fileTreeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> openFileTreeElement(newValue));
        });

        update();
    }

    @FXML
    private void updateDisplayedFiles() {
        // Remove all currently shown files
        flpFileView.getChildren().clear();

        filesToShow = fileExplorer.getCurrentFolderContent();
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
        if (BracketCounter > 2) {
            //NewString = fileExplorer.getCurrentFolder().getPath().getParent().toString() + fileExplorer.getCurrentFolder().getPath().toString();
        } else {
            NewString = NewString.substring(NewString.indexOf("Archive"));

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

    public void restoreDocument(ActionEvent event) {
        TreeItem<AbstractFile> selectedItem = fileTreeView.getSelectionModel().getSelectedItem();
        AbstractFile selectedFile = selectedItem.getValue();
        try {
            FileManager.getInstance().restoreFile(selectedFile);
            selectedItem.getParent().getChildren().remove(selectedItem);
        } catch (IOException e) {
            e.printStackTrace(); // todo error handling
        }
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
