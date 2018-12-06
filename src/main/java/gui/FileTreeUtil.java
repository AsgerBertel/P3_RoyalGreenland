package gui;

import directory.files.AbstractFile;
import directory.files.Document;
import directory.files.Folder;
import directory.plant.AccessModifier;
import javafx.scene.control.Cell;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FileTreeUtil {
    private static Image pdfImage = new Image("/icons/smallRedDoc.png");
    private static final Image folderImage = new Image("/icons/smallFolder.png");
    private static final Image documentImage = new Image("/icons/smallBlueDoc.png");


    /**
     * Generates a tree with the given folder as the root element
     *
     * @param root the root of the tree
     * @return a TreeItem created from the given root folder, containing all child files
     */
    public static TreeItem<AbstractFile> generateTree(Folder root) {
        return generateTree(root, null);
    }


    public static TreeItem<AbstractFile> generateTree(ArrayList<AbstractFile> files) {
        return generateTree(files, null);
    }

    /**
     * Generates dummy TreeItem as the root. Adds all given files and potential sub directories to the tree.
     *
     * @param files the list of files that will be included in the tree. Children of any folder will also be added to the tree.
     * @return a dummy root element containing all the given files.
     */
    public static TreeItem<AbstractFile> generateTree(ArrayList<AbstractFile> files, AccessModifier accessModifier) {
        TreeItem<AbstractFile> root = new TreeItem<>();

        // Add all files to the tree if they are contained in accessModifier or if no accessModifier is given
        for (AbstractFile file : files) {
            if (file instanceof Folder) {
                if (accessModifier == null || ((Folder) file).containsFromAccessModifier(accessModifier))
                    root.getChildren().add(generateTree((Folder) file, accessModifier));
            } else {
                if (accessModifier == null || accessModifier.contains(((Document) file).getID()))
                    root.getChildren().add(createTreeItem((Document) file));
            }
        }
        return root;
    }

    // Recursively generates a tree from a root folder. The tree will only contain files from the given access modifier
    // If the access modifier is null all files will be shown in the tree
    private static TreeItem<AbstractFile> generateTree(Folder rootFolder, AccessModifier accessModifier) {
        // Add folder element to tree
        TreeItem<AbstractFile> rootItem = createTreeItem(rootFolder);

        List<AbstractFile> children = rootFolder.getContents();
        // Sort to show files in order of file type and then name
        children.sort(FileTreeUtil::compareFiles);

        for (AbstractFile child : children) {
            if (child instanceof Folder) {
                // Generate new tree from folder if it is contained within the accessModifier or if there is no access modifier
                if (accessModifier == null || ((Folder) child).containsFromAccessModifier(accessModifier))
                    rootItem.getChildren().add(generateTree((Folder) child, accessModifier));

            } else if (child instanceof Document) {
                // Add file to the tree if it is contained within the accessModifier or there is no accessModifier
                if (accessModifier == null || accessModifier.contains(((Document) child).getID()))
                    rootItem.getChildren().add(createTreeItem(child));

            }
        }
        return rootItem;
    }

    // Creates a single tree item with no children
    public static TreeItem<AbstractFile> createTreeItem(AbstractFile file) {
        TreeItem<AbstractFile> docItem = new TreeItem<>(file);
        docItem.setGraphic(getImageView(file));
        return docItem;
    }

    // Compare function for sorting files
    private static int compareFiles(AbstractFile o1, AbstractFile o2) {
        if (o1.getClass().equals(o2.getClass())) {
            // Compare name if both classes are the same (both are folders or both are documents)
            return o1.getName().compareTo(o2.getName());
        } else {
            // Folders before documents
            if (o1 instanceof Folder)
                return -1;
            else
                return 0;
        }
    }

    // Returns an image view with an appropriate icon according to the file-type
    private static ImageView getImageView(AbstractFile file) {
        ImageView imageView;

        if (file instanceof Folder) {
            imageView = new ImageView(folderImage);

            // Set scaling of image
            imageView.setFitWidth(16);
            imageView.setFitHeight(16);
        } else {
            String fileExtension = ((Document) file).getFileExtension();
            if (fileExtension.contains("docx") || fileExtension.contains("doc")) {
                imageView = new ImageView(documentImage);
                //Set scaling of image
                imageView.setFitWidth(14);
                imageView.setFitHeight(16);
            } else {
                imageView = new ImageView(pdfImage);
                //Set scaling of image
                imageView.setFitWidth(14);
                imageView.setFitHeight(16);
            }
        }

        return imageView;
    }

}
