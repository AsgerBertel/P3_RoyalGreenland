package directory.files;

import directory.FileManager;
import org.junit.jupiter.api.Test;

import javax.naming.InvalidNameException;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class FolderTest {

    @Test
    void renameFile() {
        Path pathToTestDir = Paths.get("src/tests/resTest/Main Files Test");
        File newDirectory = new File(pathToTestDir.toString());

        if (!newDirectory.exists())
            newDirectory.mkdirs();

        Folder folder = FileManager.getTestInstance().createFolder(newDirectory.toPath(), "renameTestFolder");

        folder.renameFile("renamedTestFolder");
        assertEquals("renamedTestFolder", folder.getName());
        assertTrue(new File(pathToTestDir.toString() + File.separator + "renamedTestFolder").exists());
        assertTrue(new File(pathToTestDir.toString() + File.separator + "renamedTestFolder").delete());
    }

    @Test
    void getContents() {
    }
}