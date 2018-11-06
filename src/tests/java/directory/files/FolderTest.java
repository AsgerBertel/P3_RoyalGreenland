package directory.files;

import directory.FileManager;
import org.junit.jupiter.api.Test;

import javax.naming.InvalidNameException;
import java.io.File;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class FolderTest {

    File newDirectory = new File(Paths.get("").toAbsolutePath().toString());

    @Test
    void renameFile() {
        System.out.println(newDirectory.toPath().toString());

        if (!newDirectory.exists())
            newDirectory.mkdirs();

        Folder folder = FileManager.getInstance().createFolder(newDirectory.toPath(), "renameTestFolder");

        folder.renameFile("renamedTestFolder");
        assertEquals("renamedTestFolder", folder.getName());
        assertTrue(new File(Paths.get("").toAbsolutePath().toString() + File.separator + "renamedTestFolder").exists());
        assertTrue(new File(Paths.get("").toAbsolutePath().toString() + File.separator + "renamedTestFolder").delete());
    }

    @Test
    void getContents() {
    }
}