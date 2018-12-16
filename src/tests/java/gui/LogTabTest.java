package gui;

import app.DMSApplication;
import model.managing.FileManager;
import model.managing.SettingsManager;
import model.AbstractFile;
import model.Document;
import model.AccessModifier;
import model.Plant;
import model.managing.PlantManager;
import controller.LogController;
import gui.custom_node.PlantElement;
import log.LogEventType;
import log.LogManager;
import controller.PlantAdministrationController;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import io.json.AppFilesManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import util.TestUtil;
import log.LogEvent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;

class LogTabTest extends GUITest {
    private TreeView<AbstractFile> fileTree;
    private TreeItem<AbstractFile> treeItem;
    private Button btn;
    private LogController logController;
    private Document doc;
    private LogEvent latestChange;

    @BeforeEach
    void setup() throws IOException {
        //clear previous work
        resetFiles();

        //Switch tab to Log
        clickOn((ToggleButton)findNode("#logButton"));

        //set logController
        logController = (LogController) dmsApplication.getCurrentTab().getTabController();
        logController.update();

    }

    private static TreeCell<AbstractFile> getTreeCell(TreeView<AbstractFile> tree, TreeItem<AbstractFile> treeItem){
        Set<Node> treeCells = tree.lookupAll(".tree-cell");
        List<Node> cells = new ArrayList<>(treeCells);
        int row = tree.getRow(treeItem);
        return ((TreeCell<AbstractFile>) cells.get(row));
    }
    private void resetFiles() throws IOException{
        SettingsManager.setServerPath(TestUtil.getTestServerDocuments());
        SettingsManager.setLocalPath(TestUtil.getTestLocalDocuments());
        TestUtil.resetTestFiles();
        FileManager.resetInstance();
        PlantManager.resetInstance();
        AppFilesManager.createServerDirectories();
        AppFilesManager.createLocalDirectories();
        new File("TestingFiles/Server/RG DMS/Working Directory/App Files/logs.log");
    }

    @SuppressWarnings("Duplicates")
    private void enterText(Control clickAbleElement, String text){
        clickOn(clickAbleElement);
        if(System.getProperty("os.name").contains("Mac")){
            press(KeyCode.COMMAND);
        } else{
            press(KeyCode.CONTROL);
        }
        press(KeyCode.A);
        release(new KeyCode[]{});
        push(KeyCode.DELETE);
        write(text);
    }
    private void populateLog() {
        String user1 = "Kek", user2 = "Top", user3 = "eksD";
        for(int i =0; i<1; i++ ){
            LogManager.log(new LogEvent("prefix" + i, "suffix" + i,user1, LocalDateTime.now(), LogEventType.CREATED));
        }
        for(int i =0; i<1; i++ ){
            LogManager.log(new LogEvent("fixpre" + i, "suffix" + i,user2, LocalDateTime.now(), LogEventType.RENAMED));
        }

        for(int i =0; i<1; i++ ){
            LogEvent e = new LogEvent("prefix" + i, "fixsuf" + i,user3, LocalDateTime.now(), LogEventType.ARCHIVED);
            LogManager.log(e);
            //Save latestChange for time sort test
            latestChange = e;
        }
        logController.update();
    }

    private boolean isEventTypePresent(LogEventType eventType){
        logController.update();
        for(LogEvent e : logController.listOfEvents){
            if(e.getEventType() == eventType){
                return true;
            }
        }
        return false;
    }

    @RepeatedTest(value = 2)
    void makeCreatedEvent(){
        List<LogEventType> listOfTypes = new ArrayList<>();
        LogManager.log(new LogEvent("TestCreatedEVENT", LogEventType.CREATED));
        assertTrue(isEventTypePresent(LogEventType.CREATED));
    }
    @RepeatedTest(value = 2)
    void makeArchivedEvent(){
        clickOn((ToggleButton) findNode("#administrateDocumentsButton"));
        fileTree = findNode("#fileTreeView");
        treeItem = fileTree.getRoot().getChildren().get(0);
        clickOn(getTreeCell(fileTree,treeItem));
        clickOn((Button)findNode("#deleteFileButton"));
        assertTrue(isEventTypePresent(LogEventType.ARCHIVED));
    }

    @RepeatedTest(value = 2)
    void makeChangedEvent() throws IOException, InterruptedException {
        clickOn((ToggleButton) findNode("#administrateDocumentsButton"));
        fileTree = findNode("#fileTreeView");
        treeItem = fileTree.getRoot().getChildren().get(1);
        doubleClickOn(getTreeCell(fileTree,treeItem));
        //get child which is a file
        TreeItem<AbstractFile> selectedFile = fileTree.getRoot().getChildren().get(1).getChildren().get(3);

        Path p1 = Paths.get(SettingsManager.getServerDocumentsPath().toString(),selectedFile.getValue().getOSPath().toString());
        Files.setLastModifiedTime(p1, FileTime.from(Instant.now()));
        //sleep to wait for listener to update
        TimeUnit.SECONDS.sleep(3);
        assertTrue(isEventTypePresent(LogEventType.CHANGED));
    }

