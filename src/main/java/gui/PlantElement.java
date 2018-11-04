package gui;

import directory.plant.Plant;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class PlantElement extends BorderPane {

    private Plant plant;
    private Text text;

    public PlantElement(Plant plant){
        this.plant = plant;
        getStyleClass().add("plantElement");

        text = new Text(plant.getId() + " - " + plant.getName());
        text.getStyleClass().add("plantText");
        text.setTranslateX(6);

        HBox container = getCenteredContainer();
        container.getChildren().add(text);
        setLeft(container);

        setEventHandler(MouseEvent.MOUSE_PRESSED, event -> onClick(event));
    }

    protected void onClick(MouseEvent event){
        requestFocus(); // todo This does not work as the button will be unfocused when any other element is clicked
        event.consume();
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




}
