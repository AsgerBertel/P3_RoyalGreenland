package gui;

import app.ApplicationMode;
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

import java.nio.file.Path;
import java.util.concurrent.TimeoutException;

public abstract class GUITest extends ApplicationTest {
    private static String originalPath;

    @BeforeAll
    static final void setupApplication() throws Exception {
        Settings.loadSettings(ApplicationMode.ADMIN);
        originalPath = Settings.getServerDocumentsPath();
        Settings.setServerPath(TestUtil.getTestDocuments().toString());

        TestUtil.resetTestFiles();
        ApplicationTest.launch(DMSApplication.class, ApplicationMode.ADMIN.toString());

    }

    @AfterEach
    final void tearDown() throws TimeoutException {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    @AfterAll
    static void onTestEnd(){
        // Reset path in settings
        Settings.setServerPath(originalPath);
    }

    public void start(Stage stage) throws Exception {
        super.start(stage);
    }


    // Find a node by its' fx:id
    protected  <T extends Node> T findNode(final String fxID) {
        return (T) lookup(fxID).queryAll().iterator().next();
    }
}
