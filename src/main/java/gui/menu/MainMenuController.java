package gui.menu;

import directory.Settings;
import gui.DMSApplication;
import gui.Tab;
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

        if (Settings.getLanguage().equals(DMSApplication.DK_LOCALE)){
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

    public void deletedFiles(ActionEvent actionEvent) {
        dmsApplication.switchTab(Tab.DELETED_FILES);
        deletedFilesButton.setSelected(true);
    }

    public void log(ActionEvent actionEvent) {
        dmsApplication.switchTab(Tab.LOG);
        logButton.setSelected(true);
    }

    public void settings(ActionEvent actionEvent) {
        dmsApplication.switchTab(Tab.SETTINGS);
        settingsButton.setSelected(true);
    }

    public void danishButton(Event actionEvent) throws Exception{
        danishButton.setSelected(true);
        if (!Settings.getLanguage().equals(DMSApplication.DK_LOCALE)){
            dmsApplication.changeLanguage(DMSApplication.DK_LOCALE);
        }

    }

    public void greenlandicButton(ActionEvent actionEvent) throws Exception{
        greenlandicButton.setSelected(true);
        if (!Settings.getLanguage().equals(DMSApplication.GL_LOCALE)){
            dmsApplication.changeLanguage(DMSApplication.GL_LOCALE);
        }


    }


}
