package gui;

import directory.FileManager;
import directory.SettingsManager;
import directory.files.AbstractFile;
import directory.files.Folder;
import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import util.TestUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileAdminDragDropTest extends GUITest {

    @Test
    void simpleDragDropTest() throws InterruptedException {
        TreeView<AbstractFile> treeView = findNode("#fileTreeView");

        assertTrue(TestUtil.doesAbstractFileMatchFileSystem(FileManager.getInstance().getMainFilesRoot(), SettingsManager.getServerDocumentsPath()));
        assertTrue(TestUtil.doesAbstractFileMatchTreeItem(FileManager.getInstance().getMainFilesRoot(),treeView.getRoot()));

        // Drag folder 1 into folder 0
        TreeItem<AbstractFile> targetItem = treeView.getRoot().getChildren().get(0);
        TreeItem<AbstractFile> itemToMove = treeView.getRoot().getChildren().get(1);
        FxRobot fxRobot = drag(getTreeCell(treeView, itemToMove));
        fxRobot.dropTo(getTreeCell(treeView, targetItem));

        // Check that the folder has been moved both in the treeView and in the files list
        assertTrue(containsItemWithFile(treeView.getRoot().getChildren().get(0), itemToMove.getValue()));
        assertTrue(((Folder) targetItem.getValue()).getContents().contains(itemToMove.getValue()));

        assertTrue(TestUtil.doesAbstractFileMatchFileSystem(FileManager.getInstance().getMainFilesRoot(), SettingsManager.getServerDocumentsPath()));
        assertTrue(TestUtil.doesAbstractFileMatchTreeItem(FileManager.getInstance().getMainFilesRoot(),treeView.getRoot()));
    }

    private boolean containsItemWithFile(TreeItem<AbstractFile> root, AbstractFile file){
        for(TreeItem<AbstractFile> child : root.getChildren())
            if(child.getValue().equals(file)) return true;
        return false;
    }

    private static void printSpaces(int spaces){
        for(int i = 0; i < spaces; i++)
            System.out.print(" ");
    }

    @Test
    void dragIntoSubfolderTest() throws InterruptedException {
        TreeView<AbstractFile> treeView = findNode("#fileTreeView");

        assertTrue(TestUtil.doesAbstractFileMatchFileSystem(FileManager.getInstance().getMainFilesRoot(), SettingsManager.getServerDocumentsPath()));
        assertTrue(TestUtil.doesAbstractFileMatchTreeItem(FileManager.getInstance().getMainFilesRoot(),treeView.getRoot()));

        // Drag itemToMove folder into its' first subfolder
        treeView.getRoot().getChildren().get(0).setExpanded(true);
        Thread.sleep(200);

        TreeItem<AbstractFile> itemToMove = treeView.getRoot().getChildren().get(0);
        TreeItem<AbstractFile> targetItem = itemToMove.getChildren().get(0);

        FxRobot fxRobot = drag(getTreeCell(treeView, itemToMove));
        fxRobot.dropTo(getTreeCell(treeView, targetItem));

        // Check (in both files list and treeview) that the folder has not been moved
        assertFalse(containsItemWithFile(itemToMove, itemToMove.getValue()));
        assertTrue(containsItemWithFile(treeView.getRoot(), itemToMove.getValue()));

        assertTrue(treeView.getRoot().getChildren().contains(itemToMove));
        assertTrue(itemToMove.getChildren().contains(targetItem));

        // Assert the file list matches the treeView
        assertTrue(TestUtil.doesAbstractFileMatchFileSystem(FileManager.getInstance().getMainFilesRoot(), SettingsManager.getServerDocumentsPath()));
        assertTrue(TestUtil.doesAbstractFileMatchTreeItem(FileManager.getInstance().getMainFilesRoot(),treeView.getRoot()));
    }

    private static TreeCell getTreeCell(TreeView tree, TreeItem<AbstractFile> treeItem){
        Set<Node> treeCells = tree.lookupAll(".tree-cell");
        List<Node> cells = new ArrayList<>(treeCells);
        int row = tree.getRow(treeItem);
        return ((TreeCell) cells.get(row));
    }

    private void expandAllItems(){

    }
}

