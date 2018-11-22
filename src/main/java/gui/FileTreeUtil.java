package gui;

import directory.files.AbstractFile;
import directory.files.Document;
import directory.files.Folder;
import directory.plant.AccessModifier;
import javafx.event.EventHandler;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;

import java.util.List;
import java.util.Objects;

public class FileTreeUtil {


    // Recursively generates a tree from a root folder. The tree will only contain files from the given access modifier
    // If the access modifier is null all files will be shown in the tree
    public static TreeItem<AbstractFile> generateTree(Folder rootFolder, AccessModifier accessModifier) {
        // Add folder element to tree
        TreeItem<AbstractFile> rootItem = new TreeItem<>(rootFolder);
    //    rootItem.setGraphic(getImageView(rootFolder));

        List<AbstractFile> children = rootFolder.getContents();
        // Sort to show files in order of file type and name
        children.sort(FileTreeUtil::compareFiles);

        for (AbstractFile child : children) {
            if (child instanceof Folder) {
                // Generate new tree from folder if it is contained within the accessModifier or if there is no access modifier
                if (accessModifier == null || ((Folder) child).containsFromAccessModifier(accessModifier))
                    rootItem.getChildren().add(generateTree((Folder) child, accessModifier));

            } else if (child instanceof Document) {
                // Add file to the tree if it is contained within the accessModifier or there is no accessModifier
                if (accessModifier == null || accessModifier.contains(((Document) child).getID())) {
                    TreeItem<AbstractFile> docItem = new TreeItem<>(child);
                  //  docItem.setGraphic(getImageView(child));
                    rootItem.getChildren().add(docItem);


                }
            }
            // TODO: 19-11-2018 Find a way to implement eventhandlers for drag and drop. Functions have been made - Asger.

        }
        return rootItem;
    }

    public static TreeItem<AbstractFile> generateTree(Folder rootFile) {
        return generateTree(rootFile, null);
    }

    // Compare function for sorting files
    private static int compareFiles(AbstractFile o1, AbstractFile o2) {
        if (o1.getClass().equals(o2.getClass())) {
            // Compare name is both classes are the same (both are folders or both are documents)
            return o1.getName().compareTo(o2.getName());
        } else {
            // Folders before documents
            if (o1 instanceof Folder)
                return -1;
            else
                return 0;
        }
    }


}
