package app;

import gui.DMSApplication;
import javafx.stage.Stage;

public class DMSViewer {

    public static void main(String[] args) {
        DMSApplication.launch(DMSApplication.class, ApplicationMode.VIEWER.toString());
    }

}
