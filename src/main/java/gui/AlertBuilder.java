package gui;

import app.DMSApplication;
import log.LoggingErrorTools;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AlertBuilder {
    private static Alert alert = new Alert(Alert.AlertType.NONE);

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
    public static void updateFailExceptionPopUp () {
        buildAlert(Alert.AlertType.INFORMATION,
                DMSApplication.getMessage("Exception.UpdateFailException.Title"),
                DMSApplication.getMessage("Exception.UpdateFailException.Header"),
                DMSApplication.getMessage("Exception.UpdateFailException.Context"+"\n"
                        +LoggingErrorTools.generateWritePath(LocalDateTime.now()))
        );
        alert.showAndWait();
    }
    public static void illegalFileExceptionPopUp (String targetPath) {
        buildAlert(Alert.AlertType.ERROR,
                DMSApplication.getMessage("Exception.IllegalFileException.Title"),
                DMSApplication.getMessage("Exception.IllegalFileException.Header"),
                DMSApplication.getMessage("Exception.IllegalFileException.Context1")
                        +targetPath
                        +LoggingErrorTools.generateWritePath(LocalDateTime.now())
                        +DMSApplication.getMessage("Exception.IllegalFileException.Context2")
        );
        alert.showAndWait();
    }

    public static void fileNotFoundPopUp () {
        buildAlert(
                Alert.AlertType.ERROR,
                DMSApplication.getMessage("Exception.FileNotFound.Title"),
                DMSApplication.getMessage("Exception.FileNotFound.Header"),
                null);
        alert.showAndWait();
    }

    public static void fileNotFoundPopUp (String customMsg) {
        buildAlert(
                Alert.AlertType.ERROR,
                DMSApplication.getMessage("Exception.FileNotFound.Title"),
                customMsg,
                null);
        alert.showAndWait();
    }

    public static void PreferencesNotResetPopup() {
        buildAlert(
                Alert.AlertType.ERROR,
                DMSApplication.getMessage("Exception.PreferencesNotReset.Title"),
                DMSApplication.getMessage("Exception.PreferencesNotReset.Header"),
                null);
        alert.showAndWait();
    }

    public static void programRestartPopUp() {
        buildAlert(Alert.AlertType.INFORMATION,
                DMSApplication.getMessage("DMSApplication.restart.Title"),
                null,
                DMSApplication.getMessage("DMSApplication.restart.Context")
        );
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
                DMSApplication.getMessage("Exception.IO.Context") + "\n"
                        +LoggingErrorTools.generateWritePath(LocalDateTime.now())
        );
        alert.showAndWait();
    }
    /**
     * Identical to IOExceptionPopup(), however displays the path of the file for which the IOException occurred.
     * @param targetPath
     */
    public static void IOExceptionPopUpWithString (String targetPath) {
        buildAlert(Alert.AlertType.ERROR,
                DMSApplication.getMessage("Exception.IO.Title"),
                DMSApplication.getMessage("Exception.IO.Header"),
                DMSApplication.getMessage("Exception.IO.Target.Context1")+targetPath+"\n"
                        + DMSApplication.getMessage("Exception.IO.Target.Context2")
                        + LoggingErrorTools.generateWritePath(LocalDateTime.now())
        );
        alert.showAndWait();
    }

    public static void customErrorPopUp(String customTitle, String customHeader, String customContext) {
        buildAlert(Alert.AlertType.ERROR, customTitle, customHeader,customContext);
        alert.showAndWait();
    }

    /**
     * Displays an ERROR popup telling the user in case of InvalidNameException. Primarily in context of Files.move()
     * and Files.copy(), in case file already exists in the target path, or named an illegal name. Parameters consist of
     * the source path and target path.
     * @param src
     * @param target
     */
    @Deprecated
    public static void invalidNameExceptionPopUp(String src, String target) {
        alert.setAlertType(Alert.AlertType.ERROR);
        alert.setTitle(DMSApplication.getMessage("Exception.InvalidName.Title"));
        alert.setHeaderText(DMSApplication.getMessage("Exception.InvalidName.Header"));
        alert.setContentText(DMSApplication.getMessage("Exception.InvalidName.Context") + "\n" + src + "\n" + target);
        alert.showAndWait();
    }

    /**
     *
     */
    public static void interruptedExceptionPopUp (String threadName) {
        buildAlert(
                Alert.AlertType.ERROR,
                DMSApplication.getMessage("Exception.Interrupted.Title"),
                DMSApplication.getMessage("Exception.Interrupted.Header"),
                threadName + ": " + DMSApplication.getMessage("Exception.Interrupted.Context")
        );
        alert.showAndWait();
    }
    public static void interruptedExceptionShutdownPopUp (String threadName) {
        buildAlert(
                Alert.AlertType.ERROR,
                DMSApplication.getMessage("Exception.Interrupted.Shutdown.Title"),
                DMSApplication.getMessage("Exception.Interrupted.Shutdown.Header"),
                threadName + ": " + DMSApplication.getMessage("Exception.Interrupted.Shutdown.Context")
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

    public static Alert deletePlantPopUp () {
        buildAlert(
                Alert.AlertType.CONFIRMATION,
                DMSApplication.getMessage("PlantAdmin.Popup.DeleteTitle"),
                DMSApplication.getMessage("PlantAdmin.Popup.Info"),
                DMSApplication.getMessage("PlantAdmin.Popup.YouSure"));
        ((Button) alert.getDialogPane().lookupButton(ButtonType.OK)).setText(DMSApplication.getMessage("PlantAdmin.Popup.Delete"));
        ((Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL)).setText(DMSApplication.getMessage("PlantAdmin.Popup.Cancel"));
        return alert;
    }

    private static void buildAlert(Alert.AlertType type, String title, String header, String context) {
        alert.setAlertType(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(context);
    }

}
