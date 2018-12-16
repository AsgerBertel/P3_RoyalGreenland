package controller;

import app.ApplicationMode;
import model.managing.SettingsManager;
import app.DMSApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class InitializationController implements Initializable {

    @FXML
    private TextField inputTextField;
    private Button browseButton;
    private Text errorText;
    private Text settingDescription;
    private Button nextButton;
    private Button previousButton;

    private State currentState;

    private String serverPath = "";
    private String localPath = "";
    private String userName = SettingsManager.getUsername();

    private enum State {
        LOCAL_PATH,
        SERVER_PATH,
        USERNAME
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (DMSApplication.getApplicationMode().equals(ApplicationMode.ADMIN)) {
            setState(State.SERVER_PATH);
        } else {
            setState(State.LOCAL_PATH);
        }
    }

    private void setState(State state) {
        this.currentState = state;
        errorText.setText("");
        inputTextField.clear();
        removeError();
        switch (state) {
            case LOCAL_PATH:
                settingDescription.setText(DMSApplication.getMessage("Initialization.EnterLocalPath"));
                inputTextField.setText(localPath);
                previousButton.setVisible(false);
                browseButton.setVisible(true);
                break;
            case SERVER_PATH:
                settingDescription.setText(DMSApplication.getMessage("Initialization.EnterServerPath"));
                inputTextField.setText(serverPath);

                // Only show "previousButton" if the application is running in viewer mod (as the local path setting
                // is irrelevant when running in admin mode)
                if (DMSApplication.getApplicationMode().equals(ApplicationMode.ADMIN))
                    previousButton.setVisible(false);
                else
                    previousButton.setVisible(true);
                browseButton.setVisible(true);
                break;
            case USERNAME:
                settingDescription.setText(DMSApplication.getMessage("Initialization.EnterUsername"));
                inputTextField.setText(userName);
                browseButton.setVisible(false);
                previousButton.setVisible(true);
                break;
        }
    }

    private void removeError() {
        errorText.setText("");
        inputTextField.getStyleClass().removeAll(SettingsController.ERROR_STYLE_CLASS);
    }

    private void showError(String message) {
        inputTextField.getStyleClass().add(SettingsController.ERROR_STYLE_CLASS);
        errorText.setText(message);
    }

    public void onBrowsePath(ActionEvent actionEvent) {
        String message;
        if (currentState == State.SERVER_PATH) {
            message = DMSApplication.getMessage("Initialization.EnterServerPath");
        } else {
            message = DMSApplication.getMessage("Initialization.EnterLocalPath");
        }


        File chosenDirectory = SettingsController.chooseDirectoryPrompt(message, Paths.get(inputTextField.getText()).toFile());

        if (chosenDirectory != null) {
            inputTextField.setText(chosenDirectory.getAbsolutePath());
            removeError();
        } else if (!isLegalDirectory(chosenDirectory)) {
            showError(DMSApplication.getMessage("Initialization.IllegalChosenFile"));
        } else {
            showError(DMSApplication.getMessage("Initialization.EmptyInput"));
        }
    }

    public void onPrevious(ActionEvent actionEvent) {
        boolean saveInput = !(inputTextField.getText() == null) && !(inputTextField.getText().isEmpty());

        if (currentState == State.SERVER_PATH) {
            if (saveInput) serverPath = inputTextField.getText();
            setState(State.LOCAL_PATH);
        } else if (currentState == State.USERNAME) {
            if (saveInput) userName = inputTextField.getText();
            setState(State.SERVER_PATH);
        }
    }

    public void onNext(ActionEvent actionEvent) {
        if (inputTextField.getText() == null || inputTextField.getText().isEmpty()) {
            showError(DMSApplication.getMessage("Initialization.EmptyInput"));
            return;
        }

        if (currentState == State.LOCAL_PATH) {
            localPath = inputTextField.getText();
            setState(State.SERVER_PATH);
        } else if (currentState == State.SERVER_PATH) {
            serverPath = inputTextField.getText();
            setState(State.USERNAME);
        } else {
            userName = inputTextField.getText();
            close();
        }
    }

    // Close the initialization window
    private void close() {
        SettingsManager.setServerPath(Paths.get(serverPath));
        SettingsManager.setUsername(userName);

        // Save local path if the application is running in viewer mode
        if (DMSApplication.getApplicationMode().equals(ApplicationMode.VIEWER))
            SettingsManager.setLocalPath(Paths.get(localPath));

        // Close initialization window
        ((Stage) nextButton.getScene().getWindow()).close();
    }
    private boolean isLegalDirectory(File file) {
        boolean isNull = file != null;
        return file.isDirectory() && !file.isHidden() && file.exists() && file.canRead() && file.canWrite() && !isNull;
    }
}
