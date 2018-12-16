package gui;

import app.ApplicationMode;
import app.DMSApplication;
import model.managing.SettingsManager;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import util.TestUtil;

import java.util.concurrent.TimeoutException;

public abstract class GUITest extends ApplicationTest {

    DMSApplication dmsApplication;

    @Override
    public void start(Stage stage) {
        stage.show();
        this.dmsApplication = DMSApplication.getDMSApplication();
    }



    @AfterEach
    void tearDown() throws TimeoutException, InterruptedException {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
        Platform.runLater(() -> switchLanguageSetting());
        Thread.sleep(300);
    }

    @BeforeAll @SuppressWarnings("Duplicates")
    static void setupApplication() throws Exception {
        if (Boolean.getBoolean("headless")) {
            System.out.println("Headless mode");
            System.setProperty("testfx.robot", "glass");
            System.setProperty("testfx.headless", "true");
            System.setProperty("prism.order", "sw");
            System.setProperty("prism.text", "t2k");
            System.setProperty("java.awt.headless", "true");
        }

        SettingsManager.setServerPath(TestUtil.getTestServerDocuments());
        SettingsManager.setLanguage(DMSApplication.DK_LOCALE);

        SettingsManager.loadSettings(ApplicationMode.ADMIN);

        ApplicationTest.launch(DMSApplication.class, ApplicationMode.ADMIN.toString());
    }

    private void switchLanguageSetting()  {
        if(DMSApplication.getLanguage().equals(DMSApplication.DK_LOCALE)){
            dmsApplication.changeLanguage(DMSApplication.GL_LOCALE);
        }else{
            dmsApplication.changeLanguage(DMSApplication.DK_LOCALE);
        }
    }

    @SuppressWarnings("unchecked") // Find a node by its' fx:id
     <T extends Node> T findNode(final String fxID) {
        return (T) lookup(fxID).queryAll().iterator().next();
    }

    void selectAllAndDelete(){
        if(System.getProperty("os.name").contains("Mac")){
            press(KeyCode.COMMAND);
        } else{
            press(KeyCode.CONTROL);
        }
        press(KeyCode.A);
        release(new KeyCode[]{});
        push(KeyCode.DELETE);
    }

    void clickOnContextMenuItem(int index){
        moveBy(48, 18);
        moveBy(0, 27 * index);
        clickOn(MouseButton.PRIMARY);
    }




}
