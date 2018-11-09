package gui;

import directory.files.AbstractFile;
import directory.files.Folder;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.print.Doc;
import java.util.Comparator;
import java.util.List;

public class FileTreeGenerator {

    private static final Image folderImage = new Image("/icons/smallFolder.png");
    private static final Image documentImage = new Image("/icons/smallBlueDoc.png");

    public static TreeItem<AbstractFile> generateTree(AbstractFile rootFile){
        TreeItem<AbstractFile> item = new TreeItem<>(rootFile);

        if(rootFile instanceof Folder){
            List<AbstractFile> children = ((Folder) rootFile).getContents();

            // Sort to show files in order of file type and name
            children.sort(FileTreeGenerator::compareFiles);

            for(AbstractFile child : children)
                item.getChildren().add(generateTree(child));
        }

        item.setGraphic(getImageView(rootFile));
        return item;
    }

    private static int compareFiles(AbstractFile o1, AbstractFile o2){
        if(o1.getClass().equals(o2.getClass())){
            // Compare name is both classes are the same (both are folders or both are documents)
            return o1.getName().compareTo(o2.getName());
        }else{
            // Folders before documents
            if(o1 instanceof Folder)
                return -1;
            else
                return 0;
        }
    }

    // Returns an image view with an appropriate icon according to the file-type
    public static ImageView getImageView(AbstractFile file){
        ImageView imageView;
        if(file instanceof Folder){
            imageView = new ImageView(folderImage);
            // Set scaling of image
            imageView.setFitWidth(16);
            imageView.setFitHeight(16);
        }else{
            // todo add icons for multiple filetypes
            imageView = new ImageView(documentImage);
            //Set scaling of image
            imageView.setFitWidth(14);
            imageView.setFitHeight(16);
        }
        return imageView;
    }



}
