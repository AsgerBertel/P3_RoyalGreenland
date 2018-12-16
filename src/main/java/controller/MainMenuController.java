package controller;

import app.ApplicationMode;
import model.managing.SettingsManager;
import app.DMSApplication;
import gui.Tab;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;

public class MainMenuController {

    private DMSApplication dmsApplication;

    @FXML
    ToggleButton viewDocumentsButton;
    @FXML
    ToggleButton administrateDocumentsButton;
    @FXML
    ToggleButton administratePlantsButton;
    @FXML
    ToggleButton archiveButton;
    @FXML
    ToggleButton logButton;
    @FXML
    ToggleButton settingsButton;

    @FXML
    ToggleButton danishButton;
    @FXML
    ToggleButton greenlandicButton;

    private final ToggleGroup menuTG = new ToggleGroup();
    private final ToggleGroup languageTG = new ToggleGroup();

    public void init(DMSApplication dmsApplication){
        viewDocumentsButton.setToggleGroup(menuTG);
        settingsButton.setToggleGroup(menuTG);

        // Only adds these tabs if it's DMSAdmin
        if(administrateDocumentsButton != null) {
            administrateDocumentsButton.setToggleGroup(menuTG);
            administratePlantsButton.setToggleGroup(menuTG);
            archiveButton.setToggleGroup(menuTG);
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

        if (SettingsManager.getLanguage().equals(DMSApplication.DK_LOCALE)){
            danishButton.setSelected(true);

        } else {
            greenlandicButton.setSelected(true);
        }
    }

    public void administrateDocuments(ActionEvent actionEvent) {
        dmsApplication.switchTab(Tab.FILE_ADMINISTRATION);
        administrateDocumentsButton.setSelected(true);
    }

    public void viewDocuments(ActionEvent actionEvent) {
        dmsApplication.switchTab(Tab.FILE_OVERVIEW);
        viewDocumentsButton.setSelected(true);
    }

    public void administratePlants(ActionEvent actionEvent) {
        dmsApplication.switchTab(Tab.PLANT_ADMINISTRATION);
        administratePlantsButton.setSelected(true);
    }

    public void archive(ActionEvent actionEvent) {
        dmsApplication.switchTab(Tab.ARCHIVE);
        archiveButton.setSelected(true);
    }

    public void log(ActionEvent actionEvent) {
        dmsApplication.switchTab(Tab.LOG);
        logButton.setSelected(true);
    }

    public void settings(ActionEvent actionEvent) {
        if(DMSApplication.getApplicationMode() == ApplicationMode.ADMIN){
            dmsApplication.switchTab(Tab.SETTINGS_ADMIN);
        } else {
            dmsApplication.switchTab(Tab.SETTINGS);
        }
        settingsButton.setSelected(true);
    }

    public void danishButton(Event actionEvent) {
        danishButton.setSelected(true);
        if (!SettingsManager.getLanguage().equals(DMSApplication.DK_LOCALE)){
            dmsApplication.changeLanguage(DMSApplication.DK_LOCALE);
        }

    }

    public void greenlandicButton(ActionEvent actionEvent) {
        greenlandicButton.setSelected(true);
        if (!SettingsManager.getLanguage().equals(DMSApplication.GL_LOCALE)){
            dmsApplication.changeLanguage(DMSApplication.GL_LOCALE);
        }


    }


}
