package gui;

import app.ApplicationMode;
import directory.Settings;
import javafx.scene.Node;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.testfx.framework.junit5.ApplicationTest;
import util.TestUtil;

import java.io.IOException;

public abstract class GUITest extends ApplicationTest {

    private static String originalPath;

    public void start(Stage stage) throws Exception {
        super.start(stage);
    }

    // Find a node by its' fx:id
    protected  <T extends Node> T findNode(final String fxID) {
        return (T) lookup(fxID).queryAll().iterator().next();
    }


    @BeforeAll @SuppressWarnings("Duplicates")
    static final void setupApplication() throws Exception {
        Settings.loadSettings(ApplicationMode.ADMIN);

        originalPath = Settings.getServerDocumentsPath();
        Settings.setServerPath(TestUtil.getTestDocuments().toString());

        ApplicationTest.launch(DMSApplication.class, ApplicationMode.ADMIN.toString());
    }

    @BeforeEach
    static void setupTest() throws IOException {
        TestUtil.resetTestFiles();
    }

    @AfterAll
    static void cleanUp(){
        // Reset path in settings
        Settings.setServerPath(originalPath);
    }

}