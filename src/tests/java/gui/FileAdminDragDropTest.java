package gui;

import directory.files.AbstractFile;
import javafx.scene.Node;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;
import util.TestUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FileAdminDragDropTest extends GUITest {

    @Test
    void simpleDragDropTest() throws InterruptedException {
        TreeView<AbstractFile> treeView = findNode("#fileTreeView");

        FxRobot fxRobot = drag(getTreeCell(treeView, treeView.getRoot().getChildren().get(0)));
        fxRobot.dropTo(getTreeCell(treeView, treeView.getRoot().getChildren().get(1)));

    }

    private static TreeCell getTreeCell(TreeView tree, TreeItem<AbstractFile> treeItem){
        Set<Node> treeCells = tree.lookupAll(".tree-cell");
        List<Node> cells = new ArrayList<>(treeCells);
        int row = tree.getRow(((TreeItem) treeItem));
        return ((TreeCell) cells.get(row));
    }
}
