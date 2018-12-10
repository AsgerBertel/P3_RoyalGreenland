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

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileTester {

    private static Path originalPath;

    @BeforeAll @SuppressWarnings("Duplicates")
    static final void setupApplication() throws Exception {
        Settings.loadSettings(ApplicationMode.ADMIN);
        originalPath = Settings.getServerPath();
        Settings.setServerPath(TestUtil.getTestDocuments());
    }

    @BeforeEach
    void resetBeforeEachMethod() throws IOException, InterruptedException {
        TestUtil.resetTestFiles();
        FileManager.resetInstance();

        AppFilesManager.createServerDirectories();
        AppFilesManager.createLocalDirectories();
        setSettings();
    }

    void setSettings(){

    }

    @AfterAll
    static void onTestEnd(){
        // Reset path in settings
        Settings.setServerPath(originalPath);
    }

    protected static AbstractFile findInMainFiles(Path path){
        if (path.toString().startsWith(File.separator)){
            path = Paths.get(path.toString().substring(1));
        }
        return FileManager.getInstance().findFile(path, FileManager.getInstance().getMainFiles()).get();
    }
}
