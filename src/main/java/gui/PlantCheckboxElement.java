package gui;

import directory.plant.Plant;
import javafx.scene.control.CheckBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.util.function.Consumer;

public class PlantCheckboxElement extends PlantElement{

    private CheckBox checkBox;

    public PlantCheckboxElement(Plant plant) {
        super(plant);

        // Add checkbox to the right side of the element
        checkBox = new CheckBox();
        HBox container = getCenteredContainer();
        container.getChildren().add(checkBox);
        container.setTranslateX(-8);
        setRight(container);

        // Define behaviour when the element is clicked
        checkBox.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> onCheckBoxClick(event));
        this.setEventHandler(MouseEvent.MOUSE_PRESSED, event -> onClick(event));
    }

    private void onCheckBoxClick(MouseEvent event){
        // Handle checkbox click the same way as a click on the whole PlantElement
        onClick(event);

        // Prevent the event from triggering any other event handlers (including the default checkbox click handler)
        event.consume();
    }

    @Override // Reverses the current 'selected' state
    protected void onClick(MouseEvent event){
        boolean newValue = !checkBox.isSelected();
        checkBox.setSelected(newValue);
        setFocused(newValue);
    }

    @Override
    public boolean isSelected(){
        return checkBox.isSelected();
    }

    @Override
    public void setSelected(boolean selected){
        checkBox.setSelected(selected);
        setFocused(selected);
    }






}
