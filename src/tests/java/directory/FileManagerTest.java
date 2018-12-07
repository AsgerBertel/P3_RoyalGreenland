package directory;

import app.ApplicationMode;
import directory.files.AbstractFile;
import directory.files.Folder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
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

    @BeforeEach
    void setSettings(){

        Settings.loadSettings(ApplicationMode.ADMIN);
        folder1 = new Folder(folderPath.toString());

        /*Settings.setServerPath(mainTestDir.resolve("Server"));
        Settings.setLocalPath(mainTestDir.resolve("Local"));
        if (Files.exists(Paths.get(Settings.getServerAppFilesPath() + "allFiles.JSON"))
                && Files.exists(Paths.get(Settings.getServerAppFilesPath() + "currentFileID"))) {
            Files.delete(Paths.get(Settings.getServerAppFilesPath() + "allFiles.JSON"));
            Files.delete(Paths.get(Settings.getServerAppFilesPath() + "currentFileID"));
        }
        System.out.println(Settings.toString2());*/
    }

    @Test
    void getMainFiles() {
        ArrayList<AbstractFile> al;

        al = FileManager.getInstance().getMainFiles();

        assertEquals(folder1.getOSPath(), al.get(0).getOSPath());

    }

    @Test
    void getArchiveFiles() {

        try {
            Files.move(Paths.get(Settings.getServerDocumentsPath().toString() + File.separator + folder1.getOSPath().toString()),
                    Paths.get(Settings.getServerArchivePath().toString() + File.separator + folder1.getOSPath().toString()));
        } catch (IOException e) {
            System.out.println("could not move");
            e.printStackTrace();
        }

        ArrayList<AbstractFile> al;

        al = FileManager.getInstance().getArchiveFiles();

        assertEquals(al.get(0).getOSPath(), folder1.getOSPath());

    }

    @Test
    void uploadFile() {
    }

    @Test
    void uploadFile1() {
    }

    @Test
    void createFolder() {
    }

    @Test
    void createFolder1() {
    }

    @Test
    void deleteFile() {
    }

    @Test
    void generateUniqueFileName() {
    }

    @Test
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
    }
}