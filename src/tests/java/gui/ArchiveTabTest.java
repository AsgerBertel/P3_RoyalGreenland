package gui;

import directory.FileManager;
import directory.SettingsManager;
import directory.plant.PlantManager;
import gui.deleted_files.ArchiveController;
import gui.file_administration.FileAdminController;
import javafx.scene.control.ToggleButton;
import json.AppFilesManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import util.TestUtil;

import java.io.IOException;

public class ArchiveTabTest extends GUITest {

    private ArchiveController fileController;

    @BeforeEach
    void setup() throws IOException, InterruptedException {
        clickOn((ToggleButton)findNode("#archiveButton"));
        fileController = (ArchiveController) dmsApplication.getCurrentTab().getTabController();
        resetFiles();
    }

    private void resetFiles() throws IOException, InterruptedException {
        SettingsManager.setServerPath(TestUtil.getTestServerDocuments());
        SettingsManager.setLocalPath(TestUtil.getTestLocalDocuments());

        TestUtil.resetTestFiles();
        FileManager.resetInstance();
        PlantManager.resetInstance();

        AppFilesManager.createServerDirectories();
        AppFilesManager.createLocalDirectories();
    }

    @Test
    void testRestore(){
        clickOn((ToggleButton)findNode("#administrateDocumentsButton"));

    }
}
