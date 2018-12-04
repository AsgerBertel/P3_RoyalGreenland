package gui;

import directory.Settings;
import javafx.scene.control.Alert;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AlertBuilder
{
    private static Alert alert = new Alert(Alert.AlertType.NONE);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Displays a WARNING popup informing the user that a given file already exists. Used in context of Files.exists()
     */
    public static void fileAlreadyExistsPopUp() {
        buildAlert(
                Alert.AlertType.WARNING,
                DMSApplication.getMessage("FileManager.fileNameExistsPopUp.Title"),
                DMSApplication.getMessage("FileManager.fileNameExistsPopUp.Header"),
                null);
        alert.showAndWait();
    }
    public static void fileNotFound() {
        buildAlert(
                Alert.AlertType.ERROR,
                DMSApplication.getMessage("Exception.fileNotFound.Title"),
                DMSApplication.getMessage("Exception.fileNotFound.Header"),
                null);
        alert.showAndWait();
    }

    /**
     * Displays an ERROR popup informing the user of an IOException. Ressource context tells of plausible causes.
     * Displays the given filepath of the error report.
     * Used in context of IOException and in cohesion with LoggingErrorTools of the throwable.
     */
    public static void IOExceptionPopUp() {
        buildAlert(
                Alert.AlertType.ERROR,
                DMSApplication.getMessage("FileManager.IOException.Title"),
                DMSApplication.getMessage("FileManager.IOException.Header"),
                DMSApplication.getMessage("FileManager.IOException.Context")+"\n"
                        +Settings.getServerErrorLogsPath()+LocalDateTime.now().format(formatter));
        alert.showAndWait();
    }

    /**
     * Displays an ERROR popup telling the user incase of InvalidNameException. Primarily in context of Files.move()
     * and Files.copy(), incase file already exists in the target path, or named an illegal name. Parameters consist of
     * the source path and target path.
     * @param src
     * @param target
     */
    @Deprecated
    public static void invalidNameExceptionPopUp (String src, String target) {
        alert.setAlertType(Alert.AlertType.ERROR);
        alert.setTitle(DMSApplication.getMessage("FileManager.InvalidNameException.Title"));
        alert.setHeaderText(DMSApplication.getMessage("FileManager.InvalidNameException.Header"));
        alert.setContentText(DMSApplication.getMessage("FileManager.InvalidNameException.Context")+"\n"+src+"\n"+target);
        alert.showAndWait();
    }

    /**
     *
     */
    public static void interruptedExceptionPopup(String threadName) {
        buildAlert(
                Alert.AlertType.ERROR,
                DMSApplication.getMessage("FileManager.InterruptedException.Title"),
                DMSApplication.getMessage("FileManager.InterruptedException.Header"),
                threadName+ ": "+DMSApplication.getMessage("FileManager.InterruptedException.Context"));
        alert.showAndWait();
    }
    public static void uploadDocumentPopUp() {
        buildAlert(
                Alert.AlertType.INFORMATION,
                DMSApplication.getMessage("FileManager.UploadError.Title"),
                null,
                DMSApplication.getMessage("FileManager.UploadError.Context"));
        alert.showAndWait();
    }
    private static void buildAlert (Alert.AlertType type, String title, String header, String context) {
        alert.setAlertType(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(context);
    }

}
