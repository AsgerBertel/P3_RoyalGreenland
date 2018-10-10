package Main;

import Directory.CreateDirectory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

import java.net.URL;

public class Controller {

    @FXML
    private Button btnTest;

    @FXML
    private TextField txtFolderName;
    @FXML
    void test() {

        CreateDirectory test = new CreateDirectory();
        test.CreateFolder("C:\\p3_folders/", txtFolderName.getText());
    }

}
