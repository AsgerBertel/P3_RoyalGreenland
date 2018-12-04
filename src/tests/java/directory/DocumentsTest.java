package directory;

import app.ApplicationMode;
import gui.DMSApplication;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import util.TestUtil;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class DocumentsTest {

    private static String originalPath;

    @BeforeAll
    static final void setupApplication() throws Exception {
        Settings.loadSettings(ApplicationMode.ADMIN);
        originalPath = Settings.getServerDocumentsPath();
        Settings.setServerPath(TestUtil.getTestDocuments().toString());

        TestUtil.resetTestFiles();
        ApplicationTest.launch(DMSApplication.class, ApplicationMode.ADMIN.toString());
    }

    @AfterAll
    static void onTestEnd(){
        // Reset path in settings
        Settings.setServerPath(originalPath);
    }



}
