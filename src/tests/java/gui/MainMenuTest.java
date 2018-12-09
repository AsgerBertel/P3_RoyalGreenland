package gui;

import javafx.scene.control.ToggleButton;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MainMenuTest extends GUITest {


    @Test
    void switchLanguageTest() throws InterruptedException {
        assertEquals(DMSApplication.getLanguage(), DMSApplication.DK_LOCALE);
        clickOn((ToggleButton) findNode("#greenlandicButton"));
        Thread.sleep(500);
        assertEquals(DMSApplication.getLanguage(), DMSApplication.GL_LOCALE);
        clickOn((ToggleButton) findNode("#danishButton"));
        Thread.sleep(500);
        assertEquals(DMSApplication.getLanguage(), DMSApplication.DK_LOCALE);
    }

    @Test
   void switchTabTest() throws InterruptedException {
        for(int i = 0; i < 3; i++){
            clickOn((ToggleButton)findNode("#administrateDocumentsButton"));
            assertEquals(dmsApplication.getCurrentTab(), Tab.FILE_ADMINISTRATION);

            clickOn((ToggleButton)findNode("#viewDocumentsButton"));
            assertEquals(dmsApplication.getCurrentTab(), Tab.FILE_OVERVIEW);

            clickOn((ToggleButton)findNode("#administratePlantsButton"));
            assertEquals(dmsApplication.getCurrentTab(), Tab.PLANT_ADMINISTRATION);

            clickOn((ToggleButton)findNode("#deletedFilesButton"));
            assertEquals(dmsApplication.getCurrentTab(), Tab.DELETED_FILES);

            clickOn((ToggleButton)findNode("#logButton"));
            assertEquals(dmsApplication.getCurrentTab(), Tab.LOG);

            clickOn((ToggleButton)findNode("#settingsButton"));
            assertEquals(dmsApplication.getCurrentTab(), Tab.SETTINGS);
        }
   }


}