    @RepeatedTest(value = 2)
    void makeRenamedEvent() {
        clickOn((ToggleButton) findNode("#administrateDocumentsButton"));
        fileTree = findNode("#fileTreeView");
        treeItem = fileTree.getRoot().getChildren().get(1);
        doubleClickOn(getTreeCell(fileTree,treeItem));
        //get child which is a file
        TreeItem<AbstractFile> selectedFile = fileTree.getRoot().getChildren().get(1).getChildren().get(3);
        rightClickOn(getTreeCell(fileTree,selectedFile));
        clickOnContextMenuItem(1);
        write("testFile");
        clickOn(DMSApplication.getMessage("AdminFiles.PopUpRename.NewName"));
        assertTrue(isEventTypePresent(LogEventType.RENAMED));
    }

    @RepeatedTest(value = 2)
    void makeFolderRenamedEvent(){
        clickOn((ToggleButton) findNode("#administrateDocumentsButton"));
        fileTree = findNode("#fileTreeView");
        treeItem = fileTree.getRoot().getChildren().get(1);
        rightClickOn(getTreeCell(fileTree,treeItem));
        clickOnContextMenuItem(1);
        write("testFolder");
        clickOn(DMSApplication.getMessage("AdminFiles.PopUpRename.NewName"));

        assertTrue(isEventTypePresent(LogEventType.FOLDER_RENAMED));
    }

    @RepeatedTest(value = 2)
    void makeChangesPublishedEvent(){
        //Cause event to publish
        clickOn((ToggleButton) findNode("#administrateDocumentsButton"));
        fileTree = findNode("#fileTreeView");
        treeItem = fileTree.getRoot().getChildren().get(1);
        clickOn(getTreeCell(fileTree,treeItem));
        clickOn((Button)findNode("#deleteFileButton"));

        //Click on publish button
        clickOn((Button) findNode("#publishChangesButton"));
        assertTrue(isEventTypePresent(LogEventType.CHANGES_PUBLISHED));

    }

    @RepeatedTest(value = 2)
    void makeRestoredEvent(){
        //Archive event
        clickOn((ToggleButton) findNode("#administrateDocumentsButton"));
        fileTree = findNode("#fileTreeView");
        treeItem = fileTree.getRoot().getChildren().get(1);
        clickOn(getTreeCell(fileTree,treeItem));
        clickOn((Button)findNode("#deleteFileButton"));

        //Restore event
        clickOn((ToggleButton) findNode("#archiveButton"));
        TreeView<AbstractFile>secondTree = findNode("#Filetree");
        TreeItem<AbstractFile>secondItem = secondTree.getRoot().getChildren().get(0);
        clickOn(getTreeCell(secondTree,secondItem));
        clickOn((Button)findNode("#btnRestore"));
        assertTrue(isEventTypePresent(LogEventType.RESTORED));
    }

    @RepeatedTest(value = 2)
    void makePlantCreatedEvent(){
        //Navigate to Plant tab
        clickOn((ToggleButton) findNode("#administratePlantsButton"));
        //Click on add plant
        clickOn((Button)findNode("#btnCreatePlantSidebar"));
        //fill out text fields
        TextField name = findNode("#fieldCreatePlantName");
        TextField id = findNode("#fieldCreatePlantId");
        enterText(name,"testPlant");
        enterText(id,"6969");
        //click add btn
        clickOn((Button)findNode("#btnCreatePlant"));
        assertTrue(isEventTypePresent(LogEventType.PLANT_CREATED));
    }

    @RepeatedTest(value = 2)
    void makePlantEditedEvent(){
        //add plant
        Plant plant1 = new Plant(4321,"Testing factory 1", new AccessModifier());
        PlantElement plant1Element;
        PlantAdministrationController plantController;
        PlantManager.getInstance().getAllPlants().add(plant1);
        clickOn((ToggleButton)findNode("#administratePlantsButton"));
        plantController = (PlantAdministrationController) dmsApplication.getCurrentTab().getTabController();
        plant1Element = (PlantElement) plantController.getPlantVBox().getChildren().get(0);

        //Edit plant
        //Select Plant
        clickOn(plant1Element);
        clickOn((Button)findNode("#btnEditPlantSidebar"));
        //fill out text fields
        TextField name2 = findNode("#fieldEditPlantName");
        TextField id2 = findNode("#fieldEditPlantId");
        enterText(name2,"testEdit");
        enterText(id2,"1337");
        //click add btn
        clickOn((Button)findNode("#btnSavePlantEdit"));
        assertTrue(isEventTypePresent(LogEventType.PLANT_EDITED));
    }

