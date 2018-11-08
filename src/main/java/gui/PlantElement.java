package gui;

import directory.plant.Plant;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.function.Consumer;

public class PlantElement extends BorderPane {

    private Plant plant;
    private Text text;
    protected Runnable onSelected;

    public PlantElement(Plant plant){
        this.plant = plant;
        getStyleClass().add("plantElement");
        text = new Text();
        updateText();
        this.getStyleClass().add("plantElement");

        text = new Text(plant.getId() + " - " + plant.getName());
        text.getStyleClass().add("plantText");
        text.setTranslateX(6);

        // Add container that anchors the text to the left-center of the box
        HBox container = getCenteredContainer();
        container.getChildren().add(text);
        setLeft(container);

        setEventHandler(MouseEvent.MOUSE_PRESSED, event -> onClick(event));
    }

    public void setOnSelectedListener(Runnable onSelected){
        this.onSelected = onSelected;
    }

    // Called when the element is clicked
    protected void onClick(MouseEvent event){
        setFocused(true);

        // Run listener method if initialized
        if(onSelected != null)
            onSelected.run();
    }

    public Plant getPlant(){
        return plant;
    }

    // Creates a simple container that centers its' children
    protected HBox getCenteredContainer(){
        HBox container = new HBox();
        container.getStyleClass().add("centeredContainer");
        return container;
    }

    public void setSelected(boolean selected){
        setFocused(selected);
    }

    public boolean isSelected(){
        return isFocused();
    }

    public void updateText(){
        text.setText(plant.getId() + " - " + plant.getName());
    }


}
