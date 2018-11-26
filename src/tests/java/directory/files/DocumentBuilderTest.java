package directory.files;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class DocumentBuilderTest {
    File resourcesDirectory = new File("src/tests/resTest");
    private Path pathToTestFile = Paths.get(resourcesDirectory.getAbsolutePath() + "/Main Files Test/testFile.pdf");
    private Path pathToCurrentID = Paths.get( resourcesDirectory.getAbsolutePath() + "/currentFileID");


    @Test
    void createDocumentANDreadAndUpdateCurrentID() {
        DocumentBuilder.getInstance().setCurrentIDPath(pathToCurrentID);
        int ID = DocumentBuilder.getInstance().readAndUpdateCurrentID();
        DocumentBuilder.getInstance().createDocument(pathToTestFile);
        int IDafterIncrement = DocumentBuilder.getInstance().readAndUpdateCurrentID();
        assertEquals(ID, IDafterIncrement - 2);
    }
}