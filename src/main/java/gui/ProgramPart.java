package gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

import java.io.IOException;

public enum ProgramPart {

    FILE_OVERVIEW("FileOverview.fxml"),
    FILE_ADMINISTRATION("FileAdministration.fxml"),
    PLANT_ADMINISTRATION("PlantAdministration.fxml"),
    LOG("LOG.fxml"),
    DELETED_FILES("DeletedFiles.fxml");

    private String fxmlFileName;
    private Pane node;

    ProgramPart(String fxmlFileName){
        this.fxmlFileName = fxmlFileName;
    }

    public Pane getPane() throws IOException {
        if(node == null)
            node = FXMLLoader.load(getClass().getResource(DMSApplication.fxmlPath + fxmlFileName));

        return node;
    }

}
