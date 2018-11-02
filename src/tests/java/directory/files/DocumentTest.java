package directory.files;

import org.junit.jupiter.api.Test;

import javax.naming.InvalidNameException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class DocumentTest {
    private File resourcesDirectory = new File("src/tests/resTest");
    private Path pathToTestFile = Paths.get(resourcesDirectory.getAbsolutePath() + "/Main Files Test/FL 01 GR_01 Flowdiagram Produktion af saltfisk.pdf");
    private Document doc = DocumentBuilder.getInstance().createDocument(pathToTestFile);
    private Path pathToWrongFile = Paths.get(resourcesDirectory.getAbsolutePath() + "Main Files Test/FL 01 GR_01 Flowdiagram Produktion af salt");
    private Document wrongDoc = DocumentBuilder.getInstance().createDocument(pathToWrongFile);

    @Test
    void getID() {
        assertEquals(doc.getID(), doc.getID());
    }

    @Test
    void getFileExtension() {

        //Gets right extension
        assertEquals("pdf" ,doc.getFileExtension());
        //No extension, sends nothing back
        assertEquals("", wrongDoc.getFileExtension());
    }

    @Test
    void moveFile() {
        Path orgPath = doc.getPath();
        Path pathMoveTo = Paths.get(resourcesDirectory.getAbsolutePath() + "/Main Files Test/MoveFileTest/");
        try {
            doc.moveFile(pathMoveTo);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertEquals(doc.getPath().toString(), pathMoveTo.toString() + File.separator + doc.getName());

        try {
            doc.moveFile(orgPath.getParent());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void openDocument() {
    }

    @Test
    void renameFile() {
        // Store original name and new name
        String originalName = doc.getName();
        String newName = "name.pdf";
        try {
            doc.renameFile(newName);
        } catch (InvalidNameException e) {
            e.printStackTrace();
        }

        // Assert that the document has changed name to the new one correctly.
        assertEquals(newName, doc.getName());

        // Change name back to original.
        try {
            doc.renameFile(originalName);
        } catch (InvalidNameException e) {
            e.printStackTrace();
        }

    }

    @Test
    void renameWrongName(){

        //Try changing one document name to the same as the other
        String originalName = doc.getName();
        assertThrows(InvalidNameException.class, () ->
                wrongDoc.renameFile(originalName));
    }

    @Test
    void deleteFile() {
        // todo Implement when the function is implemented.
    }
}