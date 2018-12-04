package gui.menu;

import directory.Settings;
import gui.DMSApplication;
import gui.TabLoader;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;

import java.util.Locale;

public class MainMenuController {

    DMSApplication dmsApplication;

    @FXML
    ToggleButton viewDocuments;
    @FXML
    ToggleButton administrateDocuments;
    @FXML
    ToggleButton administratePlants;
    @FXML
    ToggleButton deletedFiles;
    @FXML
    ToggleButton log;
    @FXML
    ToggleButton settings;

    @FXML
    ToggleButton changeToDanish;
    @FXML
    ToggleButton changeToGreenlandic;

    ToggleGroup menuTG = new ToggleGroup();
    ToggleGroup languageTG = new ToggleGroup();

    public void init(DMSApplication dmsApplication){
        viewDocuments.setToggleGroup(menuTG);
        settings.setToggleGroup(menuTG);

        // Only adds these tabs if it's DMSAdmin
        if(administrateDocuments != null) {
            administrateDocuments.setToggleGroup(menuTG);
            administratePlants.setToggleGroup(menuTG);
            deletedFiles.setToggleGroup(menuTG);
            log.setToggleGroup(menuTG);
        }

        // Highlights tab that's open from the start
        if (administrateDocuments != null)
            administrateDocuments.setSelected(true);
        else
            viewDocuments.setSelected(true);


        changeToDanish.setToggleGroup(languageTG);
        changeToGreenlandic.setToggleGroup(languageTG);

        this.dmsApplication = dmsApplication;

        if (Settings.getLanguage().equals(DMSApplication.DK_LOCALE)){
            changeToDanish.setSelected(true);

        } else {
            changeToGreenlandic.setSelected(true);
        }
    }

    public void administrateDocuments(ActionEvent actionEvent) {
        dmsApplication.switchWindow(TabLoader.FILE_ADMINISTRATION);
        administrateDocuments.setSelected(true);
    }

    public void viewDocuments(ActionEvent actionEvent) {
        dmsApplication.switchWindow(TabLoader.FILE_OVERVIEW);
        viewDocuments.setSelected(true);
    }

    public void administratePlants(ActionEvent actionEvent) {
        dmsApplication.switchWindow(TabLoader.PLANT_ADMINISTRATION);
        administratePlants.setSelected(true);
    }

    public void deletedFiles(ActionEvent actionEvent) {
        dmsApplication.switchWindow(TabLoader.DELETED_FILES);
        deletedFiles.setSelected(true);
    }

    public void log(ActionEvent actionEvent) {
        dmsApplication.switchWindow(TabLoader.LOG);
        log.setSelected(true);
    }

    public void settings(ActionEvent actionEvent) {
        dmsApplication.switchWindow(TabLoader.SETTINGS);
        settings.setSelected(true);
    }

    public void changeToDanish(Event actionEvent) throws Exception{
        changeToDanish.setSelected(true);
        if (!Settings.getLanguage().equals(DMSApplication.DK_LOCALE)){
            dmsApplication.changeLanguage(DMSApplication.DK_LOCALE);
            dmsApplication.restartApp();
        }

    }

    public void changeToGreenlandic(ActionEvent actionEvent) throws Exception{
        changeToGreenlandic.setSelected(true);
        if (!Settings.getLanguage().equals(DMSApplication.GL_LOCALE)){
            dmsApplication.changeLanguage(DMSApplication.GL_LOCALE);
            dmsApplication.restartApp();
        }


    }


}
