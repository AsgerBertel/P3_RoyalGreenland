package directory.files;

import org.junit.jupiter.api.Test;

import javax.naming.InvalidNameException;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FolderTest {
    private File resourcesDirectory = new File("src/tests/resTest");
    private Path pathToTestFolder = Paths.get(resourcesDirectory.getAbsolutePath() + "/Main Files Test/renameFolderTest");

    @Test
    void renameFile() {
        Folder folder = new Folder(pathToTestFolder);
        List<AbstractFile> contents =  folder.getContents();

        for(AbstractFile file : contents){
            System.out.println("Name: " + file.getName());
            System.out.println("Path: " + file.getPath().toString());
        }

        try {
            folder.renameFile("renameFolderTest22");
        } catch (InvalidNameException e) {
            System.out.println("Error in renameFile test");
            e.printStackTrace();
        }

        for(AbstractFile file : contents){
            System.out.println("Name: " + file.getName());
            System.out.println("Path: " + file.getPath().toString());
        }
    }

    @Test
    void deleteFile() {
    }

    @Test
    void getContents() {
    }
}