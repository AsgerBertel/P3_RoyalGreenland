package gui;

import app.ApplicationMode;
import directory.DocumentsTest;
import directory.Settings;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import util.TestUtil;
import java.util.concurrent.TimeoutException;

public abstract class GUITest extends ApplicationTest {

    public void start(Stage stage) throws Exception {
        super.start(stage);
    }


    // Find a node by its' fx:id
    protected  <T extends Node> T findNode(final String fxID) {
        return (T) lookup(fxID).queryAll().iterator().next();
    }
}
