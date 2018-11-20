package app;

import gui.DMSApplication;

import java.io.File;

public class DMSAdmin {


    public static void main(String[] args) {
        DMSApplication.launch(DMSApplication.class, ApplicationMode.ADMIN.toString());

    }

}
