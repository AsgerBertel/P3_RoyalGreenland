package gui;

import app.ApplicationMode;
import directory.Settings;
import gui.menu.MainMenuController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import json.AppFilesManager;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import static gui.TabLoader.FILE_ADMINISTRATION;

public class DMSApplication extends Application {

    private static Stage primaryStage;

    private VBox root;

    public static final Locale DK_LOCALE = new Locale("da", "DK");
    public static final Locale GL_LOCALE = new Locale("kl", "GL");

    private static Locale locale = DK_LOCALE;
    private static ResourceBundle messages = ResourceBundle.getBundle("Messages", locale);

    private static final int MIN_WIDTH = 1024;
    private static final int MIN_HEIGHT = 768;
    public static final String APP_TITLE = "RG - Document Management System";

    private FXMLLoader fxmlLoader;
    public static final String fxmlPath = "/fxml/";

    private Node mainMenu, fileOverview, fileAdministration, plantAdministration, log;

    private static ApplicationMode applicationMode;

    private Settings settings;


    // This empty constructor needs to be here for reasons related to launching this Application from a seperate class
    public DMSApplication() {
    }

    @Override
    public void start(Stage stage) throws Exception {
        // Figure out if program should run in admin or viewer mode
        String appModeParameter = getParameters().getRaw().get(0);
        applicationMode = ApplicationMode.valueOf(appModeParameter);

        initializeApplication();

        this.primaryStage = stage;

        // Load settings from preferences and prompt the user for new path if necessary
        loadRootElement();
        primaryStage.setTitle(APP_TITLE);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        if (applicationMode.equals(ApplicationMode.ADMIN)) {
            switchWindow(FILE_ADMINISTRATION);
        } else {
            switchWindow(TabLoader.FILE_OVERVIEW);
        }
    }

    private void loadRootElement() throws IOException {
        root = new VBox();
        root.setMinSize(MIN_WIDTH, MIN_HEIGHT);
        root.setPrefSize(MIN_WIDTH, MIN_HEIGHT);
        primaryStage.setMinHeight(MIN_HEIGHT);
        primaryStage.setMinWidth(MIN_WIDTH);
        root.getStylesheets().add("/styles/masterSheet.css");

        // Load the language properties into the FXML loader
        ResourceBundle bundle = ResourceBundle.getBundle("Messages", locale);

        if (applicationMode.equals(ApplicationMode.ADMIN)) {
            fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath + "AdminMainMenu.fxml"), bundle);
        } else {
            fxmlLoader = new FXMLLoader(getClass().getResource(fxmlPath + "ViewerMainMenu.fxml"), bundle);
        }

        // Improve font rendering
        System.setProperty("prism.lcdtext", "false");
        System.setProperty("prism.text", "t2k");

        mainMenu = fxmlLoader.load();
        ((MainMenuController) fxmlLoader.getController()).init(this);

        root.getChildren().add(mainMenu);
    }

    // Shows the given part of the program
    public void switchWindow(TabLoader programPart) {
        // Remove all currently added elements except the main menu
        while (root.getChildren().size() > 1)
            root.getChildren().remove(1);

        Pane newPane = null;

        try {
            newPane = programPart.getPane(this);

            // Make sure the new pane scales to the rest of the window
            newPane.prefHeightProperty().bind(root.heightProperty());
            newPane.prefWidthProperty().bind(root.widthProperty());

            root.getChildren().add(newPane);
        } catch (IOException e) {

            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Fejl");
            alert.setContentText("Kontakt Udvikleren Mail: ds323@student.aau.dk");
        }
    }

    public void restartApp() throws Exception {
        start(primaryStage);
    }

    public void changeLanguage(Locale newLocale) {
        locale = newLocale;
        Settings.setLanguage(newLocale);
        messages = ResourceBundle.getBundle("Messages", newLocale);
    }

    public static Locale getLanguage() {
        return locale;
    }

    public static String getMessage(String key) {
        return messages.getString(key);
    }

    public static ApplicationMode getApplicationMode() {
        return applicationMode;
    }

    private void initializeApplication() {
        // Load settings and initialize paths if non are saved
        Settings.loadSettings(applicationMode);
        this.locale = Settings.getLanguage();
        this.messages = ResourceBundle.getBundle("Messages", locale);
        // Create application folder if they are missing
        if (applicationMode.equals(ApplicationMode.VIEWER)) {
            try {
                // Create any local app directories that might be missing
                AppFilesManager.createLocalDirectories();
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Fejl");
                alert.setHeaderText(null);
                alert.setContentText("Vælg din lokale sti igen");
            }
        } else if (applicationMode.equals(ApplicationMode.ADMIN)) {
            try {
                // Create any server side directories that might be missing
                AppFilesManager.createServerDirectories();
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Fejl");
                alert.setHeaderText(null);
                alert.setContentText("Chek om du har forbindelse til serveren");

            }
        }
    }

}
