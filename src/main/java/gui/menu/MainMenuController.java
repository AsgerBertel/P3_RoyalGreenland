package gui.menu;

import gui.DMSApplication;
import gui.TabController;
import gui.TabLoader;
import javafx.event.ActionEvent;
import javafx.event.Event;

import java.util.Locale;

public class MainMenuController {

    DMSApplication dmsApplication;

    public void init(DMSApplication dmsApplication){
        this.dmsApplication = dmsApplication;
    }

    public void administrateDocuments(ActionEvent actionEvent) {
        dmsApplication.switchWindow(TabLoader.FILE_ADMINISTRATION);
    }

    public void viewDocuments(ActionEvent actionEvent) {
        dmsApplication.switchWindow(TabLoader.FILE_OVERVIEW);
    }

    public void administratePlants(ActionEvent actionEvent) {
        dmsApplication.switchWindow(TabLoader.PLANT_ADMINISTRATION);
    }

    public void deletedFiles(ActionEvent actionEvent) {
        dmsApplication.switchWindow(TabLoader.DELETED_FILES);
    }

    public void log(ActionEvent actionEvent) {
        dmsApplication.switchWindow(TabLoader.LOG);
    }

    public void settings(ActionEvent actionEvent) {
        dmsApplication.switchWindow(TabLoader.SETTINGS);
    }

    public void changeToDanish(Event actionEvent) throws Exception{
        dmsApplication.changeLanguage(new Locale("da", "DK"));
        dmsApplication.restartApp();

    }

    public void changeToGreenlandic(ActionEvent actionEvent) throws Exception{
        dmsApplication.changeLanguage(new Locale("kl", "GL"));
        dmsApplication.restartApp();
    }


}
