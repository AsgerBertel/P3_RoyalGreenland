package gui;

import app.ApplicationMode;
import directory.SettingsManager;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import util.TestUtil;

import java.nio.file.Path;
import java.util.concurrent.TimeoutException;

public abstract class GUITest extends ApplicationTest {

    private static Path originalPath;
    protected DMSApplication dmsApplication;

    @Override
    public void start(Stage stage) throws Exception {
        stage.show();
        this.dmsApplication = DMSApplication.getDMSApplication();
    }

    @AfterEach
    void tearDown() throws TimeoutException, InterruptedException {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
        Platform.runLater(() -> switchLanguageSetting());
        Thread.sleep(200);
    }

    @BeforeAll @SuppressWarnings("Duplicates")
    static void setupApplication() throws Exception {
        SettingsManager.loadSettings(ApplicationMode.ADMIN);

        originalPath = SettingsManager.getServerPath();
        SettingsManager.setServerPath(TestUtil.getTestDocuments());
        SettingsManager.setLanguage(DMSApplication.DK_LOCALE);

        ApplicationTest.launch(DMSApplication.class, ApplicationMode.ADMIN.toString());

    }

    @BeforeEach
    final void setup() throws InterruptedException {

    }



    @AfterAll
    static void cleanUp(){
        // Reset path in settings
        //SettingsManager.setServerPath(originalPath);
    }

    void switchLanguageSetting()  {
        if(DMSApplication.getLanguage().equals(DMSApplication.DK_LOCALE)){
            dmsApplication.changeLanguage(DMSApplication.GL_LOCALE);
        }else{
            dmsApplication.changeLanguage(DMSApplication.DK_LOCALE);
        }
    }


    // Find a node by its' fx:id
    protected  <T extends Node> T findNode(final String fxID) {
        return (T) lookup(fxID).queryAll().iterator().next();
    }

    protected void selectAllAndDelete(){
        if(System.getProperty("os.name").contains("Mac")){
            press(KeyCode.COMMAND);
        } else{
            press(KeyCode.CONTROL);
        }
        press(KeyCode.A);
        release(new KeyCode[]{});
        push(KeyCode.DELETE);
    }




}
