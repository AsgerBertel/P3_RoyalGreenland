package directory;

import app.ApplicationMode;
import directory.files.AbstractFile;
import json.AppFilesManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import util.TestUtil;

import java.io.IOException;
import java.nio.file.Path;

public class FileTester {

    private static Path originalPath;

    @BeforeAll @SuppressWarnings("Duplicates")
    static final void setupApplication() throws Exception {
        TestUtil.resetTestFiles();

        SettingsManager.loadSettings(ApplicationMode.ADMIN);
        originalPath = SettingsManager.getServerPath();
        SettingsManager.setServerPath(TestUtil.getTestDocuments());

        AppFilesManager.createServerDirectories();
        AppFilesManager.createLocalDirectories();
    }

    @BeforeEach
    void resetBeforeEachMethod() throws IOException {
        TestUtil.resetTestFiles();

        AppFilesManager.createServerDirectories();
        AppFilesManager.createLocalDirectories();
    }

    @AfterAll
    static void onTestEnd(){
        // Reset path in settings
        SettingsManager.setServerPath(originalPath);
    }

    protected AbstractFile findInMainFiles(Path path){
        return FileManager.getInstance().findFile(path, FileManager.getInstance().getMainFiles()).get();
    }
}
