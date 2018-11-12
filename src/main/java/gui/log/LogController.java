package gui.log;

import gui.TabController;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.util.ResourceBundle;

public class LogController implements TabController {


    @FXML
    private TableView<rgEvent> tableView;

    @FXML
    private TableColumn<rgEvent,String> event;

    @FXML
    private TableColumn<rgEvent,String> user;

    @FXML
    private TableColumn<rgEvent,String> time;

    @FXML
    public void initialize(URL location, ResourceBundle resources){
        LoggingTools lt = new LoggingTools();
        event.setCellValueFactory(new PropertyValueFactory<rgEvent,String>("Event"));
        user.setCellValueFactory(new PropertyValueFactory<rgEvent,String>("User"));
        time.setCellValueFactory(new PropertyValueFactory<rgEvent,String>("Time"));

        tableView.getItems().setAll(lt.listOfAllEvents());

    }
    @Override
    public void update() {

    }
}
