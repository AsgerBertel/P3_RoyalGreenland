package gui;

import directory.Settings;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

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
                DMSApplication.getMessage("Exception.FileNameExists.Title"),
                DMSApplication.getMessage("Exception.FileNameExists.Header"),
                null);
        alert.showAndWait();
    }
    public static void fileNotFoundPopup () {
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
                DMSApplication.getMessage("Exception.IO.Title"),
                DMSApplication.getMessage("Exception.IO.Header"),
                DMSApplication.getMessage("Exception.IO.Context")+"\n"
                        +Settings.getServerErrorLogsPath()+LocalDateTime.now().format(formatter)
        );
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
        alert.setTitle(DMSApplication.getMessage("Exception.InvalidName.Title"));
        alert.setHeaderText(DMSApplication.getMessage("Exception.InvalidName.Header"));
        alert.setContentText(DMSApplication.getMessage("Exception.InvalidName.Context")+"\n"+src+"\n"+target);
        alert.showAndWait();
    }

    /**
     *
     */
    public static void interruptedExceptionPopup(String threadName) {
        buildAlert(
                Alert.AlertType.ERROR,
                DMSApplication.getMessage("Exception.Interrupted.Title"),
                DMSApplication.getMessage("Exception.Interrupted.Header"),
                threadName+ ": "+DMSApplication.getMessage("Exception.Interrupted.Context")
        );
        alert.showAndWait();
    }
    public static void uploadDocumentPopUp() {
        buildAlert(
                Alert.AlertType.INFORMATION,
                DMSApplication.getMessage("FileManager.UploadError.Title"),
                null,
                DMSApplication.getMessage("FileManager.UploadError.Context")
        );
        alert.showAndWait();
    }
    private static void buildAlert (Alert.AlertType type, String title, String header, String context) {
        alert.setAlertType(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(context);
    }
    public static Alert deletePlantPopup(){
        buildAlert(
                Alert.AlertType.CONFIRMATION,
                DMSApplication.getMessage("PlantAdmin.Popup.DeleteTitle"),
                DMSApplication.getMessage("PlantAdmin.Popup.Info"),
                DMSApplication.getMessage("PlantAdmin.Popup.YouSure"));
        ((Button) alert.getDialogPane().lookupButton(ButtonType.OK)).setText(DMSApplication.getMessage("PlantAdmin.Popup.Delete"));
        ((Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL)).setText(DMSApplication.getMessage("PlantAdmin.Popup.Cancel"));
        return alert;
    }

}
