package gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public enum TabLoader {

    FILE_OVERVIEW("FileOverview.fxml"),
    FILE_ADMINISTRATION("FileAdministration.fxml"),
    PLANT_ADMINISTRATION("PlantAdministration.fxml"),
    LOG("Log.fxml"),
    DELETED_FILES("DeletedFiles.fxml"),
    SETTINGS("Settings.fxml");

    private String fxmlFileName;
    private Pane node;
    private TabController tabController;
    private Locale lang;

    TabLoader(String fxmlFileName){
        this.fxmlFileName = fxmlFileName;
        lang = DMSApplication.getLanguage();
    }

    public Pane getPane() throws IOException {
        if(node == null || lang != DMSApplication.getLanguage()){
            ResourceBundle bundle = ResourceBundle.getBundle("Messages", DMSApplication.getLanguage());
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(DMSApplication.fxmlPath + fxmlFileName), bundle);
            node = fxmlLoader.load();
            tabController = fxmlLoader.getController();
        }

        tabController.update();
        return node;
    }
}
