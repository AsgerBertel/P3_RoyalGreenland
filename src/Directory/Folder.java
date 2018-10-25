package Directory;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.naming.InvalidNameException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class Folder extends AbstractFile {

    public List<AbstractFile> folderContents = FXCollections.observableArrayList();

    private static Image folderImage = new Image("icons/folder.png"); // todo add image shit (maybe in superclass)

    public Folder(Path path) {
        super(path);
    }

    @Override
    public void renameFile(String newFileName) throws InvalidNameException {

        // TODO: 25-10-2018 : add functionality for changing path for all child elements (in relation to the accessmodifier)
    }

    @Override
    public void deleteFile(Path path) throws IOException {
        // TODO: 25-10-2018 : delete folder and contents
    }

    // Reads the content o path its given
    public List<AbstractFile> getContents() throws IOException {
        updateContents();
        return folderContents;
    }

    // Reads the list of files within the folder
    public void updateContents() throws IOException{
        Files.walk(path, 1)
                .filter(path1 -> Files.isDirectory(path1) && !path1.equals(path))
                .forEach(file -> folderContents.add(new Folder(file.toAbsolutePath())));

        Files.walk(path, 1)
                .filter(Files::isRegularFile)
                .forEach(file -> folderContents.add(new Document(file.toAbsolutePath())));
    }
}
