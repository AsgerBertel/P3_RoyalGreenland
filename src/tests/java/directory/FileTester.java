package directory;

import app.ApplicationMode;
import directory.files.AbstractFile;
import json.AppFilesManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import util.TestUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileTester {

    private static Path originalServerPath;
    private static Path originalLocalPath;

    @BeforeAll @SuppressWarnings("Duplicates")
    static final void setupApplication() {
        SettingsManager.loadSettings(ApplicationMode.ADMIN);
        originalServerPath = SettingsManager.getServerPath();
        originalLocalPath = SettingsManager.getLocalPath();
        SettingsManager.setServerPath(TestUtil.getTestServerDocuments());
        SettingsManager.setLocalPath(TestUtil.getTestLocalDocuments());
    }

    @BeforeEach
    void resetBeforeEachMethod() throws IOException {
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
        SettingsManager.setServerPath(originalServerPath);
        SettingsManager.setLocalPath(originalLocalPath);
    }

    protected static AbstractFile findInMainFiles(Path path){
        if (path.toString().startsWith(File.separator)){
            path = Paths.get(path.toString().substring(1));
        }
        return FileManager.getInstance().findFile(path, FileManager.getInstance().getMainFiles()).get();
    }
}
