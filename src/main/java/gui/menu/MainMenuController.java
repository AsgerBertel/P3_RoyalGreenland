package gui.menu;

import gui.DMSApplication;
import gui.ProgramPart;
import javafx.event.ActionEvent;
import javafx.event.Event;

import java.io.IOException;
import java.util.Locale;

public class MainMenuController {

    DMSApplication dmsApplication;

    public void init(DMSApplication dmsApplication){
        this.dmsApplication = dmsApplication;
    }

    public void administrateDocuments(ActionEvent actionEvent) {
        dmsApplication.switchWindow(ProgramPart.FILE_ADMINISTRATION);
    }

    public void viewDocuments(ActionEvent actionEvent) {
        dmsApplication.switchWindow(ProgramPart.FILE_OVERVIEW);
    }

    public void administratePlants(ActionEvent actionEvent) {
        dmsApplication.switchWindow(ProgramPart.PLANT_ADMINISTRATION);
    }

    public void deletedFiles(ActionEvent actionEvent) {
        dmsApplication.switchWindow(ProgramPart.DELETED_FILES);
    }

    public void log(ActionEvent actionEvent) {
        dmsApplication.switchWindow(ProgramPart.LOG);
    }

    public void changeToDanish(Event actionEvent) throws Exception{
        DMSApplication.setLocale(new Locale("da", "DK"));
        DMSApplication.restartApp();
    }

    public void changeToGreenlandic(ActionEvent actionEvent) throws Exception{
        DMSApplication.setLocale(new Locale("kl", "GL"));
        DMSApplication.restartApp();
    }
}