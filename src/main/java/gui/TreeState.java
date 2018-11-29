package gui;

import directory.files.AbstractFile;
import directory.files.Folder;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.nio.file.Path;
import java.util.HashMap;

public class TreeState{

    private HashMap<Path, Boolean> expandedStateByPath;

    public TreeState(TreeView<AbstractFile> fileTree){
        generateMap(fileTree);
    }

    public void setExpandedState(TreeView<AbstractFile> fileTree){
        generateMap(fileTree);
    }

    private void generateMap(TreeView<AbstractFile> fileTree){
        this.expandedStateByPath = new HashMap<>();
        mapChildren(fileTree.getRoot());
    }

    private void mapChildren(TreeItem<AbstractFile> root){
        ObservableList<TreeItem<AbstractFile>> children = root.getChildren();
        for(TreeItem<AbstractFile> child : children){
            if(child.getValue() instanceof Folder){
                expandedStateByPath.put(child.getValue().getOSPath(), child.isExpanded());
                mapChildren(child);
            }
        }
    }

    public void replicateTreeExpansion(TreeItem<AbstractFile> root){
        ObservableList<TreeItem<AbstractFile>> children = root.getChildren();
        for(TreeItem<AbstractFile> child : children){
            if(child.getValue() instanceof Folder){
                child.setExpanded(expandedStateByPath.getOrDefault(child.getValue().getOSPath(), Boolean.FALSE));
                replicateTreeExpansion(child);
            }
        }
    }
}