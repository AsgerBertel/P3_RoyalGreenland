package gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

import java.io.IOException;

public enum TabLoader {

    FILE_OVERVIEW("FileOverview.fxml"),
    FILE_ADMINISTRATION("FileAdministration.fxml"),
    PLANT_ADMINISTRATION("PlantAdministration.fxml"),
    LOG("LOG.fxml"),
    DELETED_FILES("DeletedFiles.fxml");

    private String fxmlFileName;
    private Pane node;
    private TabController tabController;

    TabLoader(String fxmlFileName){
        this.fxmlFileName = fxmlFileName;
    }

    public Pane getPane() throws IOException {
        if(node == null){
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(DMSApplication.fxmlPath + fxmlFileName));
            node = fxmlLoader.load();
            tabController = fxmlLoader.getController();
        }

        tabController.update();
        return node;
    }

}
