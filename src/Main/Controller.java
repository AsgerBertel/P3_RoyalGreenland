package Main;

import Directory.DirectoryManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class Controller {

    @FXML
    private Button btnTest;

    @FXML
    private TextField txtFolderName;
    @FXML
    void test() {
        DirectoryManager directoryManager = new DirectoryManager();

        directoryManager.CreateFolder("C:\\p3_folders/", txtFolderName.getText());
    }

}
