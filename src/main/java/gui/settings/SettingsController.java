package gui.settings;

import directory.Settings;
import gui.DMSApplication;
import gui.TabController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
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

    public static final String UNSAVED_CHANGE_STYLE_CLASS = "unsaved", ERROR_STYLE_CLASS = "error";
    public ToggleButton changeToGreenlandic;
    public ToggleButton changeToDanish;
    private ToggleGroup languageGroup = new ToggleGroup();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        usernameTextField.setText(Settings.getUsername());
        serverPathTextField.setText(Settings.getServerPath());
        localPathTextField.setText(Settings.getLocalFilesPath());
        saveChangesButton.setDisable(true);

        changeToGreenlandic.setToggleGroup(languageGroup);
        changeToDanish.setToggleGroup(languageGroup);

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

    static File chooseDirectoryPrompt(String message) {
        DirectoryChooser fileChooser = new DirectoryChooser();
        fileChooser.setTitle(message);
        File chosenFile = fileChooser.showDialog(new Stage());
        if (chosenFile == null) return null;
        return chosenFile;
    }

    public static boolean isValidPath(String path){
        return path != null && !path.isEmpty();
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

            if(!containsErrors()){
                saveChangesButton.setDisable(false);
            }
        } else {
            textField.getStyleClass().removeAll(Collections.singleton(styleClass));
        }
    }

    // Called when the saveChangesButton is pressed
    public void onSaveChanges(ActionEvent actionEvent) {
        boolean allChangesSaved = true;

        // todo - Is this readable? - Magnus
        // Save all changes and set allChangeSaved to false if a save failed
        allChangesSaved &= saveChange(usernameTextField, () -> Settings.setUsername(usernameTextField.getText()));
        allChangesSaved &= saveChange(serverPathTextField, () -> Settings.setServerPath(serverPathTextField.getText()));
        allChangesSaved &= saveChange(localPathTextField, () -> Settings.setUsername(usernameTextField.getText()));

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

    protected boolean containsErrors(){
        return usernameTextField.getStyleClass().contains(ERROR_STYLE_CLASS)
                || serverPathTextField.getStyleClass().contains(ERROR_STYLE_CLASS)
                || localPathTextField.getStyleClass().contains(ERROR_STYLE_CLASS);
    }


    public void changeToDanish(ActionEvent actionEvent) {

    }

    public void changeToGreenlandic(ActionEvent actionEvent) {

    }
}
