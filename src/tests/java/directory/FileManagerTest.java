package directory;

import app.ApplicationMode;
import directory.files.AbstractFile;
import directory.files.Document;
import directory.files.DocumentBuilder;
import directory.files.Folder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.print.Doc;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class FileManagerTest extends FileTester {

    private File resourcesDirectory = new File("src/tests/resTest");
    private Path pathToTestDir = Paths.get(resourcesDirectory + File.separator + "Main Files Test");
    private Path pathToOnlineFileTestFolder = Paths.get(resourcesDirectory + File.separator + "Main Files Test" + File.separator + "onlineFileTest");
    private Path toTestFile = Paths.get(resourcesDirectory + File.separator + "Main Files Test" + File.separator + "testFile1.pdf");
    private Path toTestFile2 = Paths.get(resourcesDirectory + File.separator + "Main Files Test" + File.separator + "testFile.pdf");
    private Path archivePath = Paths.get("Sample files" + File.separator + "Archive");
    private Path pathToJsonTest = Paths.get(resourcesDirectory.getAbsolutePath() + File.separator + "Main Files Test/RLFiles/Server/App Files/allFiles.JSON");
    private Path pathToJsonTestUnix = Paths.get(resourcesDirectory.getAbsolutePath() + File.separator + "allFilesTestUnix.JSON");
    private Path pathToTestFolder = Paths.get(resourcesDirectory.getAbsolutePath() + File.separator + "Main Files Test" + File.separator + "deleteTest");
    private Path mainTestDir = Paths.get(resourcesDirectory + File.separator + "Main Files Test" + File.separator + "RLFiles");

    Path folderPath = Paths.get("02_VINTERTØRRET FISK");
    Folder folder1;
    Path docPath = Paths.get("03_URENSET STENBIDERROGN/GMP 03 GR_02.pdf");
    Document doc;
    Path doc2Path = Paths.get("02_VINTERTØRRET FISK/HA 02 GR_02  HACCP plan for indfrysning af fisk.doc");
    Document doc2;

    @BeforeEach
    void setSettings(){
        Settings.loadSettings(ApplicationMode.ADMIN);
        folder1 = (Folder) findInMainFiles(folderPath);
        doc = (Document) findInMainFiles(docPath);
        doc2 = (Document) findInMainFiles(doc2Path);
    }

    @Test
    void getMainFiles() {
        ArrayList<AbstractFile> al;

        al = FileManager.getInstance().getMainFiles();

        assertEquals(folder1.getOSPath(), al.get(0).getOSPath());

    }

    @Test
    void getArchiveFiles() {

        FileManager.getInstance().deleteFile(folder1);

        ArrayList<AbstractFile> al;

        al = FileManager.getInstance().getArchiveFiles();

        assertEquals(al.get(0).getOSPath(), folder1.getOSPath());
    }

    @Test
    void uploadFile() {
        Document uploadedDoc = FileManager.getInstance().uploadFile(Settings.getServerDocumentsPath().resolve(doc.getOSPath()));

        assertTrue(FileManager.getInstance().getMainFilesRoot().getContents().contains(uploadedDoc));
    }

    @Test
    void uploadFile1() {
        Document uploadedDoc = FileManager.getInstance().uploadFile(Settings.getServerDocumentsPath().resolve(doc.getOSPath()), folder1);

        assertTrue(folder1.getContents().contains(uploadedDoc));
    }

    @Test
    void createFolder() throws IOException {
        Folder createdFolder = FileManager.getInstance().createFolder("new folder");

        assertTrue(FileManager.getInstance().getMainFilesRoot().getContents().contains(createdFolder));
    }

    @Test
    void createFolder1() throws IOException {
        Folder createdFolder = FileManager.getInstance().createFolder("new folder", folder1);

        assertTrue(folder1.getContents().contains(createdFolder));
    }

    @Test
    void deleteFile() {

        Document movedDoc = FileManager.getInstance().uploadFile(Settings.getServerDocumentsPath().resolve(doc.getOSPath()));

        assertTrue(FileManager.getInstance().getMainFiles().contains(movedDoc));
        assertTrue(FileManager.getInstance().getMainFiles().contains(folder1));

        FileManager.getInstance().deleteFile(movedDoc);
        FileManager.getInstance().deleteFile(folder1);

        assertFalse(FileManager.getInstance().getMainFiles().contains(movedDoc));
        assertFalse(FileManager.getInstance().getMainFiles().contains(folder1));
    }

    @Test
    void generateUniqueFileName(){
        // Upload the same file multiple times
        Document originalFile = (Document) findInMainFiles(Paths.get("02_VINTERTØRRET FISK/HA 02 GR_02  HACCP plan for indfrysning af fisk.pdf"));
        Folder parentFolder = (Folder) findInMainFiles(Paths.get("02_VINTERTØRRET FISK"));

        Path originalFilePath = Settings.getServerDocumentsPath().resolve(originalFile.getOSPath());
        Document duplicateFile1 = FileManager.getInstance().uploadFile(originalFilePath, parentFolder);
        Document duplicateFile2 = FileManager.getInstance().uploadFile(originalFilePath, parentFolder);

        System.out.println(duplicateFile1.getName());
        System.out.println(duplicateFile2.getName());

    }

    /*@Test
    void restoreFile() {
    }

    @Test
    void findParent() {
    }

    @Test
    void save() {
    }

    @Test
    void getMainFilesRoot() {
    }

    @Test
    void getArchiveRoot() {
    }

    @Test
    void findInMainFiles() {
    }

    @Test
    void moveFile() {
    }

    @Test
    void renameFile() {
    }*/
}