package directory.files;

import app.ApplicationMode;
import directory.FileManager;
import directory.FileTester;
import directory.SettingsManager;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class DocumentBuilderTest extends FileTester {
    private Path pathToTestFile = Paths.get("03_URENSET STENBIDERROGN/GMP 03 GR_02.pdf");

    @Override
    protected void setSettings(){
        SettingsManager.loadSettings(ApplicationMode.ADMIN);
    }

    @Test
    void createDocumentANDreadAndUpdateCurrentID() {
        int ID = DocumentBuilder.getInstance().readAndUpdateCurrentID();
        Document doc = DocumentBuilder.getInstance().createDocument(pathToTestFile);
        int IDafterIncrement = DocumentBuilder.getInstance().readAndUpdateCurrentID();

        //the assert is IDafterIncrement - 2 because the ID updates two times before
        assertEquals(ID, IDafterIncrement - 2);

        //asserts that file now exists
        assertTrue(Files.exists(SettingsManager.getServerDocumentsPath().resolve(doc.getOSPath())));
    }
}