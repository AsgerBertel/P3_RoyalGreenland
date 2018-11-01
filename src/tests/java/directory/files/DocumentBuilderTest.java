package directory.files;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class DocumentBuilderTest {
    File resourcesDirectory = new File("src/tests/resTest");
    private Path pathToTestFile = Paths.get(resourcesDirectory.getAbsolutePath() + "/Main Files Test/FL 01 GR_01 Flowdiagram Produktion af saltfisk.pdf");
    private Path pathToCurrentID = Paths.get( resourcesDirectory.getAbsolutePath() + "/currentFileIDTest");


    @Test
    void createDocumentANDreadAndUpdateCurrentID() {
        DocumentBuilder.getInstance().setCurrentIDPath(pathToCurrentID);
        int ID = DocumentBuilder.getInstance().readAndUpdateCurrentID();
        DocumentBuilder.getInstance().createDocument(pathToTestFile);
        int IDafterIncrement = DocumentBuilder.getInstance().readAndUpdateCurrentID();
        System.out.println(ID + " after: " + IDafterIncrement);
        assertEquals(ID, IDafterIncrement - 2);
    }
}