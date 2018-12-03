package gui;

import app.ApplicationMode;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;

import java.util.concurrent.TimeoutException;

public abstract class GUITest extends ApplicationTest {

    @BeforeAll
    static final void setupApplication() throws Exception {
        ApplicationTest.launch(DMSApplication.class, ApplicationMode.ADMIN.toString());
    }

    @Override
    public final void start(Stage stage) throws Exception {
        super.start(stage);
    }

    @AfterEach
    final void tearDown() throws TimeoutException {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    // Find a node by its' fx:id
    protected  <T extends Node> T findNode(final String fxID) {
        return (T) lookup(fxID).queryAll().iterator().next();
    }
}
