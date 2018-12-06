package directory;

import app.ApplicationMode;
import gui.DMSApplication;
import gui.settings.InitializationController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class Settings {
    private static Preferences preferences = Preferences.userNodeForPackage(Settings.class);
    private static Settings settings;

    private static final String DEFAULT_NULL_VALUE = "null";

    // Keys for retrieving/storing data in preferences
    private static final String SERVER_PATH_PREF = "server_path";
    private static final String LOCAL_PATH_PREF = "local_path";
    private static final String USERNAME_PREF = "username";
    private static final String LANGUAGE_PREF = "language";

    private static final String APPLICATION_FOLDER_NAME = "RG DMS/";

    // Relative program paths
    private static final String ARCHIVE_PATH = "Archive/";
    private static final String DOCUMENTS_PATH = "Documents/";
    private static final String APP_FILES_PATH = "App Files/";
    private static final String ERROR_LOGS_PATH = "Error Logs/";

    private static final String WORKING_DIRECTORY_PATH = "Working Directory/";
    private static final String PUBLISHED_FILES_PATH = "Published Files/";

    // Default language is danish
    private static Locale language = DMSApplication.DK_LOCALE;

    private static String username = getComputerName();
    private static String serverPath;
    private static String localPath;

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
    private static void initializeSettingsPrompt() {
        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader();

            ResourceBundle bundle = ResourceBundle.getBundle("Messages", DMSApplication.getLanguage());
            fxmlLoader.setResources(bundle);
            System.out.println(preferences.absolutePath());

            fxmlLoader.setLocation(Settings.class.getResource(DMSApplication.fxmlPath + "Initialization.fxml"));

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

    public static String getPublishedDocumentsPath(){
        return serverPath + PUBLISHED_FILES_PATH + DOCUMENTS_PATH;
    }

    public static String getPublishedAppFilesPath(){
        return serverPath + PUBLISHED_FILES_PATH + APP_FILES_PATH;
    }

    // Returns the absolute path of the main files on the server
    public static String getServerDocumentsPath() {
        return serverPath + WORKING_DIRECTORY_PATH + DOCUMENTS_PATH;
    }

    // Returns the absolute path of the archived files on the server
    public static String getServerArchivePath() {
        return serverPath + WORKING_DIRECTORY_PATH +ARCHIVE_PATH;
    }

    // Returns the absolute path of the application files on the server
    public static String getServerAppFilesPath() {
        return serverPath + WORKING_DIRECTORY_PATH + APP_FILES_PATH;
    }

    // Returns the absolute path of the local file copies
    public static String getLocalFilesPath() {
        return localPath + DOCUMENTS_PATH;
    }

    // Returns the absolute path of the application files on the local drive
    public static String getLocalAppFilesPath() {
        return localPath + APP_FILES_PATH;
    }

    public static String getServerErrorLogsPath() { return serverPath + ERROR_LOGS_PATH; }

    public static String getUsername() {
        return username;
    }

    public static void setServerPath(String newPath) {
        newPath = getUniversalPath(newPath);
        serverPath = completeApplicationPath(newPath);
        preferences.put(SERVER_PATH_PREF, serverPath);
    }

    public static void setLocalPath(String newPath) {
        newPath = getUniversalPath(newPath);
        localPath = completeApplicationPath(newPath);
        preferences.put(USERNAME_PREF, localPath);
    }

    // Replaces backslashes with forward slashes
    public static String getUniversalPath(String path) {

        return path.replace("\\", "/").replace("//", "/");
    }

    // Adds the application folder name to the path if it's not already in there
    private static String completeApplicationPath(String path) {
        String completedPath = path;
        if (completedPath.charAt(path.length() - 1) != '/')
            completedPath += '/';

        // Append the application folder if it is not included in the given path
        if (!completedPath.contains(APPLICATION_FOLDER_NAME))
            completedPath += APPLICATION_FOLDER_NAME;

        return completedPath;
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

    public static String getServerPath() {
        return serverPath;
    }

    public static String getLocalPath() {
        return localPath;
    }

    public static String toString2() {
        return "server path:           " + getServerPath() + System.getProperty("line.separator") +
                "server documents path: " + getServerDocumentsPath() + System.getProperty("line.separator") +
                "server app files path  " + getServerAppFilesPath() + System.getProperty("line.separator") +
                "server archive path    " + getServerArchivePath() + System.getProperty("line.separator") +
                "local path:            " + getLocalFilesPath() + System.getProperty("line.separator") +
                "local documents path:  " + getLocalFilesPath() + System.getProperty("line.separator") +
                "local app files path:  " + System.getProperty("line.separator");
    }
}
