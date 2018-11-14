package gui;

import directory.files.AbstractFile;
import directory.files.Document;
import directory.files.Folder;
import directory.plant.AccessModifier;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.List;

public class FileTreeGenerator {

    private static final Image folderImage = new Image("/icons/smallFolder.png");
    private static final Image documentImage = new Image("/icons/smallBlueDoc.png");

    public static TreeItem<AbstractFile> generateTree(Folder rootFolder, AccessModifier accessModifier) {
        // Add folder element to tree
        TreeItem<AbstractFile> rootItem = new TreeItem<>(rootFolder);
        rootItem.setGraphic(getImageView(rootFolder));

        List<AbstractFile> children = rootFolder.getContents();
        // Sort to show files in order of file type and name
        children.sort(FileTreeGenerator::compareFiles);

        for (AbstractFile child : children) {
            if (child instanceof Folder) {
                // Generate new tree from folder if it is contained within the accessModifier or if there is no access modifier
                if(accessModifier == null || ((Folder) child).containsFromAccessModifier(accessModifier))
                    rootItem.getChildren().add(generateTree((Folder) child, accessModifier));

            }else if(child instanceof Document){
                // Add file to the tree if it is contained within the accessModifier or there is no accessModifier
                if(accessModifier == null || accessModifier.contains(((Document)child).getID())){
                    TreeItem<AbstractFile> docItem = new TreeItem<>(child);
                    docItem.setGraphic(getImageView(child));
                    rootItem.getChildren().add(docItem);
                }
            }
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

    // Returns an image view with an appropriate icon according to the file-type
    public static ImageView getImageView(AbstractFile file) {
        ImageView imageView;
        if (file instanceof Folder) {
            imageView = new ImageView(folderImage);
            // Set scaling of image
            imageView.setFitWidth(16);
            imageView.setFitHeight(16);
        } else {
            // todo add icons for multiple filetypes
            imageView = new ImageView(documentImage);
            //Set scaling of image
            imageView.setFitWidth(14);
            imageView.setFitHeight(16);
        }
        return imageView;
    }


}
