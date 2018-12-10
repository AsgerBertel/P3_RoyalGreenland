package directory;

import app.ApplicationMode;
import directory.files.AbstractFile;
import directory.files.Document;
import directory.files.Folder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class FileManagerTest extends FileTester {

    Path folderPath = Paths.get("02_VINTERTØRRET FISK");
    Folder folder1;
    Path docPath = Paths.get("03_URENSET STENBIDERROGN/GMP 03 GR_02.pdf");
    Document doc;
    Path doc2Path = Paths.get("02_VINTERTØRRET FISK/HA 02 GR_02  HACCP plan for indfrysning af fisk.doc");
    Document doc2;
    Path parentFolderPath = Paths.get("03_URENSET STENBIDERROGN");
    Folder parentFolder;

    @BeforeEach
    void setSettings() {
        Settings.loadSettings(ApplicationMode.ADMIN);
        folder1 = (Folder) findInMainFiles(folderPath);
        doc = (Document) findInMainFiles(docPath);
        doc2 = (Document) findInMainFiles(doc2Path);
        parentFolder = (Folder) findInMainFiles(parentFolderPath);
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
        //assertTrue(FileManager.getInstance().getArchiveFiles().contains(movedDoc));

    }

    @Test
    void generateUniqueFileName() {
        // Upload the same file multiple times
        Document originalFile = (Document) findInMainFiles(Paths.get("02_VINTERTØRRET FISK/HA 02 GR_02  HACCP plan for indfrysning af fisk.pdf"));
        Folder parentFolder = (Folder) findInMainFiles(Paths.get("02_VINTERTØRRET FISK"));

        Path originalFilePath = Settings.getServerDocumentsPath().resolve(originalFile.getOSPath());
        Document duplicateFile1 = FileManager.getInstance().uploadFile(originalFilePath, parentFolder);
        Document duplicateFile2 = FileManager.getInstance().uploadFile(originalFilePath, parentFolder);

        // todo should probably just assert that none of the files (originalFile, duplicate1 and duplicate2) have the same paths instead
        // Assert that a number has been appended to the end of the file name
        assertTrue(duplicateFile1.getName().endsWith("(1)." + duplicateFile1.getFileExtension()));
        assertTrue(duplicateFile2.getName().endsWith("(2)." + duplicateFile1.getFileExtension()));
    }

    @Test
    void restoreFile() throws IOException {

        FileManager.getInstance().deleteFile(doc);

        //todo getArchiveFiles doesnt work

        //assertTrue(FileManager.getInstance().getArchiveFiles().contains(doc));
        assertFalse(FileManager.getInstance().getMainFiles().contains(doc));

        FileManager.getInstance().restoreFile(doc);

        //todo restore doesnt restore file back into mainfiles
        //assertTrue(FileManager.getInstance().getMainFiles().contains(doc));

        //todo doesnt work with folders either
        FileManager.getInstance().deleteFile(folder1);

        //assertTrue(FileManager.getInstance().getArchiveFiles().contains(folder1));
        assertFalse(FileManager.getInstance().getMainFiles().contains(folder1));


    }

    @Test
    void findParent() {
        Folder folder = FileManager.findParent(doc, FileManager.getInstance().getMainFilesRoot()).get();

        assertEquals(parentFolder, folder);
    }

    @Test
    void save() {
        //todo do we need tests for this?
    }

    @Test
    void getMainFilesRoot() {
        Folder folder = FileManager.getInstance().getMainFilesRoot();

        Folder parentFolder = FileManager.findParent(folder1, FileManager.getInstance().getMainFilesRoot()).get();

        assertEquals(folder, parentFolder);
    }

    @Test
    void getArchiveRoot() {
        Folder folder = FileManager.getInstance().getArchiveRoot();

        FileManager.getInstance().deleteFile(folder1);

        Folder parentFolder = FileManager.findParent(folder1, FileManager.getInstance().getArchiveRoot()).get();

        assertEquals(folder, parentFolder);
    }

    @Test
    void findInMainFiles() {
        Folder folder = (Folder) FileManager.getInstance().findFile(folder1.getOSPath(), FileManager.getInstance().getMainFiles()).get();

        assertEquals(folder, folder1);
    }

    @Test
    void moveFile() throws IOException {

        assertTrue(parentFolder.getContents().contains(doc));
        assertFalse(folder1.getContents().contains(doc));

        FileManager.getInstance().moveFile(doc, folder1);

        assertTrue(folder1.getContents().contains(doc));
        assertFalse(parentFolder.getContents().contains(doc));
    }

    @Test
    void renameFile() throws FileAlreadyExistsException {
        assertNotEquals("new file", doc.getName());
        assertFalse(doc.getOSPath().toString().contains("new file"));

        FileManager.getInstance().renameFile(doc, "new file");

        assertEquals("new file", doc.getName());
        assertTrue(doc.getOSPath().toString().contains("new file"));
    }
}