package Directory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ReadDirectory
{

    public ArrayList<Document> readAllDocuments(Path path) throws IOException {
        ArrayList<Document> documents = new ArrayList<>();

        Files.walk(path)
                .filter(Files::isRegularFile)
                .forEach(path1 -> documents.add(new Document(path1)));

        return documents;
    }
    public ArrayList<Folder> readAllFolders(Path path) throws IOException {
        ArrayList<Folder> folders = new ArrayList<>();

        Files.walk(path)
                .filter(Files::isDirectory)
                .forEach(path1 -> folders.add(new Folder(path1)));

        return folders;
    }

}
