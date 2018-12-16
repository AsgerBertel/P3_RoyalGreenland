package gui;
import app.DMSApplication;
import javafx.scene.control.ToggleButton;
import org.junit.jupiter.api.RepeatedTest;

import static org.junit.jupiter.api.Assertions.*;

class MainMenuTest extends GUITest {

    @RepeatedTest(value = 2)
    void switchLanguageTest() throws InterruptedException {
        if (DMSApplication.getLanguage().equals(DMSApplication.GL_LOCALE)) {
            clickOn((ToggleButton) findNode("#danishButton"));
            Thread.sleep(300);
        }

        assertEquals(DMSApplication.getLanguage(), DMSApplication.DK_LOCALE);
        clickOn((ToggleButton) findNode("#greenlandicButton"));
        Thread.sleep(300);
        assertEquals(DMSApplication.GL_LOCALE, DMSApplication.getLanguage());
        clickOn((ToggleButton) findNode("#danishButton"));
        Thread.sleep(300);
        assertEquals(DMSApplication.getLanguage(), DMSApplication.DK_LOCALE);
    }

    @RepeatedTest(value = 2)
    void switchTabTest() {
        for (int i = 0; i < 2; i++) {
            clickOn((ToggleButton) findNode("#administrateDocumentsButton"));
            assertEquals(dmsApplication.getCurrentTab(), Tab.FILE_ADMINISTRATION);

            clickOn((ToggleButton) findNode("#viewDocumentsButton"));
            assertEquals(dmsApplication.getCurrentTab(), Tab.FILE_OVERVIEW);

            clickOn((ToggleButton) findNode("#administratePlantsButton"));
            assertEquals(dmsApplication.getCurrentTab(), Tab.PLANT_ADMINISTRATION);

            clickOn((ToggleButton) findNode("#archiveButton"));
            assertEquals(dmsApplication.getCurrentTab(), Tab.ARCHIVE);

            clickOn((ToggleButton) findNode("#logButton"));
            assertEquals(dmsApplication.getCurrentTab(), Tab.LOG);

            clickOn((ToggleButton) findNode("#settingsButton"));
            assertTrue(dmsApplication.getCurrentTab() == Tab.SETTINGS_ADMIN || dmsApplication.getCurrentTab() == Tab.SETTINGS);
        }
    }




}
