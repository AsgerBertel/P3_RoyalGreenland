package directory;

import app.ApplicationMode;
import gui.AlertBuilder;
import gui.DMSApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class SettingsManager {
    private static Preferences preferences = Preferences.userNodeForPackage(SettingsManager.class);

    private static final String DEFAULT_NULL_VALUE = "null";

    // Keys for retrieving/storing data in preferences
    private static final String SERVER_PATH_PREF = "server_path";
    private static final String LOCAL_PATH_PREF = "local_path";
    private static final String USERNAME_PREF = "username";
    private static final String LANGUAGE_PREF = "language";

    public static final String APPLICATION_FOLDER_NAME = DMSApplication.APP_TITLE;

    // Relative program paths
    private static final String ARCHIVE_PATH = "Archive" + File.separator;
    private static final String DOCUMENTS_PATH = "Documents" + File.separator;
    private static final String APP_FILES_PATH = "App Files" + File.separator;
    private static final String ERROR_LOGS_PATH = "Error Logs" + File.separator;

    private static final String WORKING_DIRECTORY_PATH = "Working Directory" + File.separator;
    private static final String PUBLISHED_FILES_PATH = "Published Files" + File.separator;

    // Default language is danish
    private static Locale language = DMSApplication.DK_LOCALE;

    private static String username = getComputerName();
    private static String serverPath;
    private static String localPath;
    private static Path pathServer;
    private static Path pathLocal;

    public static void loadSettings(ApplicationMode applicationMode) {
        serverPath = preferences.get(SERVER_PATH_PREF, DEFAULT_NULL_VALUE);
        localPath = preferences.get(LOCAL_PATH_PREF, DEFAULT_NULL_VALUE);

        username = preferences.get(USERNAME_PREF, getComputerName());

        String languageString = preferences.get(LANGUAGE_PREF, DEFAULT_NULL_VALUE);
        if (languageString.equals(DMSApplication.DK_LOCALE.toString()))
            language = DMSApplication.DK_LOCALE;
        else if (languageString.equals(DMSApplication.GL_LOCALE.toString()))
            language = DMSApplication.GL_LOCALE;

        // Prompt the user for paths if any are missing
        if (serverPath.equals(DEFAULT_NULL_VALUE))
            initializeSettingsPrompt();
        else if (applicationMode == ApplicationMode.VIEWER && localPath.equals(DEFAULT_NULL_VALUE))
            initializeSettingsPrompt();
    }

    // Prompt the user for the path to both server and local storage
    public static void initializeSettingsPrompt() {
        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader();

            ResourceBundle bundle = ResourceBundle.getBundle("Messages", DMSApplication.getLanguage());
            fxmlLoader.setResources(bundle);

            fxmlLoader.setLocation(SettingsManager.class.getResource(DMSApplication.fxmlPath + "Initialization.fxml"));

            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle(DMSApplication.APP_TITLE);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static Locale getLanguage() {
        return language;
    }

    public static void setLanguage(Locale newLanguage) {
        language = newLanguage;
        preferences.put(LANGUAGE_PREF, newLanguage.toString());
    }

    public static Path getPublishedDocumentsPath(){
        return getServerPath().resolve(PUBLISHED_FILES_PATH).resolve(DOCUMENTS_PATH);
    }

    public static Path getPublishedAppFilesPath(){
        return getServerPath().resolve(PUBLISHED_FILES_PATH).resolve(APP_FILES_PATH);
    }

    // Returns the absolute path of the main files on the server
    public static Path getServerDocumentsPath() {
        return getServerPath().resolve(WORKING_DIRECTORY_PATH).resolve(DOCUMENTS_PATH);
    }

    // Returns the absolute path of the archived files on the server
    public static Path getServerArchivePath() {
        return getServerPath().resolve(WORKING_DIRECTORY_PATH).resolve(ARCHIVE_PATH);
    }

    // Returns the absolute path of the application files on the server
    public static Path getServerAppFilesPath() {
        return getServerPath().resolve(WORKING_DIRECTORY_PATH).resolve(APP_FILES_PATH);
    }

    // Returns the absolute path of the local file copies
    public static Path getLocalFilesPath() {
        return getLocalPath().resolve(DOCUMENTS_PATH);
    }

    // Returns the absolute path of the application files on the local drive
    public static Path getLocalAppFilesPath() {
        return getLocalPath().resolve(APP_FILES_PATH);
    }
    public static Path getLocalErrorLogsPath() {
        return getLocalPath().resolve(ERROR_LOGS_PATH);
    }
    public static Path getServerErrorLogsPath() {
        return getServerPath().resolve(ERROR_LOGS_PATH);
    }

    public static String getUsername() {
        return username;
    }

    public static void setServerPath(Path newPath) {
        serverPath = completeApplicationPath(newPath).toString();
        preferences.put(SERVER_PATH_PREF, serverPath);
        AlertBuilder.programRestartPopup();
        DMSApplication.getDMSApplication().restartApp();
    }

    public static void setLocalPath(Path newPath) {
        localPath = completeApplicationPath(newPath).toString();
        preferences.put(LOCAL_PATH_PREF, localPath);
        AlertBuilder.programRestartPopup();
        DMSApplication.getDMSApplication().restartApp();
    }

    // Adds the application folder name to the path if it's not already in there
    private static Path completeApplicationPath(Path path) {
        String completedPath = path.toString();

        // Append the application folder if it is not included in the given path
        if (!completedPath.contains(APPLICATION_FOLDER_NAME))
            completedPath += File.separator + APPLICATION_FOLDER_NAME;

        return Paths.get(completedPath);
    }

    public static void setUsername(String newName) {
        username = newName;
        preferences.put(USERNAME_PREF, username);
    }

    public static String getComputerName() {
        Map<String, String> env = System.getenv();
        if (env.containsKey("COMPUTERNAME"))
            return env.get("COMPUTERNAME");
        else return env.getOrDefault("HOSTNAME", "Unknown Computer");
    }

    public static Path getServerPath() {
        return Paths.get(serverPath);
    }

    public static Path getLocalPath() {
        return Paths.get(localPath);
    }

    public static String toString2() {
        return  "server path:           " + getServerPath() + System.getProperty("line.separator") +
                "server documents path: " + getServerDocumentsPath() + System.getProperty("line.separator") +
                "server app files path  " + getServerAppFilesPath() + System.getProperty("line.separator") +
                "server archive path    " + getServerArchivePath() + System.getProperty("line.separator") +
                "local path:            " + getLocalFilesPath() + System.getProperty("line.separator") +
                "local documents path:  " + getLocalFilesPath() + System.getProperty("line.separator") +
                "local app files path:  " + System.getProperty("line.separator");
    }
}
