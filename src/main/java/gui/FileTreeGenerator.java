package gui;

import directory.files.AbstractFile;
import directory.files.Folder;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

public class FileTreeGenerator {

    private static final Image folderImage = new Image("/icons/small_folder.png");
    private static final Image documentImage = new Image("/icons/small_blue_doc.png");

    public static TreeItem<AbstractFile> generateTree(AbstractFile rootFile){
        TreeItem<AbstractFile> item = new TreeItem<>(rootFile);

        if(rootFile instanceof Folder){
            item.setGraphic(new ImageView(folderImage));
            List<AbstractFile> children = ((Folder) rootFile).getContents();

            for(AbstractFile child : children)
                item.getChildren().add(generateTree(child));


        }else{
            item.setGraphic(new ImageView(documentImage));
            // todo add different icons for different file types
        }

        return item;
    }



}
