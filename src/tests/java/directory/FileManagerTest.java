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
    private Path pathToTestDir = Paths.get(resourcesDirectory.getAbsolutePath() + File.separator + "Main Files Test");
    private Path pathToOnlineFileTestFolder = Paths.get(resourcesDirectory.getAbsolutePath() + File.separator + "Main Files Test" + File.separator + "onlineFileTest");
    private Path toTestFile = Paths.get(resourcesDirectory.getAbsolutePath() + File.separator + "Main Files Test" + File.separator + "testFile.pdf");
    private Path archivePath = Paths.get("Sample files" + File.separator + "Archive");
    private Path pathToJsonTest = Paths.get(resourcesDirectory.getAbsolutePath() + File.separator + "allFilesTest.JSON");
    private Path pathToJsonTestUnix = Paths.get(resourcesDirectory.getAbsolutePath() + File.separator + "allFilesTestUnix.JSON");

    @BeforeEach
    void initEach() {
        FileManager.getInstance().setPathToJson(pathToJsonTest.toString());
        FileManager.getInstance().readFromJsonFile();
    }

    // Todo use FileManager.deleteFile() to delete file.
    @Test
    void uploadFile() {
        FileManager.getInstance().setPathToJson(pathToJsonTest.toString());
        FileManager.getInstance().uploadFile(toTestFile, pathToOnlineFileTestFolder);
        assertTrue(Files.exists( Paths.get(pathToOnlineFileTestFolder.toString() + File.separator + "testFile.pdf")));

        try {
            Files.delete(Paths.get(pathToOnlineFileTestFolder.toString() + File.separator + "testFile.pdf"));
        } catch (IOException e) {
            System.out.println("UploadFileTest: ");
            e.printStackTrace();
        }

        assertEquals("testFile.pdf", FileManager.getInstance().allContent.get(0).getName());
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
        Document doc = DocumentBuilder.getInstance().createDocument(toTestFile);

        FileManager fm = new FileManager();

        fm.deleteFile(doc);

        assertEquals(toTestFile.toString(), doc.getPath().toString());
        assertTrue(Files.exists(Paths.get(archivePath.toString() + File.separator + doc.getName())));
    }

    void restoreDocument() throws IOException {
        Document doc = DocumentBuilder.getInstance().createDocument(toTestFile);

        FileManager fm = new FileManager();

        fm.restoreDocument(doc);
    }

    void deleteDocument2() throws IOException {
        Document doc = DocumentBuilder.getInstance().createDocument(toTestFile);

        FileManager fm = new FileManager();

        fm.deleteFile(doc);
    }

    void restoreDocumentWithPath() throws IOException {
        Path newPath = Paths.get(resourcesDirectory.getAbsolutePath() + File.separator + "Main Files Test" + File.separator + "Restore test" + File.separator + "testFile.pdf");

        Document doc = DocumentBuilder.getInstance().createDocument(newPath);

        FileManager fm = new FileManager();

        fm.restoreDocument(doc);

        //deletes folder and moves file back

        Files.move(newPath, toTestFile);
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

    @Test
    void deleteFolder() throws IOException {
        Folder folder = new Folder("C:\\Users\\Hanna\\IdeaProjects\\P3\\src\\tests\\resTest\\Main Files Test\\deleteTest");

        FileManager fm = new FileManager();

        fm.deleteFile(folder);
    }
}