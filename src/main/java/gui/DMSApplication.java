package gui;

import app.ApplicationMode;
import directory.FileManager;
import gui.menu.MainMenuController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class DMSApplication extends Application {

    private Stage primaryStage = new Stage();

    private VBox root;

    private static Locale locale = new Locale("da", "DK");
    private static ResourceBundle messages = ResourceBundle.getBundle("Messages", locale);

    private static final int MIN_WIDTH = 1024;
    private static final int MIN_HEIGHT = 768;
    private static final String APP_TITLE = "RG - Document Management System";

    private FXMLLoader fxmlLoader;
    public static final String fxmlPath = "/fxml/";

    private Node mainMenu, fileOverview, fileAdministration, plantAdministration, log;

    private ApplicationMode applicationMode;

    // This empty constructor needs to be here for reasons related to launching this Application from a seperate class
    public DMSApplication(){}

    @Override
    public void start(Stage primaryStage) throws Exception{
        String applicationMode = getParameters().getRaw().get(0);

        root = new VBox();
        root.setMinSize(MIN_WIDTH, MIN_HEIGHT);
        root.setPrefSize(MIN_WIDTH, MIN_HEIGHT);
        primaryStage.setMinHeight(MIN_HEIGHT);
        primaryStage.setMinWidth(MIN_WIDTH);

        // Load the language properties into the FXML loader
        ResourceBundle bundle = ResourceBundle.getBundle("Messages", locale);

        if(applicationMode.equals(ApplicationMode.ADMIN.toString())){
            fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath + "AdminMainMenu.fxml"), bundle);
        }else{
            fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath + "ViewerMainMenu.fxml"), bundle);
        }


        // Improve font rendering
        System.setProperty("prism.lcdtext", "false");
        System.setProperty("prism.text", "t2k");

        mainMenu = fxmlLoader.load();
        ((MainMenuController) fxmlLoader.getController()).init(this);

        root.getChildren().add(mainMenu);
        primaryStage.setTitle(APP_TITLE);
        primaryStage.setScene(new Scene(root));

        this.primaryStage = primaryStage;
        primaryStage.show();

        switchWindow(TabLoader.FILE_ADMINISTRATION);

    }

    // Shows the given part of the program
    public void switchWindow(TabLoader programPart) {
        // Remove all currently added elements except the main menu
        while(root.getChildren().size() > 1)
            root.getChildren().remove(1);

        Pane newPane = null;

        try {
            newPane = programPart.getPane();

            // Make sure the new pane scales to the rest of the window
            newPane.prefHeightProperty().bind(root.heightProperty());
            newPane.prefWidthProperty().bind(root.widthProperty());

            root.getChildren().add(newPane);
        } catch (IOException e) {

            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Fejl");
            alert.setContentText("Kontakt Udvikleren"); // todo lol.

        }
    }

    public void restartApp() throws Exception{
        primaryStage.close();
        start(new Stage());
    }

    public void changeLanguage(Locale locale) {
        this.locale = locale;
        messages = ResourceBundle.getBundle("Messages", locale);
    }

    public static Locale getLanguage(){
        return locale;
    }

    public static String getMessage(String key){
        return messages.getString(key);
    }

}
