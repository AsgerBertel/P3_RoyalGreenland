package directory;

import app.ApplicationMode;
import gui.DMSApplication;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.testfx.framework.junit5.ApplicationTest;
import util.TestUtil;

import java.nio.file.Path;

public class DocumentsTest {

    private static Path originalPath;

    @BeforeAll @SuppressWarnings("Duplicates")
    static final void setupApplication() throws Exception {
        Settings.loadSettings(ApplicationMode.ADMIN);
        originalPath = Settings.getServerDocumentsPath();
        Settings.setServerPath(TestUtil.getTestDocuments());

        TestUtil.resetTestFiles();
        ApplicationTest.launch(DMSApplication.class, ApplicationMode.ADMIN.toString());
    }

    @AfterAll
    static void onTestEnd(){
        // Reset path in settings
        Settings.setServerPath(originalPath);
    }
}
