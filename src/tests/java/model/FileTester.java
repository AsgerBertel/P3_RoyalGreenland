package model;

import model.AbstractFile;
import model.managing.FileManager;
import model.managing.SettingsManager;
import org.junit.jupiter.api.BeforeEach;
import util.TestUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileTester {

    private static Path originalServerPath;
    private static Path originalLocalPath;

    @BeforeEach
    void resetBeforeEachMethod() throws IOException {
        SettingsManager.setServerPath(TestUtil.getTestServerDocuments());
        SettingsManager.setLocalPath(TestUtil.getTestLocalDocuments());

        TestUtil.resetTestFiles();

        setSettings();
    }

    protected void setSettings(){

    }


    protected static AbstractFile findInMainFiles(Path path){
        if (path.toString().startsWith(File.separator)){
            path = Paths.get(path.toString().substring(1));
        }
        return FileManager.getInstance().findFile(path, FileManager.getInstance().getMainFiles()).get();
    }
}