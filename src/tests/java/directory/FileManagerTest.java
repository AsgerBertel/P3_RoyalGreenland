package directory;

import app.ApplicationMode;
import directory.files.AbstractFile;
import directory.files.Document;
import directory.files.Folder;
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

    @Override
    protected void setSettings() {
        SettingsManager.loadSettings(ApplicationMode.ADMIN);
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
        Document uploadedDoc = FileManager.getInstance().uploadFile(SettingsManager.getServerDocumentsPath().resolve(doc.getOSPath()));

        assertTrue(FileManager.getInstance().getMainFilesRoot().getContents().contains(uploadedDoc));
    }

    @Test
    void uploadFile1() {
        Document uploadedDoc = FileManager.getInstance().uploadFile(SettingsManager.getServerDocumentsPath().resolve(doc.getOSPath()), folder1);

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

        Document movedDoc = FileManager.getInstance().uploadFile(SettingsManager.getServerDocumentsPath().resolve(doc.getOSPath()));

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

        Path originalFilePath = SettingsManager.getServerDocumentsPath().resolve(originalFile.getOSPath());
        Document duplicateFile1 = FileManager.getInstance().uploadFile(originalFilePath, parentFolder);
        Document duplicateFile2 = FileManager.getInstance().uploadFile(originalFilePath, parentFolder);

        // Asserts that the paths are not the same
        assertNotEquals(originalFile.getOSPath(), duplicateFile1.getOSPath());
        assertNotEquals(originalFile.getOSPath(), duplicateFile2.getOSPath());
        assertNotEquals(duplicateFile1.getOSPath(), duplicateFile2.getOSPath());

        // Assert that a number has been appended to the end of the file name
        assertTrue(duplicateFile1.getName().endsWith("(1)." + duplicateFile1.getFileExtension()));
        assertTrue(duplicateFile2.getName().endsWith("(2)." + duplicateFile1.getFileExtension()));
    }

    @Test
    void restoreFile() throws IOException {
        FileManager fileManager = FileManager.getInstance();

        //asserts that doc is not in archive
        assertFalse(fileManager.findFile(doc.getOSPath(), fileManager.getArchiveFiles()).isPresent());
        fileManager.deleteFile(doc);

        //asserts that doc is in archive, and not ind main files
        assertTrue(fileManager.findFile(doc.getOSPath(), fileManager.getArchiveFiles()).isPresent());
        assertFalse(fileManager.findFile(doc.getOSPath(), fileManager.getMainFiles()).isPresent());

        //deletes parent folder to prove that it restores entire path
        fileManager.deleteFile(parentFolder);

        //restores doc
        doc = (Document) fileManager.findFile(doc.getOSPath(), fileManager.getArchiveFiles()).get();
        FileManager.getInstance().restoreFile(doc);

        //asserts that doc is not in archive files, and it is in main files
        assertFalse(fileManager.findFile(doc.getOSPath(), fileManager.getArchiveFiles()).isPresent());
        assertTrue(fileManager.findFile(doc.getOSPath(), fileManager.getMainFiles()).isPresent());
    }

    @Test
    void findParent() {
        Folder folder = FileManager.findParent(doc, FileManager.getInstance().getMainFilesRoot()).get();

        assertEquals(parentFolder, folder);
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