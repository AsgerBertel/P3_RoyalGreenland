package directory.files;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class DocumentTest {
    File resourcesDirectory = new File("src/tests/resTest");
    Path pathToTestFile = Paths.get(resourcesDirectory.getAbsolutePath() + "/Main Files Test/FL 01 GR_01 Flowdiagram Produktion af saltfisk.pdf");
    private Document doc = DocumentBuilder.getInstance().createDocument(pathToTestFile);

    @Test
    void getID() {
        assertEquals(doc.getID(), doc.getID());
    }

    @Test
    void getFileExtension() {
        assertEquals(doc.getFileExtension(), "pdf");
    }

    @Test
    void moveFile() {
    }

    @Test
    void openDocument() {
        try {
            doc.openDocument();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void renameFile() {
    }

    @Test
    void deleteFile() {
    }
}