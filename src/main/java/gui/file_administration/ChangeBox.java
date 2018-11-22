package gui.file_administration;

import gui.log.LogEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class ChangeBox extends VBox {

    private LogEvent event;

    private Text text;
    private Pane splitter;

    private static final int TEXT_WIDTH = 175;
    private static final int BOX_WIDTH = 190;

    public ChangeBox(LogEvent event) {
        this.event = event;
        this.setMinWidth(BOX_WIDTH);
        this.setMaxWidth(BOX_WIDTH);

        getStyleClass().add("changesVBox");

        text = new Text();
        text.setWrappingWidth(TEXT_WIDTH);
        text.setText(event.getEventString());
        text.getStyleClass().add("changeText");
        //this.getStylesheets().add("/styles/fileAdministration.css");
        splitter = new Pane();
        splitter.setMinWidth(BOX_WIDTH);
        splitter.setMaxWidth(BOX_WIDTH);
        splitter.getStyleClass().add("changesSplitter");
        getChildren().add(splitter);
        getChildren().add(text);
    }
}

