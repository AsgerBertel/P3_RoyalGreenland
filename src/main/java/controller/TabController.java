package controller;

import app.DMSApplication;
import javafx.fxml.Initializable;

public interface TabController extends Initializable {

    void initReference(DMSApplication dmsApplication);

    void update();

}
