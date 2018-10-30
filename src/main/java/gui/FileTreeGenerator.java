package gui;

import directory.files.AbstractFile;
import directory.files.Folder;
import javafx.scene.control.TreeItem;
import java.util.List;

public class FileTreeGenerator {

    public static TreeItem<AbstractFile> generateTree(AbstractFile rootFile){
        TreeItem<AbstractFile> item = new TreeItem<>(rootFile);

        if(rootFile instanceof Folder){
            //item.setGraphic(); todo set
            List<AbstractFile> children = ((Folder) rootFile).getContents();

            for(AbstractFile child : children)
                item.getChildren().add(generateTree(child));
        }else{
            //item.setGraphic(); todo set document icon
        }

        return item;
    }



}
