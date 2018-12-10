package gui;

import directory.SettingsManager;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import java.nio.file.Paths;
import java.util.Locale;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

public class SettingsTabTest extends GUITest{

    @BeforeEach
    void setTab() throws InterruptedException {
        clickOn((ToggleButton)findNode("#settingsButton"));
    }

    @RepeatedTest(value = 2)
    void textFieldsTest() {
        final String TEST_TEXT = "TEST";

        TextField serverField = findNode("#serverPathTextField");
        TextField localField = findNode("#localPathTextField");
        TextField userField = findNode("#usernameTextField");

        enterText(serverField, TEST_TEXT);
        enterText(localField, TEST_TEXT);
        enterText(userField, TEST_TEXT);

        assertEquals(serverField.getText(), TEST_TEXT);
        assertEquals(localField.getText(), TEST_TEXT);
        assertEquals(userField.getText(), TEST_TEXT);
    }

    private void enterText(Control clickAbleElement, String text){
        clickOn(clickAbleElement);
        press(KeyCode.CONTROL);
        press(KeyCode.A);
        release(new KeyCode[]{});
        push(KeyCode.DELETE);
        write(text);
    }

    @RepeatedTest(value = 2)
    void enableSaveButtonTest(){
        Button saveButton = findNode("#saveChangesButton");
        assertTrue(saveButton.isDisabled());

        TextField serverField = findNode("#serverPathTextField");
        enterText(serverField, serverField.getText());
        assertFalse(saveButton.isDisabled());

        clickOn(saveButton);
        assertTrue(saveButton.isDisabled());
    }

    @RepeatedTest(value = 2)
    void saveChangesTest(){
        TextField serverField = findNode("#serverPathTextField");
        TextField localField = findNode("#localPathTextField");
        TextField userField = findNode("#usernameTextField");

        saveTextValueTest(serverField, Paths.get("TestFolder/server/" + DMSApplication.APP_TITLE).toString(),
                () -> SettingsManager.getServerPath().toString());
        saveTextValueTest(localField, Paths.get("TestFolder/local/" + DMSApplication.APP_TITLE).toString(),
                () -> SettingsManager.getLocalPath().toString());
        saveTextValueTest(userField, "SorenSmoke", () -> SettingsManager.getUsername());
    }


    void saveTextValueTest(TextField textField, String newValue, Supplier<String> getSettingsValue){
        String defaultValue = getSettingsValue.get();
        Button saveButton = findNode("#saveChangesButton");

        enterText(textField, newValue);
        clickOn(saveButton);
        assertEquals(newValue, getSettingsValue.get());

        enterText(textField, defaultValue);
        clickOn(saveButton);
        assertEquals(defaultValue, getSettingsValue.get());
    }

    @RepeatedTest(value = 2)
    void changeLanguageTest() throws InterruptedException {
        ToggleButton greenlandicButton = findNode("#greenlandicSettingsButton");
        ToggleButton danishButton = findNode("#danishSettingsButton");
        Button saveButton = findNode("#saveChangesButton");

        ToggleButton oldLanguageButton;
        ToggleButton newLanguageButton;
        Locale oldLanguage;
        Locale newLanguage;

        if(DMSApplication.getLanguage().equals(DMSApplication.GL_LOCALE)){
            oldLanguage = DMSApplication.GL_LOCALE;
            oldLanguageButton = greenlandicButton;
            newLanguage = DMSApplication.DK_LOCALE;
            newLanguageButton = danishButton;
        }else{
            oldLanguage = DMSApplication.DK_LOCALE;
            oldLanguageButton = danishButton;
            newLanguage = DMSApplication.GL_LOCALE;
            newLanguageButton = greenlandicButton;
        }

        assertTrue(oldLanguageButton.isSelected());
        assertFalse(newLanguageButton.isSelected());

        clickOn(newLanguageButton);

        assertTrue(newLanguageButton.isSelected());
        assertFalse(oldLanguageButton.isSelected());

        assertEquals(DMSApplication.getLanguage(), oldLanguage);
        clickOn(saveButton);
        assertEquals(DMSApplication.getLanguage(), newLanguage);
    }

    @RepeatedTest(value = 2)
    void noChangeLanguageTest() throws InterruptedException {
        ToggleButton greenlandicButton = findNode("#greenlandicSettingsButton");
        ToggleButton danishButton = findNode("#danishSettingsButton");
        Button saveButton = findNode("#saveChangesButton");

        assertTrue(saveButton.isDisabled());

        if(DMSApplication.getLanguage().equals(DMSApplication.GL_LOCALE)){
            assertTrue(greenlandicButton.isSelected());
            clickOn(greenlandicButton);
            assertTrue(greenlandicButton.isSelected());
            assertEquals(DMSApplication.getLanguage(), DMSApplication.GL_LOCALE);
        }else{
            assertTrue(danishButton.isSelected());
            clickOn(danishButton);
            assertTrue(danishButton.isSelected());
            assertEquals(DMSApplication.getLanguage(), DMSApplication.DK_LOCALE);
        }

        assertTrue(saveButton.isDisabled());
    }

    @RepeatedTest(value = 2)
    void defaultValueTest(){
        TextField serverField = findNode("#serverPathTextField");
        TextField localField = findNode("#localPathTextField");
        TextField userField = findNode("#usernameTextField");
        ToggleButton greenlandicButton = findNode("#greenlandicSettingsButton");
        ToggleButton danishButton = findNode("#danishSettingsButton");

        assertEquals(serverField.getText(), SettingsManager.getServerPath().toString());
        assertEquals(localField.getText(), SettingsManager.getLocalPath().toString());
        assertEquals(userField.getText(), SettingsManager.getUsername());

        if(DMSApplication.getLanguage().equals(DMSApplication.DK_LOCALE)){
            assertTrue(danishButton.isSelected());
            assertFalse(greenlandicButton.isSelected());
        }else{
            assertTrue(greenlandicButton.isSelected());
            assertFalse(danishButton.isSelected());
        }
    }





}
