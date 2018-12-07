package gui;

import javafx.scene.control.ToggleButton;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MainMenuTest extends GUITest {

   @RepeatedTest(value = 2)
   void switchTabTest() throws InterruptedException {

       clickOn((ToggleButton)findNode("#administrateDocumentsButton"));
       assertEquals(dmsApplication.getCurrentTab(), Tab.FILE_ADMINISTRATION);

       clickOn((ToggleButton)findNode("#viewDocumentsButton"));
       assertEquals(dmsApplication.getCurrentTab(), Tab.FILE_OVERVIEW);

       clickOn((ToggleButton)findNode("#administratePlantsButton"));
       assertEquals(dmsApplication.getCurrentTab(), Tab.PLANT_ADMINISTRATION);

       clickOn((ToggleButton)findNode("#deletedFilesButton"));
       assertEquals(dmsApplication.getCurrentTab(), Tab.DELETED_FILES);

       clickOn((ToggleButton)findNode("#settingsButton"));
       assertEquals(dmsApplication.getCurrentTab(), Tab.SETTINGS);

   }


}
