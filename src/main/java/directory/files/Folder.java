package directory.files;


import javafx.collections.FXCollections;
import javafx.scene.image.Image;

import javax.naming.InvalidNameException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Folder extends AbstractFile {

    public List<AbstractFile> folderContents = FXCollections.observableArrayList();

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
    private void updateContents() throws IOException{
        // todo add access modifier into filter
        Files.walk(path, 1)
                .filter(path1 -> Files.isDirectory(path1) && !path1.equals(path))
                .forEach(file -> folderContents.add(new Folder(file.toAbsolutePath())));

        Files.walk(path, 1)
                .filter(Files::isRegularFile)
                .forEach(file -> folderContents.add(new Document(file.toAbsolutePath())));
    }
}