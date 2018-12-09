package gui;

import javafx.scene.control.ToggleButton;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PlantAdminTabTest extends GUITest {

    @BeforeEach
    void setTab() throws InterruptedException {
        clickOn((ToggleButton)findNode("#plantAdminButton"));
    }

    @Test
    void createEditSwitchTest() {



    }
}
