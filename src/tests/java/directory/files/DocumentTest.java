package directory.files;

import app.ApplicationMode;
import directory.Settings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class DocumentTest {
    private File resourcesDirectory = new File("src/tests/resTest");
    private Path pathToTestFileExt = Paths.get(resourcesDirectory.getAbsolutePath() + "/Main Files Test/testFileExt.pdf");
    private Path pathToTestFileMove = Paths.get(resourcesDirectory.getAbsolutePath() + "/Main Files Test/testFileMove.pdf");
    private Document docExt;
    private Document docMove;
    private Path pathToWrongFile = Paths.get(resourcesDirectory.getAbsolutePath() + "Main Files Test/testFileWrong.pdf");
    private Document docWrong;

    @BeforeEach
    void setSettings(){
        Settings.loadSettings(ApplicationMode.ADMIN);
        docExt = DocumentBuilder.getInstance().createDocument(pathToTestFileExt);
        docMove = DocumentBuilder.getInstance().createDocument(pathToTestFileMove);
        docWrong = DocumentBuilder.getInstance().createDocument(pathToWrongFile);
    }

    @Test
    void getID() {
        assertEquals(docExt.getID(), docExt.getID());
    }

    @Test
    void getFileExtension() {

        //Gets right extension
        assertEquals("pdf" ,docExt.getFileExtension());
        //No extension, sends nothing back
        assertEquals("pdf", docWrong.getFileExtension());
    }

   /* @Test
    void moveFile() {
        Path orgPath = docMove.getPath();
        Path pathMoveTo = Paths.get(resourcesDirectory.getAbsolutePath() + "/Main Files Test/MoveFileTest/");
        try {
            docMove.moveFile(pathMoveTo);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertEquals(docMove.getPath().toString(), pathMoveTo.toString() + File.separator + docMove.getName());

        try {
            docMove.moveFile(orgPath.getParent());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    @Test
    void openDocument() {
    }

    @Test
    void setLastModified() {

    }
/*
    @Test todo
    void createPlant() {
        // Store original name and new name
        String originalName = doc.getName();
        String newName = "name.pdf";
        try {
            doc.createPlant(newName);
        } catch (InvalidNameException e) {
            e.printStackTrace();
        }

        // Assert that the document has changed name to the new one correctly.
        assertEquals(newName, doc.getName());

        // Change name back to original.
        try {
            doc.createPlant(originalName);
        } catch (InvalidNameException e) {
            e.printStackTrace();
        }

    }*/
/*
    @Test
    void renameWrongName(){

        //Try changing one document name to the same as the other
        String originalName = doc.getName();
        assertThrows(InvalidNameException.class, () ->
                docWrong.createPlant(originalName));
    }*/

    @Test
    void deleteFile() {
        // todo Implement when the function is implemented.
    }
}