package Directory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Folder extends AbstractDocFolder
{
    ArrayList<AbstractDocFolder> folderContents;

    public Folder(String name, Path path) {
        super(name, path);
    }

    public ArrayList<AbstractDocFolder> readContent () throws IOException
    {
        ArrayList<AbstractDocFolder> content = new ArrayList<>();
        Files.walk(path)
                .filter(Files::isDirectory)
                .forEach(file -> content.add(new Folder(file.getFileName().toString(), file.getFileName())));

        Files.walk(path)
                .filter(Files::isRegularFile)
                .forEach(file -> content.add(new Document(file.getFileName().toString(), file.getFileName())));

        return content;
    }
}
