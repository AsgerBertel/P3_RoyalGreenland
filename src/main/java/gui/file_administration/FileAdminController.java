package gui.file_administration;

import directory.files.AbstractFile;
import directory.files.Folder;
import gui.FileTreeGenerator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;

import java.net.URL;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class FileAdminController implements Initializable {

    @FXML
    private TreeView fileTree;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void addDocument(ActionEvent actionEvent) {
        fileTree.setRoot(FileTreeGenerator.generateTree(new Folder(Paths.get(System.getProperty("user.dir") + "/Sample Files/Main Files"))));
    }

    public void createFolder(ActionEvent actionEvent) {
    }

    public void deleteFile(ActionEvent actionEvent) {
    }


}
