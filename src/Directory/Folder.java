package Directory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Folder extends AbstractDocFolder
{
    private ArrayList<AbstractDocFolder> folderContents;

    public Folder(String name, Path path) {
        super(name, path);
    }

    public void readContent () throws IOException {
        ArrayList<AbstractDocFolder> content = new ArrayList<>();

        Files.walk(path, 1)
                .filter(Files::isDirectory)
                .forEach(file -> content.add(new Folder(file.getFileName().toString(), file.getFileName())));

        Files.walk(path, 1)
                .filter(Files::isRegularFile)
                .forEach(file -> content.add(new Document(file.getFileName().toString(), file.getFileName())));

        folderContents = content;
    }

    public Folder openFolder(String name, Path path) {
        return new Folder(name, path);
    }

    public ArrayList<AbstractDocFolder> getFolderContents() throws IOException {

        if (folderContents.isEmpty()) {
            readContent();
            return folderContents;
        }
        else
            return folderContents;
    }
}
