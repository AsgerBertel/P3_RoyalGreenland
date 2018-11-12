package directory;

import directory.files.Document;
import directory.files.DocumentBuilder;
import directory.files.Folder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class FileManagerTest {
    private File resourcesDirectory = new File("src/tests/resTest" + File.separator);
    private Path pathToTestDir = Paths.get(resourcesDirectory + File.separator + "Main Files Test");
    private Path pathToOnlineFileTestFolder = Paths.get(resourcesDirectory + File.separator + "Main Files Test" + File.separator + "onlineFileTest");
    private Path toTestFile = Paths.get(resourcesDirectory + File.separator + "Main Files Test" + File.separator + "testFile1.pdf");
    private Path toTestFile2 = Paths.get(resourcesDirectory + File.separator + "Main Files Test" + File.separator + "testFile.pdf");
    private Path archivePath = Paths.get("Sample files" + File.separator + "Archive");
    private Path pathToJsonTest = Paths.get(resourcesDirectory.getAbsolutePath() + File.separator + "allFilesTest.JSON");
    private Path pathToJsonTestUnix = Paths.get(resourcesDirectory.getAbsolutePath() + File.separator + "allFilesTestUnix.JSON");
    private Path pathToTestFolder = Paths.get(resourcesDirectory.getAbsolutePath() + File.separator + "Main Files Test" + File.separator + "deleteTest");

    @BeforeEach
    void initEach() {
        FileManager.getTestInstance().setPathToJson(pathToJsonTest.toString());
        FileManager.getTestInstance().readFilesFromJson();
    }

    // Todo use FileManager.deleteFile() to delete file.
    @Test
    void uploadFile() throws IOException {
        FileManager.getTestInstance().setPathToJson(pathToJsonTest.toString());
        FileManager.getTestInstance().uploadFile(toTestFile, pathToOnlineFileTestFolder);

        assertTrue(Files.exists( Paths.get(pathToOnlineFileTestFolder.toString() + File.separator + "testFile1.pdf")));

        FileManager.getTestInstance().deleteFile(DocumentBuilder.getInstance().createDocument(Paths.get(pathToOnlineFileTestFolder.toString() + File.separator + "testFile1.pdf")));

        assertTrue(Files.exists(Paths.get(archivePath.toString() + File.separator + "testFile1.pdf")));

        Files.delete(Paths.get(archivePath.toString() + File.separator + "testFile1.pdf"));

        assertFalse(Files.exists(Paths.get(archivePath.toString() + File.separator + "testFile1.pdf")));
    }

    @Test
    void createFolder() {
        Folder folder = FileManager.getTestInstance().createFolder(pathToTestDir, "TestFolder");
        assertEquals("TestFolder", folder.getName());

        try {
            Files.delete(Paths.get(pathToTestDir + File.separator + "TestFolder"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void deleteDocument() throws IOException {
        Document doc = DocumentBuilder.getInstance().createDocument(toTestFile2);

        FileManager.getTestInstance().deleteFile(doc);

        assertTrue(Files.exists(Paths.get(archivePath.toString() + File.separator + doc.getName())));
    }

    void restoreDocument() throws IOException {
        Document doc = DocumentBuilder.getInstance().createDocument(toTestFile2);

        FileManager.getTestInstance().restoreDocument(doc);

        assertTrue(Files.exists(toTestFile2));
    }

    void deleteDocument2() throws IOException {
        Document doc = DocumentBuilder.getInstance().createDocument(toTestFile2);

        FileManager.getTestInstance().deleteFile(doc);

        assertTrue(Files.exists(Paths.get(archivePath.toString() + File.separator + doc.getName())));
    }

    void restoreDocumentWithPath() throws IOException {
        Path newPath = Paths.get(resourcesDirectory.getAbsolutePath() + File.separator + "Main Files Test" + File.separator + "Restore test" + File.separator + "testFile.pdf");

        Document doc = DocumentBuilder.getInstance().createDocument(newPath);

        FileManager fm = new FileManager();

        assertFalse(Files.exists(newPath));

        fm.restoreDocument(doc);

        assertTrue(Files.exists(newPath));

        //deletes folder and moves file back

        Files.move(newPath, toTestFile2);
        Files.delete(newPath.getParent());
    }

    @Test
    void inOrder() throws IOException {
        deleteDocument();
        restoreDocument();
    }

    @Test
    void inOrder2() throws IOException {
        deleteDocument2();
        restoreDocumentWithPath();
    }

}