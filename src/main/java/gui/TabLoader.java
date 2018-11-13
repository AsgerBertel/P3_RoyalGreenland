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
    LOG("LOG.fxml"),
    DELETED_FILES("DeletedFiles.fxml");

    private String fxmlFileName;
    private Pane node;
    private TabController tabController;
    private Locale lang;

    TabLoader(String fxmlFileName){
        this.fxmlFileName = fxmlFileName;
        lang = DMSApplication.locale;
    }

    public Pane getPane() throws IOException {
        if(node == null || lang != DMSApplication.locale){
            ResourceBundle bundle = ResourceBundle.getBundle("Messages", DMSApplication.locale);
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(DMSApplication.fxmlPath + fxmlFileName), bundle);
            node = fxmlLoader.load();
            tabController = fxmlLoader.getController();
        }

        tabController.update();
        return node;
    }
}
