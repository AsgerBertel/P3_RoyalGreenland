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

public class Folder extends AbstractDocFolder {

    public ObservableList<AbstractDocFolder> folderContents = FXCollections.observableArrayList();
    Image documentImg = new Image("file:///"+Paths.get(".").toAbsolutePath().normalize().toString()+"/Images/document.png");

    public Folder(String name, Path path) {
        super(new ImageView(new Image("file:///"+Paths.get(".").toAbsolutePath().normalize().toString()+"/Images/folder.png")), name, "folder");
        this.path = path;
    }

    public void readContent() throws IOException {

        Files.walk(path, 1)
                .filter(Files::isDirectory)
                .forEach(file -> folderContents.add(new Folder(file.getFileName().toString(), file.toAbsolutePath())));

        Files.walk(path, 1)
                .filter(Files::isRegularFile)
                .forEach(file -> folderContents.add(new Document(file.getFileName().toString(), new ImageView(documentImg), "document", file.toAbsolutePath())));
    }

    // virker mulighvis ikke
    public Folder getSubFolder(String name, Path path) {
        return new Folder(name, path);
    }

/*
    public ArrayList<AbstractDocFolder> getFolderContents() throws IOException {

        if (folderContents.isEmpty()) {
            readContent();
            return folderContents;
        }
        else
            return folderContents;
    }
*/
}
