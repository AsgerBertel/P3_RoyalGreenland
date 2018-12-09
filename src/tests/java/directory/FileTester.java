package directory;

import app.ApplicationMode;
import directory.files.AbstractFile;
import gui.DMSApplication;
import json.AppFilesManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.testfx.framework.junit5.ApplicationTest;
import util.TestUtil;

import java.io.IOException;
import java.nio.file.Path;

public class FileTester {

    private static Path originalPath;

    @BeforeAll @SuppressWarnings("Duplicates")
    static final void setupApplication() throws Exception {
        TestUtil.resetTestFiles();

        Settings.loadSettings(ApplicationMode.ADMIN);
        originalPath = Settings.getServerPath();
        Settings.setServerPath(TestUtil.getTestDocuments());


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
        Settings.setServerPath(originalPath);
    }

    protected AbstractFile findInMainFiles(Path path){
        return FileManager.getInstance().findFile(path, FileManager.getInstance().getMainFiles()).get();
    }
}
