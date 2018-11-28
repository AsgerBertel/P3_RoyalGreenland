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

import static org.junit.jupiter.api.Assertions.assertTrue;

class FileManagerTest {
    private File resourcesDirectory = new File("src/tests/resTest");
    private Path pathToTestDir = Paths.get(resourcesDirectory + File.separator + "Main Files Test");
    private Path pathToOnlineFileTestFolder = Paths.get(resourcesDirectory + File.separator + "Main Files Test" + File.separator + "onlineFileTest");
    private Path toTestFile = Paths.get(resourcesDirectory + File.separator + "Main Files Test" + File.separator + "testFile1.pdf");
    private Path toTestFile2 = Paths.get(resourcesDirectory + File.separator + "Main Files Test" + File.separator + "testFile.pdf");
    private Path archivePath = Paths.get("Sample files" + File.separator + "Archive");
    private Path pathToJsonTest = Paths.get(resourcesDirectory.getAbsolutePath() + File.separator + "Main Files Test/RLFiles/Server/App Files/allFiles.JSON");
    private Path pathToJsonTestUnix = Paths.get(resourcesDirectory.getAbsolutePath() + File.separator + "allFilesTestUnix.JSON");
    private Path pathToTestFolder = Paths.get(resourcesDirectory.getAbsolutePath() + File.separator + "Main Files Test" + File.separator + "deleteTest");
    private Path mainTestDir = Paths.get(resourcesDirectory + File.separator+ "Main Files Test" +File.separator+"RLFiles");

    @BeforeEach
    void initEach() throws IOException {
        Settings.setServerPath(mainTestDir.toString()+File.separator+"Server");
        Settings.setLocalPath(mainTestDir.toString()+File.separator+"Local");
        if(Files.exists(Paths.get(Settings.getServerAppFilesPath()+"allFiles.JSON"))
                && Files.exists(Paths.get(Settings.getServerAppFilesPath() + "currentFileID"))) {
            Files.delete(Paths.get(Settings.getServerAppFilesPath() + "allFiles.JSON"));
            Files.delete(Paths.get(Settings.getServerAppFilesPath() + "currentFileID"));
        }
        System.out.println(Settings.toString2());
    }

    // Todo use FileManager.deleteFile() to delete file.
   /* @Test
    void uploadFile() throws IOException {
        FileManager.getTestInstance().setPathToJson(pathToJsonTest.toString());
        FileManager.getTestInstance().uploadFile(toTestFile, pathToOnlineFileTestFolder);

        assertTrue(Files.exists( Paths.get(pathToOnlineFileTestFolder.toString() + File.separator + "testFile1.pdf")));

        FileManager.getTestInstance().deleteFile(DocumentBuilder.getInstance().createDocument(Paths.get(pathToOnlineFileTestFolder.toString() + File.separator + "testFile1.pdf")));

        assertTrue(Files.exists(Paths.get(archivePath.toString() + File.separator + "testFile1.pdf")));

        Files.delete(Paths.get(archivePath.toString() + File.separator + "testFile1.pdf"));

        assertFalse(Files.exists(Paths.get(archivePath.toString() + File.separator + "testFile1.pdf")));
    }*/

/*    @Test
    void deletePlant() {
        Folder folder = FileManager.getTestInstance().deletePlant(pathToTestDir, "TestFolder");
        assertEquals("TestFolder", folder.getName());

        try {
            Files.delete(Paths.get(pathToTestDir + File.separator + "TestFolder"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    private void deleteDocument() {
        Folder folder1 = (Folder)FileManager.getInstance().getMainFiles().get(1);
        Folder folder2 = (Folder)folder1.getContents().get(0);
        Document document = (Document)folder2.getContents().get(0);

        FileManager.getInstance().deleteFile(document);

        //assertTrue(Files.exists(Paths.get(archivePath.toString() + File.separator + doc.getName())));
    }

    private void restoreDocument() throws IOException {
        Folder folder1 = (Folder)FileManager.getInstance().getArchiveFiles().get(0);
        Folder folder2 = (Folder)folder1.getContents().get(0);
        Document doc = (Document)folder2.getContents().get(0);

        FileManager.getInstance().restoreFile(doc);

        //assertTrue(Files.exists(toTestFile2));
    }
/*
    private void deleteDocument2() throws IOException {
        Document doc = DocumentBuilder.getInstance().createDocument(toTestFile2);

        FileManager.getTestInstance().deleteFile(doc);

        assertTrue(Files.exists(Paths.get(archivePath.toString() + File.separator + doc.getName())));
    }*/

    @Test
    void inOrder() throws IOException {
        deleteDocument();
        restoreDocument();
    }

  /*  @Test
    void restoreFolder () throws IOException {
        Folder folder = FileManager.getTestInstance().deletePlant(Paths.get(pathToTestDir.toString() + File.separator + "restoreFolderTest"), "");

        FileManager.getTestInstance().deleteFile(folder);

        assertFalse(Files.exists(folder.getPath()));
        assertTrue(Files.exists(Paths.get(archivePath.toString() + File.separator + "restoreFolderTest")));

        FileManager.getTestInstance().restoreFile(folder);

        assertTrue(Files.exists(folder.getPath()));
        assertFalse(Files.exists(Paths.get(archivePath.toString() + File.separator + "restoreFolderTest")));

    }*/

}