package controller;

import app.ApplicationMode;
import model.managing.FileManager;
import model.managing.SettingsManager;
import model.managing.PlantManager;
import gui.AlertBuilder;
import app.DMSApplication;
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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class SettingsController implements TabController {

    @FXML
    private TextField serverPathTextField;
    private TextField localPathTextField;
    private TextField usernameTextField;
    private Button saveChangesButton;

    private static final String UNSAVED_CHANGE_STYLE_CLASS = "unsaved";
    public static final String ERROR_STYLE_CLASS = "error";
    private ToggleButton greenlandicSettingsButton;
    private ToggleButton danishSettingsButton;
    private final ToggleGroup languageGroup = new ToggleGroup();

    private DMSApplication dmsApplication;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        saveChangesButton.setDisable(true);

        // Check validity of changes when
        usernameTextField.setOnKeyReleased(e -> onUserNameChanged());
        serverPathTextField.setOnKeyReleased(e -> onServerPathChanged());
        // Local path is not used in the admin mode.
        if(DMSApplication.getApplicationMode() != ApplicationMode.ADMIN){
            localPathTextField.setOnKeyReleased(e -> onLocalPathChange());
        }

        greenlandicSettingsButton.setToggleGroup(languageGroup);
        danishSettingsButton.setToggleGroup(languageGroup);
    }

    @Override
    public void initReference(DMSApplication dmsApplication) {
        this.dmsApplication = dmsApplication;
    }

    @Override
    public void update() {
        usernameTextField.setText(SettingsManager.getUsername());
        serverPathTextField.setText(SettingsManager.getServerPath().toString());
        // Local path is not used in the admin mode.
        if(DMSApplication.getApplicationMode() != ApplicationMode.ADMIN){
            localPathTextField.setText(SettingsManager.getLocalPath().toString());
        }
        if (DMSApplication.getLanguage().equals(DMSApplication.DK_LOCALE))
            languageGroup.selectToggle(danishSettingsButton);
        else
            languageGroup.selectToggle(greenlandicSettingsButton);

    }

    public void onBrowseServerPath() {
        File serverFolder = chooseDirectoryPrompt(DMSApplication.getMessage("Settings.PopUp.ChooseServerPath"), Paths.get(serverPathTextField.getText()).toFile());
        if (serverFolder != null) {
            serverPathTextField.setText(serverFolder.getPath());
            onServerPathChanged();
        }
    }

    public void onBrowseLocalPath() {
        File serverFolder = chooseDirectoryPrompt(DMSApplication.getMessage("Settings.PopUp.ChooseLocalPath"), Paths.get(localPathTextField.getText()).toFile());
        if (serverFolder != null) {
            localPathTextField.setText(serverFolder.getPath());
            onLocalPathChange();
        }
    }

    static File chooseDirectoryPrompt(String message, File initialDirectory) {
        DirectoryChooser fileChooser = new DirectoryChooser();
        if (initialDirectory.exists())
            fileChooser.setInitialDirectory(initialDirectory);
        fileChooser.setTitle(message);
        File chosenFile = fileChooser.showDialog(new Stage());
        if (chosenFile == null) return null;
        return chosenFile;
    }

    private void onUserNameChanged() {
        verifyNotEmpty(usernameTextField);
    }

    private void onServerPathChanged() {
        verifyNotEmpty(serverPathTextField);
    }

    private void onLocalPathChange() {
        verifyNotEmpty(localPathTextField);
    }


    private void verifyNotEmpty(TextField textField) {
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

            if (!containsErrors()) {
                saveChangesButton.setDisable(false);
            }
        } else {
            textField.getStyleClass().removeAll(Collections.singleton(styleClass));
        }
    }

    // Called when the saveChangesButton is pressed
    public void onSaveChanges(ActionEvent actionEvent) {
        boolean allChangesSaved = true;

        if(!isValidPath(serverPathTextField.getText())){
            AlertBuilder.customErrorPopUp(DMSApplication.getMessage("Settings.PopUp.InvalidPathTitle"), "", DMSApplication.getMessage("Settings.PopUp.InvalidPathContext"));
            return;
        }

        // Save all changes and set allChangeSaved to false if a save failed
        allChangesSaved &= saveChange(usernameTextField, () -> SettingsManager.setUsername(usernameTextField.getText()));
        allChangesSaved &= saveChange(serverPathTextField, () -> SettingsManager.setServerPath(Paths.get(serverPathTextField.getText())));

        if(DMSApplication.getApplicationMode() != ApplicationMode.ADMIN)
            allChangesSaved &= saveChange(localPathTextField, () -> SettingsManager.setLocalPath(Paths.get(localPathTextField.getText())));


        // Only disable save button if all changes are saved correctly
        saveChangesButton.setDisable(allChangesSaved);
        FileManager.resetInstance();
        PlantManager.resetInstance();
        // Save language if different from the current language
        Locale language = danishSettingsButton.isSelected() ? DMSApplication.DK_LOCALE : DMSApplication.GL_LOCALE;
        if (!language.equals(DMSApplication.getLanguage()))
            dmsApplication.changeLanguage(language);
        else
            update();

        dmsApplication.initializeApplication();
    }

    private static boolean isValidPath(String path) {
        if(path == null || path.isEmpty()) return false;
        return Files.exists(SettingsManager.completeApplicationPath(Paths.get(path)).getParent());
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

    private boolean containsErrors() {
        if(DMSApplication.getApplicationMode() != ApplicationMode.ADMIN){
            return usernameTextField.getStyleClass().contains(ERROR_STYLE_CLASS)
                    || serverPathTextField.getStyleClass().contains(ERROR_STYLE_CLASS)
                    || localPathTextField.getStyleClass().contains(ERROR_STYLE_CLASS);
        } else {
            return usernameTextField.getStyleClass().contains(ERROR_STYLE_CLASS)
                    || serverPathTextField.getStyleClass().contains(ERROR_STYLE_CLASS);
        }
    }


    public void changeToDanish(ActionEvent actionEvent) {
        if (DMSApplication.getLanguage().equals(DMSApplication.GL_LOCALE))
            saveChangesButton.setDisable(false);
        danishSettingsButton.setSelected(true);
        greenlandicSettingsButton.setSelected(false);
    }

    public void changeToGreenlandic(ActionEvent actionEvent) {
        if (DMSApplication.getLanguage().equals(DMSApplication.DK_LOCALE))
            saveChangesButton.setDisable(false);
        greenlandicSettingsButton.setSelected(true);
        danishSettingsButton.setSelected(false);
    }
}
