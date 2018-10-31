package gui;

import directory.files.DocumentBuilder;
import gui.menu.MainMenuController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import static directory.plant.AccessModifier.testJson;


public class    DMSApplication extends Application {

    private static Stage primaryStage = new Stage();

    private VBox root;

    public static Locale locale = new Locale("da", "DK");

    private static final int MIN_WIDTH = 1024;
    private static final int MIN_HEIGHT = 768;
    private static final String APP_TITLE = "RG - Document Management System";

    private FXMLLoader fxmlLoader;
    public static final String fxmlPath = "/fxml/";

    private Node mainMenu, fileOverview, fileAdministration, plantAdministration, log;

    public static void main() {
        launch();
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        root = new VBox();
        root.setMinSize(MIN_WIDTH, MIN_HEIGHT);
        root.setPrefSize(MIN_WIDTH, MIN_HEIGHT);
        primaryStage.setMinHeight(MIN_HEIGHT);
        primaryStage.setMinWidth(MIN_WIDTH);

        // Load the language properties into the FXML loader
        ResourceBundle bundle = ResourceBundle.getBundle("Messages", locale);
        fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath + "MainMenu.fxml"), bundle);

        mainMenu = fxmlLoader.load();
        ((MainMenuController) fxmlLoader.getController()).init(this);

        root.getChildren().add(mainMenu);
        primaryStage.setTitle(APP_TITLE);
        primaryStage.setScene(new Scene(root));

        this.primaryStage = primaryStage;
        primaryStage.show();

        //System.out.println(DocumentBuilder.readAndUpdateCurrentID());
        //System.out.println(DocumentBuilder.readAndUpdateCurrentID());

        testJson();

        //JSonTest.JSONtester();

        switchWindow(ProgramPart.FILE_ADMINISTRATION);
    }

    // Shows the given part of the program
    public void switchWindow(ProgramPart programPart) {
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
            e.printStackTrace(); // todo show popup with error message for the user?
        }
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
