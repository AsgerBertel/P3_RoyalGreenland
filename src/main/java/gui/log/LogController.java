package gui.log;

import gui.TabController;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
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
    private ImageView searchImage;

    @FXML
    private TextField searchField;

    LoggingTools lt = new LoggingTools();
    private List<rgEvent> listOfEvents;
    private boolean sortedByTime = false;
    private boolean sortedByUser = false;
    private boolean sortedByFile = false;
    private Image timeOld = new Image("/icons/menu/timeNew.png");
    private Image timeNew = new Image("/icons/menu/timeOld.png");

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        searchImage.setImage(timeOld);
        update();
    }

    @Override
    public void update() {
        tableView.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY );
        event.setCellValueFactory(new PropertyValueFactory<rgEvent, String>("Event"));
        user.setCellValueFactory(new PropertyValueFactory<rgEvent, String>("User"));
        time.setCellValueFactory(new PropertyValueFactory<rgEvent, String>("Time"));
        listOfEvents = lt.getAllEvents();
        tableView.getItems().setAll(listOfEvents);
        sortedByTime = false;
        sortByTime();
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
            searchImage.setImage(timeNew);
            tableView.getItems().setAll(sortedList);
            sortedByTime = false;
        }else{
            Collections.reverse(sortedList);
            searchImage.setImage(timeOld);
            tableView.getItems().setAll(sortedList);
            sortedByTime = true;
        }
    }
}
