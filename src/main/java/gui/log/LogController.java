package gui.log;

import gui.TabController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.*;

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
    private TextField searchField;
    private List<rgEvent> listOfEvents;
    private boolean sortedByTime = false;

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


    public void keyReleased(KeyEvent keyEvent){
        search(searchField.getText().toLowerCase());
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

    public void sortByUser(){
        List<rgEvent> sortedList = listOfEvents;
        Collections.sort(sortedList, Comparator.comparing(rgEvent::getUser));
        tableView.getItems().setAll(sortedList);
    }
    public void sortByChangeType(){
        List<rgEvent> sortedList = listOfEvents;
        Collections.sort(sortedList, Comparator.comparing(rgEvent::getEventType));
        tableView.getItems().setAll(sortedList);
    }
    public void sortByTime(){
        List<rgEvent> sortedList = listOfEvents;
        Collections.sort(sortedList, Comparator.comparing(rgEvent::getLocalDateTime));
        if(sortedByTime){
            Collections.reverse(sortedList);
            tableView.getItems().setAll(sortedList);
            sortedByTime = false;
        }else{
            tableView.getItems().setAll(sortedList);
            sortedByTime = true;
        }

    }
}
