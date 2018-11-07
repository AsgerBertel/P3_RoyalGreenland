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
        FileManager.getInstance().readFromJsonFile();
    }

    // Todo use FileManager.deleteFile() to delete file.
    @Test
    void uploadFile() throws IOException {
        FileManager.getInstance().setPathToJson(pathToJsonTest.toString());
        FileManager.getInstance().uploadFile(toTestFile, pathToOnlineFileTestFolder);
        assertTrue(Files.exists( Paths.get(pathToOnlineFileTestFolder.toString() + File.separator + "testFile1.pdf")));

        try {
            Files.delete(Paths.get(pathToOnlineFileTestFolder.toString() + File.separator + "testFile1.pdf"));
        } catch (IOException e) {
            System.out.println("UploadFileTest: ");
            e.printStackTrace();
        }

        //assertEquals("testFile1.pdf", FileManager.getInstance().allContent.get(0).getName());
        assertEquals("Main Files", FileManager.getInstance().allContent.get(0).getName());
    }

    @Test
    void createFolder() {
        Folder folder = FileManager.getInstance().createFolder(pathToTestDir, "TestFolder");
        assertEquals("TestFolder", folder.getName());

        try {
            Files.delete(Paths.get(pathToTestDir + File.separator + "TestFolder"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void deleteDocument() throws IOException {
        Document doc = DocumentBuilder.getInstance().createDocument(toTestFile2);

        FileManager.getInstance().deleteFile(doc);

        //assertEquals(toTestFile2.toString(), doc.getPath().toString());
        assertTrue(Files.exists(Paths.get(archivePath.toString() + File.separator + doc.getName())));
    }

    void restoreDocument() throws IOException {
        Document doc = DocumentBuilder.getInstance().createDocument(toTestFile2);

        FileManager.getInstance().restoreDocument(doc);
    }

    void deleteDocument2() throws IOException {
        Document doc = DocumentBuilder.getInstance().createDocument(toTestFile2);

        FileManager.getInstance().deleteFile(doc);
    }

    void restoreDocumentWithPath() throws IOException {
        Path newPath = Paths.get(resourcesDirectory.getAbsolutePath() + File.separator + "Main Files Test" + File.separator + "Restore test" + File.separator + "testFile.pdf");

        Document doc = DocumentBuilder.getInstance().createDocument(newPath);

        FileManager fm = new FileManager();

        fm.restoreDocument(doc);

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

    /*@Test
    void deleteFolder() throws IOException {
        Folder folder = new Folder(pathToTestFolder.toString());

        FileManager.getInstance().deleteFile(folder);
    }*/
}