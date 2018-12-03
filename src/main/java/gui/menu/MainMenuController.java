package gui.menu;

import directory.Settings;
import gui.DMSApplication;
import gui.TabLoader;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;

public class MainMenuController {

    DMSApplication dmsApplication;

    @FXML
    ToggleButton viewDocumentsButton;
    @FXML
    ToggleButton administrateDocumentsButton;
    @FXML
    ToggleButton administratePlantsButton;
    @FXML
    ToggleButton deletedFilesButton;
    @FXML
    ToggleButton logButton;
    @FXML
    ToggleButton settingsButton;

    @FXML
    ToggleButton danishButton;
    @FXML
    ToggleButton greenlandicButton;

    ToggleGroup menuTG = new ToggleGroup();
    ToggleGroup languageTG = new ToggleGroup();

    public void init(DMSApplication dmsApplication){
        viewDocumentsButton.setToggleGroup(menuTG);
        settingsButton.setToggleGroup(menuTG);

        // Only adds these tabs if it's DMSAdmin
        if(administrateDocumentsButton != null) {
            administrateDocumentsButton.setToggleGroup(menuTG);
            administratePlantsButton.setToggleGroup(menuTG);
            deletedFilesButton.setToggleGroup(menuTG);
            logButton.setToggleGroup(menuTG);
        }

        // Highlights tab that's open from the start
        if (administrateDocumentsButton != null)
            administrateDocumentsButton.setSelected(true);
        else
            viewDocumentsButton.setSelected(true);


        danishButton.setToggleGroup(languageTG);
        greenlandicButton.setToggleGroup(languageTG);

        this.dmsApplication = dmsApplication;
        //todo set selected language
        if (Settings.getLanguage().equals(DMSApplication.DK_LOCALE)){
            danishButton.setSelected(true);

        } else {
            greenlandicButton.setSelected(true);
        }
    }

    public void administrateDocuments(ActionEvent actionEvent) {
        dmsApplication.switchWindow(TabLoader.FILE_ADMINISTRATION);
        administrateDocumentsButton.setSelected(true);
    }

    public void viewDocuments(ActionEvent actionEvent) {
        dmsApplication.switchWindow(TabLoader.FILE_OVERVIEW);
        viewDocumentsButton.setSelected(true);
    }

    public void administratePlants(ActionEvent actionEvent) {
        dmsApplication.switchWindow(TabLoader.PLANT_ADMINISTRATION);
        administratePlantsButton.setSelected(true);
    }

    public void deletedFiles(ActionEvent actionEvent) {
        dmsApplication.switchWindow(TabLoader.DELETED_FILES);
        deletedFilesButton.setSelected(true);
    }

    public void log(ActionEvent actionEvent) {
        dmsApplication.switchWindow(TabLoader.LOG);
        logButton.setSelected(true);
    }

    public void settings(ActionEvent actionEvent) {
        dmsApplication.switchWindow(TabLoader.SETTINGS);
        settingsButton.setSelected(true);
    }

    public void changeToDanish(Event actionEvent) throws Exception{
        danishButton.setSelected(true);
        if (!Settings.getLanguage().equals(DMSApplication.DK_LOCALE)){
            dmsApplication.changeLanguage(DMSApplication.DK_LOCALE);
            dmsApplication.restartApp();
        }

    }

    public void changeToGreenlandic(ActionEvent actionEvent) throws Exception{
        greenlandicButton.setSelected(true);
        if (!Settings.getLanguage().equals(DMSApplication.GL_LOCALE)){
            dmsApplication.changeLanguage(DMSApplication.GL_LOCALE);
            dmsApplication.restartApp();
        }


    }


}
