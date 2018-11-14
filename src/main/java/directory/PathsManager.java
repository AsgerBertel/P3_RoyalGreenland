package directory;

import java.util.prefs.Preferences;

public class PathsManager {

    private Preferences preferences;
    private static PathsManager settings;

    private static final String DEFAULT_NULL_VALUE = "null";

    // Keys for retrieving/storing data in preferences
    private static final String SERVER_PATH_PREF = "server_path";
    private static final String LOCAL_PATH_PREF = "local_path";

    // Relative server paths
    private static final String SERVER_MAIN_FILES_PATH = "Documents/";
    private static final String SERVER_ARCHIVE_PATH = "Archive/";
    private static final String SERVER_APP_FILES_PATH = "App Files/";

    // Relative local paths
    private static final String LOCAL_FILES_PATH = "Documents/";
    private static final String LOCAL_APP_FILES_PATH = "App Files/";

    private String serverPath, localPath;

    private PathsManager() {
        // Loads preferences
        preferences = Preferences.userNodeForPackage(PathsManager.class);

        serverPath = preferences.get(SERVER_PATH_PREF, DEFAULT_NULL_VALUE);
        localPath = preferences.get(LOCAL_PATH_PREF, DEFAULT_NULL_VALUE);

        // Add new path values if non exists
        if (true || serverPath.equals(DEFAULT_NULL_VALUE)) { // todo if(true) is obvs temporary
            getNewPaths();
        }
    }

    public void getNewPaths() {
        // temporary hardcoded paths todo Get paths from user input
        serverPath = "Sample files/Server/";
        localPath = "Sample files/Local Disk/";

        preferences.put(SERVER_PATH_PREF, serverPath);
        preferences.put(LOCAL_PATH_PREF, serverPath);
    }

    public static PathsManager getInstance() {
        if (settings == null)
            settings = new PathsManager();
        return settings;
    }

    // Returns the absolute path of the main files on the server
    public String getServerMainFilesPath() {
        return serverPath + SERVER_MAIN_FILES_PATH;
    }

    // Returns the absolute path of the archived files on the server
    public String getServerArchivePath() {
        return serverPath + SERVER_ARCHIVE_PATH;
    }

    // Returns the absolute path of the application files on the server
    public String getServerAppFilesPath() {
        return serverPath + SERVER_APP_FILES_PATH;
    }

    // Returns the absolute path of the local file copies
    public String getLocalFilesPathh() {
        return localPath + LOCAL_FILES_PATH;
    }

    // Returns the absolute path of the application files on the local drive
    public String getLocalAppFilesPath(){ return localPath + LOCAL_APP_FILES_PATH; }


}
