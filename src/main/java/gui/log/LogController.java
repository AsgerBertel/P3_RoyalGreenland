package gui.log;

import gui.DMSApplication;
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
    private TableView<LogEvent> tableView;

    @FXML
    private TableColumn<LogEvent, String> event;

    @FXML
    private TableColumn<LogEvent, String> user;

    @FXML
    private TableColumn<LogEvent, String> time;

    @FXML
    private ImageView searchImage;

    @FXML
    private TextField searchField;

    private DMSApplication dmsApplication;

    LoggingTools lt = new LoggingTools();
    private List<LogEvent> listOfEvents;
    private boolean sortedByTime = false;
    private boolean sortedByUser = false;
    private boolean sortedByFile = false;
    private Image timeOld = new Image("/icons/menu/timeNew.png");
    private Image timeNew = new Image("/icons/menu/timeOld.png");

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        searchImage.setImage(timeOld);
    }

    @Override
    public void initReference(DMSApplication dmsApplication) {
        this.dmsApplication = dmsApplication;
    }

    @Override
    public void update() {
        tableView.setColumnResizePolicy( TableView.CONSTRAINED_RESIZE_POLICY );

        event.setCellValueFactory(new PropertyValueFactory<LogEvent, String>("EventString")); // Calls getEventString() in the LogEvent
        user.setCellValueFactory(new PropertyValueFactory<LogEvent, String>("User")); // Calls getUser() in the LogEvent
        time.setCellValueFactory(new PropertyValueFactory<LogEvent, String>("Time")); // Calls getEventString() in the LogEvent
        
        listOfEvents = lt.getAllEvents();
        tableView.getItems().setAll(listOfEvents);
        sortedByTime = false;
        sortByTime();
    }




    public void keyReleased(KeyEvent keyEvent){
        search(searchField.getText().toLowerCase());
    }

    private void search(String search) {
        List<LogEvent> foundEvents = new ArrayList<>();
        for( LogEvent e : listOfEvents){
            if(e.getPrefixString().toLowerCase().contains(search) || e.getUser().toLowerCase().contains(search) || e.getSuffixString().toLowerCase().contains(search)){
                foundEvents.add(e);
            }
        }
        tableView.getItems().setAll(foundEvents);
    }

    public void sortByUser(){
        List<LogEvent> sortedList = listOfEvents;
        Collections.sort(sortedList, Comparator.comparing(LogEvent::getUser));
        tableView.getItems().setAll(sortedList);
    }
    public void sortByChangeType(){
        List<LogEvent> sortedList = listOfEvents;
        Collections.sort(sortedList, Comparator.comparing(LogEvent::getEventType));
        tableView.getItems().setAll(sortedList);
    }
    public void sortByTime(){
        List<LogEvent> sortedList = listOfEvents;
        Collections.sort(sortedList, Comparator.comparing(LogEvent::getLocalDateTime));

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
