package directory;

import app.ApplicationMode;
import directory.files.AbstractFile;
import directory.files.Folder;
import directory.plant.PlantManager;
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
    }

    @BeforeEach
    void resetBeforeEachMethod() throws IOException {
        SettingsManager.setServerPath(TestUtil.getTestServerDocuments());
        SettingsManager.setLocalPath(TestUtil.getTestLocalDocuments());

        TestUtil.resetTestFiles();
        FileManager.resetInstance();
        PlantManager.resetInstance();

        AppFilesManager.createServerDirectories();
        AppFilesManager.createLocalDirectories();
        setSettings();
    }

    protected void setSettings(){

    }

    @AfterAll
    static void onTestEnd(){
        // Reset path in settings
        SettingsManager.setServerPath(originalServerPath);
        SettingsManager.setLocalPath(originalLocalPath);
    }

    public static AbstractFile findInMainFiles(Path path){
        if (path.toString().startsWith(File.separator)){
            path = Paths.get(path.toString().substring(1));
        }
        return FileManager.getInstance().findFile(path, FileManager.getInstance().getMainFiles()).get();
    }
}
