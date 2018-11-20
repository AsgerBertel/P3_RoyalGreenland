package directory;

import gui.DMSApplication;
import gui.settings.InitializationController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import gui.DMSApplication;

import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class Settings {
    // todo add default language setting
    private static Preferences preferences = Preferences.userNodeForPackage(Settings.class);
    private static Settings settings;

    private static final String DEFAULT_NULL_VALUE = "null";

    // Keys for retrieving/storing data in preferences
    private static final String SERVER_PATH_PREF = "server_path";
    private static final String LOCAL_PATH_PREF = "local_path";
    private static final String USERNAME_PREF = "username";

    //language key
    private static final String LANGUAGE_PREF = "language";

    // Relative program paths
    private static final String ARCHIVE_PATH = "Archive/";
    private static final String DOCUMENTS_PATH = "Documents/";
    private static final String APP_FILES_PATH = "App Files/";


    // Other preferences
    private String username = "Unknown user";
    private Locale language;

    private static loggingTools.LogEvent(file.getName(),LogEventType.CREATED);

    private Settings() {
        // Loads preferences
        preferences = Preferences.userNodeForPackage(PreferencesManager.class);
        loadPreferences();
    }

    private void loadPreferences() {
        serverPath = preferences.get(SERVER_PATH_PREF, DEFAULT_NULL_VALUE);
        localPath = preferences.get(LOCAL_PATH_PREF, DEFAULT_NULL_VALUE);

        // Add new path values if non exists
        if (true || serverPath.equals(DEFAULT_NULL_VALUE)) { // todo if(true) is obvs temporary
            getNewPaths();
        }

        String languageString = preferences.get(LANGUAGE_PREF, DEFAULT_NULL_VALUE);

        if (languageString.equals(DMSApplication.DK_LOCALE))
            language = DMSApplication.DK_LOCALE;
        else if (languageString.equals(DMSApplication.GL_LOCALE))
            language = DMSApplication.GL_LOCALE;
        else
            setLanguage(DMSApplication.getLanguage());

        username = preferences.get(USERNAME_PREF, DEFAULT_NULL_VALUE);
        if (username.equals(DEFAULT_NULL_VALUE))
            setUsername(getComputerName());

        serverPath = preferences.get(SERVER_PATH_PREF, DEFAULT_NULL_VALUE);
        localPath = preferences.get(LOCAL_PATH_PREF, DEFAULT_NULL_VALUE);

        if (true || serverPath.equals(DEFAULT_NULL_VALUE) || localPath.equals(DEFAULT_NULL_VALUE)) {
            initializePathsPrompt();
        }
    }
}

    public Locale getLanguage() {
        return language;
    }

    // Prompt the user for the path to both server and local storage
    private void initializePathsPrompt() {
        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader();

            public void getNewPaths () {
                // temporary hardcoded paths todo Get paths from user input
                serverPath = "Sample files/Server/";
                localPath = "Sample files/Local Disk/";

                ResourceBundle bundle = ResourceBundle.getBundle("Messages", DMSApplication.getLanguage());
                fxmlLoader.setResources(bundle);

                fxmlLoader.setLocation(getClass().getResource(DMSApplication.fxmlPath + "Initialization.fxml"));

                Scene scene = new Scene(fxmlLoader.load());
                ((InitializationController) fxmlLoader.getController()).initSettings(this);
                stage.setTitle(DMSApplication.APP_TITLE);
                stage.setScene(scene);
                stage.showAndWait();
            } catch(IOException e){
                e.printStackTrace();
            }

        }

        public static boolean arePathsSet () {
            String serverPath = preferences.get(SERVER_PATH_PREF, DEFAULT_NULL_VALUE);
            String localPath = preferences.get(LOCAL_PATH_PREF, DEFAULT_NULL_VALUE);
            return (!serverPath.equals(DEFAULT_NULL_VALUE) && !localPath.equals(DEFAULT_NULL_VALUE));
        }

        public static Settings getInstance () {
            if (settings == null)
                settings = new Settings();
            return settings;
        }

        // Returns the absolute path of the main files on the server
        public String getServerDocumentsPath () {
            return serverPath + DOCUMENTS_PATH;
        }

        // Returns the absolute path of the archived files on the server
        public String getServerArchivePath () {
            return serverPath + ARCHIVE_PATH;
        }

        // Returns the absolute path of the application files on the server
        public String getServerAppFilesPath () {
            return serverPath + APP_FILES_PATH;
        }

        // Returns the absolute path of the local file copies
        public String getLocalFilesPath () {
            return localPath + DOCUMENTS_PATH;
        }

        // Returns the absolute path of the application files on the local drive
        public String getLocalAppFilesPath () {
            return localPath + APP_FILES_PATH;
        }

        public String getUsername () {
            return username;
        }

        public void setServerPath (String serverPath){
            this.serverPath = serverPath;
        }

        public void setLocalPath (String localPath){
            this.localPath = localPath;
        }

        public void setUsername (String username){
        this.username = username;
            preferences.put(USERNAME_PREF, username);
        }

    public static String getComputerName()
        {
            Map<String, String> env = System.getenv();
            if (env.containsKey("COMPUTERNAME"))
                return env.get("COMPUTERNAME");
            else return env.getOrDefault("HOSTNAME", "Unknown Computer");
        }

        public String getServerPath () {
            return serverPath;
        }
    }