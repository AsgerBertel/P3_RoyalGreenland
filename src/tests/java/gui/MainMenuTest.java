package gui;

import javafx.scene.control.ToggleButton;
import org.junit.jupiter.api.Test;
public class MainMenuTest extends GUITest {

   @Test
   void switchTabTest() throws InterruptedException {
       //clickOn((ToggleButton)findNode("#viewDocumentsButton"));

       clickOn((ToggleButton)findNode("#viewDocumentsButton"));
       clickOn((ToggleButton)findNode("#administrateDocumentsButton"));
       clickOn((ToggleButton)findNode("#deletedFilesButton"));
       clickOn((ToggleButton)findNode("#viewDocumentsButton"));
       Thread.sleep(1000);
   }


}
