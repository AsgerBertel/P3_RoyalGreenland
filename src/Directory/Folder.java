package Directory;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.function.Predicate;

public class Folder extends AbstractDocFolder {

    public ObservableList<AbstractDocFolder> folderContents = FXCollections.observableArrayList();


    Image documentImg = new Image("Images/document.png");

    //Constructer
    public Folder(String name, Path path) {
        super(new ImageView(new Image("Images/folder.png")), name, "folder");
        setPath(path);
    }

    //Reads the content of the of det path its given
    public void readContent() throws IOException {

        Files.walk(path, 1)
                .filter(path1 -> Files.isDirectory(path1) && !path1.equals(path))
                .forEach(file -> folderContents.add(new Folder(file.getFileName().toString(), file.toAbsolutePath())));

        Files.walk(path, 1)
                .filter(Files::isRegularFile)
                .forEach(file -> folderContents.add(new Document(file.getFileName().toString(), new ImageView(documentImg), "document", file.toAbsolutePath())));
    }
}