    @RepeatedTest(value = 2)
    void makePlantDeletedEvent(){
        Plant plant1 = new Plant(4321,"Testing factory 1", new AccessModifier());
        PlantElement plant1Element;
        PlantAdministrationController plantController;
        PlantManager.getInstance().getAllPlants().add(plant1);
        clickOn((ToggleButton)findNode("#administratePlantsButton"));
        plantController = (PlantAdministrationController) dmsApplication.getCurrentTab().getTabController();
        plant1Element = (PlantElement) plantController.getPlantVBox().getChildren().get(0);
        //Delete plant
        //Select Plant
        clickOn(plant1Element);
        clickOn((Button)findNode("#btnDeletePlantSidebar"));
        clickOn(DMSApplication.getMessage("PlantAdmin.Popup.Delete"));
        assertTrue(isEventTypePresent(LogEventType.PLANT_DELETED));
    }

    @RepeatedTest(value = 2)
    void makeFileMovedEvent(){
        clickOn((ToggleButton)findNode("#administrateDocumentsButton"));

        // Create reference for file tree
        fileTree = findNode("#fileTreeView");

        // Move something to enable the publish button.
        treeItem = fileTree.getRoot().getChildren().get(0);
        //Open folder to make sure file is visible
        doubleClickOn(getTreeCell(fileTree, fileTree.getRoot().getChildren().get(1)));
        TreeItem<AbstractFile> treeItem2 = fileTree.getRoot().getChildren().get(1).getChildren().get(3);
        drag(getTreeCell(fileTree, treeItem2));
        dropTo(getTreeCell(fileTree, treeItem));

        assertTrue(isEventTypePresent(LogEventType.FILE_MOVED));

    }

    @RepeatedTest(value = 2)
    void makePlantAccessAndRemovedGivenEvent() {

        //make plant
        Plant plant1 = new Plant(4321,"Testing factory 1", new AccessModifier());
        PlantElement plant1Element;
        PlantAdministrationController plantController;
        PlantManager.getInstance().getAllPlants().add(plant1);
        //switch tab
        clickOn((ToggleButton) findNode("#administrateDocumentsButton"));
        //Select file to add
        fileTree = findNode("#fileTreeView");
        treeItem = fileTree.getRoot().getChildren().get(1);
        doubleClickOn(getTreeCell(fileTree,treeItem));
        //get child which is a file
        TreeItem<AbstractFile> selectedFile = fileTree.getRoot().getChildren().get(1).getChildren().get(3);
        clickOn(getTreeCell(fileTree,selectedFile));
        //click plant to add file to plant
        VBox plantVBox = findNode("#plantVBox");
        clickOn(plantVBox.getChildren().get(0));
        assertTrue(isEventTypePresent(LogEventType.PLANT_ACCESS_GIVEN));
        //now remove access
        clickOn(plantVBox.getChildren().get(0));
        assertTrue(isEventTypePresent(LogEventType.PLANT_ACCESS_REMOVED));
    }

    @RepeatedTest(value = 2)
    void testSearch() {
        populateLog();
        TextField textField = findNode("#searchField");
        TableView tableView = findNode("#tableView");
        enterText(textField,"Top");
        LogEvent firstEvent = (LogEvent) tableView.getItems().get(0);
        assertEquals("Top", firstEvent.getUser());
        enterText(textField,"");
    }

    @RepeatedTest(value = 2)
    void testChangeTypeSort() {
        populateLog();
        TableView tableview = findNode("#tableView");
        clickOn((Button)findNode("#btnSortByChangeType"));
        LogEvent firstEvent = (LogEvent) tableview.getItems().get(0);
        assertSame(firstEvent.getEventType(), (LogEventType.CREATED));
    }

    @RepeatedTest(value = 2)
    void testTimeSort() {
        populateLog();
        TableView tableview = findNode("#tableView");
        clickOn((Button)findNode("#btnSortByTime"));
        LogEvent firstEvent = (LogEvent) tableview.getItems().get(0);
        assertTrue(firstEvent.getTime().matches(latestChange.getTime()));
    }
    @RepeatedTest(value = 2)
    void testUserSort() {
        populateLog();
        TableView tableview = findNode("#tableView");
        clickOn((Button)findNode("#btnSortByUser"));
        LogEvent firstEvent = (LogEvent) tableview.getItems().get(0);
        assertEquals("eksD", firstEvent.getUser());
    }


}