package gui.log;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class LogController {

    @FXML
    private TableView<rgEvent> tableView;

    @FXML
    private TableColumn<rgEvent,String> event;

    @FXML
    private TableColumn<rgEvent,String> user;

    @FXML
    private TableColumn<rgEvent,String> time;

    @FXML
   /* private void initialize(){
        event.setCellValueFactory(new PropertyValueFactory<rgEvent,String>("Event"));
        user.setCellValueFactory(new PropertyValueFactory<rgEvent,String>("User"));
        time.setCellValueFactory(new PropertyValueFactory<rgEvent,String>("Time"));

       // tableView.getItems().setAll(parseEventList);
    }*/

    private void parseEventList(){

    }
}
