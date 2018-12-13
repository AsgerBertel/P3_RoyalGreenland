package gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public enum Tab {

    FILE_OVERVIEW("FileOverview.fxml"),
    FILE_ADMINISTRATION("FileAdministration.fxml"),
    PLANT_ADMINISTRATION("PlantAdministration.fxml"),
    LOG("Log.fxml"),
    DELETED_FILES("DeletedFiles.fxml"),
    SETTINGS_ADMIN("SettingsAdmin.fxml"),
    SETTINGS("Settings.fxml");

    private String fxmlFileName;
    private Pane node;
    private TabController tabController;
    private Locale lang;

    Tab(String fxmlFileName){
        this.fxmlFileName = fxmlFileName;
        lang = DMSApplication.getLanguage();
    }

    public Pane getPane(DMSApplication dmsApplication, Locale lang) throws IOException {
        if(node == null || this.lang != lang){
            this.lang = lang;
            ResourceBundle bundle = ResourceBundle.getBundle("Messages", DMSApplication.getLanguage());
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(DMSApplication.fxmlPath + fxmlFileName), bundle);
            node = fxmlLoader.load();
            tabController = fxmlLoader.getController();
            tabController.initReference(dmsApplication);
        }

        tabController.update();
        return node;
    }

    public void update(){tabController.update();}

    public TabController getTabController(){
        return tabController;
    }
}
