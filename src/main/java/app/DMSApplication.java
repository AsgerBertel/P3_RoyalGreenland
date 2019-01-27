package app;

import io.update.FileUpdater;
import model.managing.SettingsManager;
import gui.AlertBuilder;
import gui.ExitChecker;
import gui.Tab;
import controller.FileAdminController;
import log.LoggingErrorTools;
import controller.MainMenuController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import io.json.AppFilesChangeListener;
import io.json.AppFilesManager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.util.Locale;
import java.util.ResourceBundle;

import static gui.Tab.FILE_ADMINISTRATION;

public class DMSApplication extends Application {

    private static Stage primaryStage;

    private VBox root;

    public static final Locale DK_LOCALE = new Locale("da", "DK");
    public static final Locale GL_LOCALE = new Locale("kl", "GL");

    private static Locale locale = DK_LOCALE;
    private static ResourceBundle messages = ResourceBundle.getBundle("Messages", locale);

    private static final int MIN_WIDTH = 1024;
    private static final int MIN_HEIGHT = 768;
    public static final String APP_TITLE = "RG DMS";

    private FXMLLoader fxmlLoader;
    public static final String fxmlPath = "/fxml/";

    private Node mainMenu, fileOverview, fileAdministration, plantAdministration, log;

    private static ApplicationMode applicationMode;

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    private SettingsManager settings;
    private Tab currentTab;

    private static AppFilesChangeListener externalUpdateListener;
    private static FileUpdater localFileUpdater;


    private static DMSApplication dmsApplication;

    // This empty constructor needs to be here for reasons related to launching this Application from a separate class
    public DMSApplication() {
    }

    @Override
    public void start(Stage stage) {
        // Creates a new thread and checks for unexpected error codes. If so preferences are reset.
        new ExitChecker();

        dmsApplication = this;
        // Figure out if program should run in admin or viewer mode
        String appModeParameter = getParameters().getRaw().get(0);
        applicationMode = ApplicationMode.valueOf(appModeParameter);

        initializeApplication();
        primaryStage = stage;

        // Load settings from preferences and prompt the user for new path if necessary
        loadRootElement();

        primaryStage.setTitle(APP_TITLE);
        primaryStage.setScene(new Scene(root));
        // Set icon for program.
        primaryStage.getIcons().add(new Image("icons/Logo.png"));
        primaryStage.show();

        if (applicationMode.equals(ApplicationMode.ADMIN)) {
            switchTab(FILE_ADMINISTRATION);
        } else {
            switchTab(Tab.FILE_OVERVIEW);
        }
    }


    private void loadRootElement() {
        root = new VBox();
        root.setMinSize(MIN_WIDTH, MIN_HEIGHT);
        root.setPrefSize(Screen.getPrimary().getVisualBounds().getMaxX() - 200, Screen.getPrimary().getVisualBounds().getMaxY() - 100);

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

        try {
            mainMenu = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
            AlertBuilder.IOExceptionPopUpWithString(fxmlLoader.getLocation().getPath());
            LoggingErrorTools.log(e, 6);
            System.exit(6);
        }
        ((MainMenuController) fxmlLoader.getController()).init(this);

        root.getChildren().add(mainMenu);
    }

    // Shows the given part of the program
    public void switchTab(Tab newTab) {
        // Remove all currently added elements except the main menu
        while (root.getChildren().size() > 1)
            root.getChildren().remove(1);

        Pane newPane;

        try {
            newPane = newTab.getPane(this, getLanguage());
            // Make sure the new pane scales to the rest of the window
            newPane.prefHeightProperty().bind(root.heightProperty());
            newPane.prefWidthProperty().bind(root.widthProperty());

            root.getChildren().add(newPane);
            currentTab = newTab;
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Fejl");
            alert.setContentText("Kontakt udvikleren\n Mail: ds323@student.aau.dk");
        }
    }

    public Tab getCurrentTab() {
        return currentTab;
    }

    private void restartApp() {
        stopWatcherThreads();
        start(primaryStage);
    }

    public void changeLanguage(Locale newLocale) {
        locale = newLocale;
        SettingsManager.setLanguage(newLocale);
        messages = ResourceBundle.getBundle("Messages", newLocale);
        try {
            restartApp();
        } catch (Exception e) {
            e.printStackTrace();
            String msgKey = "Exception.FailedRestart.";
            AlertBuilder.customErrorPopUp(getMessage(msgKey + "Title"),
                    getMessage(msgKey + "Header"), getMessage(msgKey + "Context"));
        }
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

    public void initializeApplication() {
        // Load settings and initialize paths if non are saved
        SettingsManager.loadSettings(applicationMode);
        locale = SettingsManager.getLanguage();
        messages = ResourceBundle.getBundle("Messages", locale);

        // Create application folder if they are missing
        try {
            if (applicationMode.equals(ApplicationMode.VIEWER)) {
                // Create any local app directories that might be missing
                AppFilesManager.createLocalDirectories();
                startLocalFileUpdater();
            } else if (applicationMode.equals(ApplicationMode.ADMIN)) {
                // Create any server side directories that might be missing
                AppFilesManager.createServerDirectories();
                if (externalUpdateListener != null)
                    externalUpdateListener.stop();

                // Listen for changes made by other administrators
                startExternalChangeListener();
            }
        } catch (InvalidPathException | FileNotFoundException e) {
            e.printStackTrace();
            AlertBuilder.fileNotFoundPopUp();
            SettingsManager.initializeSettingsPrompt();
        } catch (IOException e) {
            e.printStackTrace();
            AlertBuilder.IOExceptionPopUp();
            LoggingErrorTools.log(e, 4);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Critical error");
            alert.setHeaderText("Program shutting down");
            alert.showAndWait();
            System.exit(4);
        }
    }

    public static DMSApplication getDMSApplication() {
        return dmsApplication;
    }

    private void startLocalFileUpdater() {
        if (localFileUpdater != null && localFileUpdater.isRunning()) {
            stopLocalFileUpdater();
            System.out.println("Stopped local file updater to start a new file updater thread");
        }
        localFileUpdater = new FileUpdater(this);
        localFileUpdater.start();
    }

    private void stopLocalFileUpdater() {
        if (localFileUpdater != null)
            localFileUpdater.setRunning(false);
    }

    private void startExternalChangeListener() {
        if (externalUpdateListener != null && externalUpdateListener.isRunning()) {
            stopExternalChangeListener();
            System.out.println("Stopped existing externalUpdateListener to start a new externalChangeListener");
        }
        externalUpdateListener = new AppFilesChangeListener(this);
        externalUpdateListener.start();
    }

    private void stopExternalChangeListener() {
        if (externalUpdateListener != null)
            externalUpdateListener.stop();
    }

    public void startWatcherThreads() {
        if(applicationMode.equals(ApplicationMode.ADMIN))
            startExternalChangeListener();
        else
            startLocalFileUpdater();
    }

    public void stopWatcherThreads() {
        stopLocalFileUpdater();
        stopExternalChangeListener();
        FileAdminController fileAdminController = (FileAdminController) Tab.FILE_ADMINISTRATION.getTabController();
        if(fileAdminController != null)
            fileAdminController.stopWatchThread();
    }

}