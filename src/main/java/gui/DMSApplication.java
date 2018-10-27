package gui;

import gui.menu.MainMenuController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;


public class DMSApplication extends Application {

    private static Stage primaryStage = new Stage();

    private BorderPane root;

    public static Locale locale = new Locale("da", "DK");

    private static final int MIN_WIDTH = 800;
    private static final int MIN_HEIGHT = 600;
    private static final String APP_TITLE = "RG - Document Management System";

    private FXMLLoader fxmlLoader;
    public static final String fxmlPath = "/fxml/";

    private Node mainMenu, fileOverview, fileAdministration, plantAdministration, log;

    public static void main() {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        root = new BorderPane();
        root.setMinSize(MIN_WIDTH, MIN_HEIGHT);
        root.setPrefSize(MIN_WIDTH, MIN_HEIGHT);

        // Load the language properties into the FXML loader
        ResourceBundle bundle = ResourceBundle.getBundle("Messages", locale);
        fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath + "MainMenu.fxml"), bundle);

        mainMenu = fxmlLoader.load();
        ((MainMenuController) fxmlLoader.getController()).init(this);

        root.setTop(mainMenu);

        primaryStage.setTitle(APP_TITLE);
        primaryStage.setScene(new Scene(root));

        this.primaryStage = primaryStage;
        primaryStage.show();

        switchWindow(ProgramPart.FILE_ADMINISTRATION);
    }

    // Shows the given part of the program
    public void switchWindow(ProgramPart programPart) {
        Node newNode = null;
        try {
            newNode = programPart.getNode();
        } catch (IOException e) {
            e.printStackTrace(); // todo show popup with error message for the user?
        }
        root.setCenter(newNode);
    }

    public static void restartApp() throws Exception{
        DMSApplication main = new DMSApplication();
        primaryStage.close();
        main.start(new Stage());
    }

    public static void setLocale(Locale locale) {
        DMSApplication.locale = locale;
    }
}
