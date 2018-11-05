package directory;

import java.util.prefs.Preferences;

public class PathsManager {

    private Preferences preferences;
    private static PathsManager settings;

    // Keys for retrieving/storing data in preferences
    private static final String SERVER_PATH_PREF = "server_path";
    private static final String LOCAL_PATH_PREF = "local_path";

    private static final String DEFAULT_NULL_VALUE = "null";

    // Relative server paths
    private static final String SERVER_MAIN_FILES_PAT = "Documents/";
    private static final String SERVER_ARCHIVE_PAT = "Archive/";

    // Relative local paths
    private static final String LOCAL_FILES_PATH = "Documents/";

    // Temporary
    private static final String JAR_PATH = PathsManager.class.getProtectionDomain().getCodeSource().getLocation().getPath();

    private String serverPath, localPath;

    private PathsManager(){
        // Loads preferences
        preferences = Preferences.userNodeForPackage(PathsManager.class);

        serverPath = preferences.get(SERVER_PATH_PREF, DEFAULT_NULL_VALUE);
        localPath = preferences.get(LOCAL_PATH_PREF, DEFAULT_NULL_VALUE);

        // Add new path values if non exists
        if(serverPath.equals(DEFAULT_NULL_VALUE)){
            // temporary hardcoded paths todo Get paths from user input
            serverPath = JAR_PATH + "Sample files/Server/";
            localPath = JAR_PATH + "Sample files/Local Disk/";

            preferences.put(SERVER_PATH_PREF, serverPath);
            preferences.put(LOCAL_PATH_PREF, serverPath);
        }
    }

    public static PathsManager getInstance(){
        if(settings == null)
            settings = new PathsManager();
        return settings;
    }

    // Returns the absolute path of the main files on the server
    public String getServerMainFilesPath() {
        return serverPath + SERVER_MAIN_FILES_PAT;
    }

    // Returns the absolute path of the archived files on the server
    public String getServerArchivePath() {
        return serverPath + SERVER_ARCHIVE_PAT;
    }

    // Returns the absolute path of the local file copies
    public String getLocalFilesPathh() {
        return localPath + LOCAL_FILES_PATH;
    }

}
