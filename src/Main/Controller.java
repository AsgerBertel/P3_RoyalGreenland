package Main;

import Directory.CreateDirectory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;

import java.net.URL;

public class Controller {

    @FXML
    private Button btnTest;
    @FXML
    void test() {
        CreateDirectory createDirectory = new CreateDirectory();
        createDirectory.CreateFolder();
    }

}
