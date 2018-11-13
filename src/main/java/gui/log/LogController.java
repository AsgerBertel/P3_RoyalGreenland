package gui.log;

import gui.TabController;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class LogController implements TabController {


    @FXML
    private TableView<rgEvent> tableView;

    @FXML
    private TableColumn<rgEvent, String> event;

    @FXML
    private TableColumn<rgEvent, String> user;

    @FXML
    private TableColumn<rgEvent, String> time;

    @FXML
    private Pane box;

    @FXML
    private VBox vbox;

    @FXML
    private TextField searchField;

    private boolean searchToggled = false;
    private List<rgEvent> listOfEvents;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        LoggingTools lt = new LoggingTools();
        listOfEvents = lt.listOfAllEvents();
        update();
    }

    @Override
    public void update() {
        event.setCellValueFactory(new PropertyValueFactory<rgEvent, String>("Event"));
        user.setCellValueFactory(new PropertyValueFactory<rgEvent, String>("User"));
        time.setCellValueFactory(new PropertyValueFactory<rgEvent, String>("Time"));
        tableView.getItems().setAll(listOfEvents);
    }

    public void searchClicked(ActionEvent actionEvent) {
        if (searchToggled) {
            box.toBack();
            searchToggled = false;
        } else {
            box.toFront();
            searchToggled = true;
        }
    }

    public void keyReleased(KeyEvent keyEvent){
        if(keyEvent.getCode() == KeyCode.ENTER){
            box.toBack();
            searchToggled = false;
        }
        search(searchField.getText());
    }

    private void search(String search) {
        List<rgEvent> foundEvents = new ArrayList<>();
        for( rgEvent e : listOfEvents){
            if(e.getFileName().toLowerCase().contains(search) || e.getUser().toLowerCase().contains(search)){
                foundEvents.add(e);
            }
        }
        tableView.getItems().setAll(foundEvents);
    }

}
