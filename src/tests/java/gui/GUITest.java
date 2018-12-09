package gui;

import app.ApplicationMode;
import directory.Settings;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import util.TestUtil;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.TimeoutException;

public abstract class GUITest extends ApplicationTest {

    private static Path originalPath;
    protected DMSApplication dmsApplication;

    @Override
    public void start(Stage stage) throws Exception {
        stage.show();
        this.dmsApplication = DMSApplication.getDMSApplication();
    }

    @AfterEach
    void tearDown() throws TimeoutException {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    @BeforeAll @SuppressWarnings("Duplicates")
    static final void setupApplication() throws Exception {
        Settings.loadSettings(ApplicationMode.ADMIN);

        originalPath = Settings.getServerPath();
        Settings.setServerPath(TestUtil.getTestDocuments());
        Settings.setLanguage(DMSApplication.DK_LOCALE);

        ApplicationTest.launch(DMSApplication.class, ApplicationMode.ADMIN.toString());
    }

    @AfterAll
    static void cleanUp(){
        // Reset path in settings
        Settings.setServerPath(originalPath);
    }


    // Find a node by its' fx:id
    protected  <T extends Node> T findNode(final String fxID) {
        return (T) lookup(fxID).queryAll().iterator().next();
    }




}
