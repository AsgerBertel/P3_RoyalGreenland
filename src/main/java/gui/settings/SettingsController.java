package gui.settings;

import directory.PreferencesManager;
import gui.DMSApplication;
import gui.TabController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.*;

public class SettingsController implements TabController {

    @FXML
    public TextField serverPathTextField;
    public TextField localPathTextField;
    public TextField usernameTextField;
    public Button saveChangesButton;

    private PreferencesManager preferencesManager;

    private static final String UNSAVED_CHANGE_STYLE_CLASS = "unsaved", ERROR_STYLE_CLASS = "error";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        preferencesManager = PreferencesManager.getInstance();
        usernameTextField.setText(preferencesManager.getUsername());
        serverPathTextField.setText(preferencesManager.getServerPath());
        localPathTextField.setText(preferencesManager.getLocalFilesPath());
        saveChangesButton.setDisable(true);

        // Check validity of changes when
        usernameTextField.setOnKeyReleased(e -> onUserNameChanged());
        serverPathTextField.setOnKeyReleased(e -> onServerPathChanged());
        localPathTextField.setOnKeyReleased(e -> onLocalPathChange());
    }

    @Override
    public void update() {

    }

    public void onBrowseServerPath() {
        File serverFolder = chooseDirectoryPrompt(DMSApplication.getMessage("Settings.PopUp.ChooseServerPath"));
        if (serverFolder != null) {
            serverPathTextField.setText(serverFolder.getPath());
            onServerPathChanged();
        }
    }

    public void onBrowseLocalPath() {
        File serverFolder = chooseDirectoryPrompt(DMSApplication.getMessage("Settings.PopUp.ChooseLocalPath"));
        if (serverFolder != null) {
            serverPathTextField.setText(serverFolder.getPath());
            onLocalPathChange();
        }
    }

    private File chooseDirectoryPrompt(String message) {
        DirectoryChooser fileChooser = new DirectoryChooser();
        fileChooser.setTitle(message);
        File chosenFile = fileChooser.showDialog(new Stage());
        if (chosenFile == null) return null;
        return chosenFile;
    }

    private void onUserNameChanged() {
        verifyNotEmpty(usernameTextField);
    }

    private void onServerPathChanged() {
        // todo What constitutes a valid path? Is it only that it's not empty?? - Magnus
        verifyNotEmpty(serverPathTextField);
    }

    private void onLocalPathChange() {
        // todo What constitutes a valid path? - Magnus
        verifyNotEmpty(localPathTextField);
    }


    private void verifyNotEmpty(TextField textField){
        if (textField.getText().isEmpty()) {
            setStyleClassEnabled(textField, true, ERROR_STYLE_CLASS);
        } else {
            setStyleClassEnabled(textField, true, UNSAVED_CHANGE_STYLE_CLASS);
            setStyleClassEnabled(textField, false, ERROR_STYLE_CLASS);
        }
    }

    public void onEnter() {
        saveChangesButton.requestFocus();
    }

    // Enables/disables a given textfield's style class
    private void setStyleClassEnabled(TextField textField, boolean enabled, String styleClass) {
        if (enabled) {
            // Only add it if it's not already in the list
            if (!textField.getStyleClass().contains(styleClass))
                textField.getStyleClass().add(styleClass);

            saveChangesButton.setDisable(false);
        } else {
            textField.getStyleClass().removeAll(Collections.singleton(styleClass));
        }
    }

    // Called when the saveChangesButton is pressed
    public void onSaveChanges(ActionEvent actionEvent) {
        boolean allChangesSaved = true;

        // todo - Is this readable? - Magnus
        // Save all changes and set allChangeSaved to false if a save failed
        allChangesSaved &= saveChange(usernameTextField, () -> preferencesManager.setUsername(usernameTextField.getText()));
        allChangesSaved &= saveChange(serverPathTextField, () -> preferencesManager.setServerPath(serverPathTextField.getText()));
        allChangesSaved &= saveChange(localPathTextField, () -> preferencesManager.setUsername(usernameTextField.getText()));

        saveChangesButton.setDisable(allChangesSaved);
    }

    /* Executes the saveAction if the textField contains changes
       Aborts save and returns false if the textField contains invalid input */
    private boolean saveChange(TextField textField, Runnable saveAction) {
        if (textField.getStyleClass().contains(ERROR_STYLE_CLASS))
            return false;

        if (textField.getStyleClass().contains(UNSAVED_CHANGE_STYLE_CLASS)) {
            saveAction.run();
            setStyleClassEnabled(textField, false, UNSAVED_CHANGE_STYLE_CLASS);
        }

        return true;
    }

}
