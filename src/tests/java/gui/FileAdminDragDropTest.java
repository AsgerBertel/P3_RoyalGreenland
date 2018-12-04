package gui;

import directory.files.AbstractFile;
import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import util.TestUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FileAdminDragDropTest extends GUITest {


    @BeforeAll
    static void setUp() throws IOException {
        TestUtil.resetTestFiles();
    }

    @Test
    void dropTest() throws InterruptedException {
        TreeView<AbstractFile> treeView = findNode("#fileTreeView");

        moveTo(getTreeCell(treeView, treeView.getRoot().getChildren().get(0)));
        press(MouseButton.PRIMARY);
        moveTo(getTreeCell(treeView, treeView.getRoot().getChildren().get(1)));

        Thread.sleep(1500);
    }

    private static TreeCell getTreeCell(TreeView tree, TreeItem<AbstractFile> treeItem){
        Set<Node> treeCells = tree.lookupAll(".tree-cell");
        List<Node> cells = new ArrayList<>(treeCells);
        int row = tree.getRow(((TreeItem) treeItem));
        return ((TreeCell) cells.get(row));
    }

    private void drag(){
    }

}
