package directory;

import directory.files.Document;
import directory.files.DocumentBuilder;
import directory.files.Folder;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileManagerTest {
    File resourcesDirectory = new File("src/tests/resTest" + File.separator);
    Path pathToTestDir = Paths.get(resourcesDirectory.getAbsolutePath() + File.separator + "Main Files Test");
    Path pathToOnlineFileTestFolder = Paths.get(resourcesDirectory.getAbsolutePath() + File.separator + "Main Files Test" + File.separator + "onlineFileTest");
    Path toTestFile = Paths.get(resourcesDirectory.getAbsolutePath() + File.separator + "Main Files Test" + File.separator + "testFile.pdf");
    Path archivePath = Paths.get("Sample files" + File.separator + "Archive");

    @Test
    void uploadFile() {
        FileManager.uploadFile(toTestFile, pathToOnlineFileTestFolder);

        assertTrue(Files.exists(Paths.get(pathToOnlineFileTestFolder.toString() + File.separator + "testFile.pdf")));

        try {
            Files.delete(Paths.get(pathToOnlineFileTestFolder.toString() + File.separator + "testFile.pdf"));
        } catch (IOException e) {
            System.out.println("UploadFileTest: ");
            e.printStackTrace();
        }
    }

    @Test
    void createFolder() {
        Folder folder = FileManager.createFolder(pathToTestDir, "TestFolder");
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

        fm.deleteDocument(doc);

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

        fm.deleteDocument(doc);
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

    void deleteDocument3() throws IOException {
        Document doc = DocumentBuilder.getInstance().createDocument(toTestFile);

        FileManager fm = new FileManager();

        fm.deleteDocument(doc);
    }

    void restoreDocumentWithPath2() throws IOException {
        Path newPath = Paths.get(resourcesDirectory.getAbsolutePath() + File.separator + "Main Files Test" + File.separator + "Restore test" + File.separator + "Mega test" + File.separator + "Ultra test" + File.separator + "testFile.pdf");

        Document doc = DocumentBuilder.getInstance().createDocument(newPath);

        FileManager fm = new FileManager();

        fm.restoreDocument(doc);

    }

    void deleteEmptyFolders() throws IOException {
        Path newPath = Paths.get(resourcesDirectory.getAbsolutePath() + File.separator + "Main Files Test" + File.separator + "Restore test" + File.separator + "Mega test" + File.separator + "Ultra test" + File.separator + "testFile.pdf");

        Document doc = DocumentBuilder.getInstance().createDocument(newPath);

        FileManager fm = new FileManager();

        fm.deleteDocument(doc);

    }


    void restoreDocument2() throws IOException {
        Document doc = DocumentBuilder.getInstance().createDocument(toTestFile);

        FileManager fm = new FileManager();

        fm.restoreDocument(doc);
    }

    @Test
    void inOrder() throws IOException {
        deleteDocument();
        restoreDocument();
        deleteDocument2();
        restoreDocumentWithPath();
        deleteDocument3();
        restoreDocumentWithPath2();
        deleteEmptyFolders();
        restoreDocument2();
    }
}