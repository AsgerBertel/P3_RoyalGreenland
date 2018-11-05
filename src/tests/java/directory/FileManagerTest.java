package directory;
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
    private File resourcesDirectory = new File("src/tests/resTest" + File.separator);
    private Path pathToTestDir = Paths.get(resourcesDirectory.getAbsolutePath() + File.separator + "Main Files Test");
    private Path pathToOnlineFileTestFolder = Paths.get(resourcesDirectory.getAbsolutePath() + File.separator + "Main Files Test" + File.separator + "onlineFileTest");
    private Path toTestFile = Paths.get(resourcesDirectory.getAbsolutePath() + File.separator + "Main Files Test" + File.separator + "testFile.pdf");
    private Path pathToJsonTest = Paths.get(resourcesDirectory.getAbsolutePath() + File.separator + "allFilesTest.JSON");

    @Test
    void initEach(){
        FileManager.getInstance().readFromJsonFile(pathToJsonTest.toString());
        System.out.println(FileManager.getInstance().allContent.get(0).getName());
    }

    @Test
    void uploadFile() {
        FileManager.getInstance().setPathToJson(pathToJsonTest.toString());
        FileManager.getInstance().updateJsonFile();

        FileManager.getInstance().uploadFile(toTestFile, pathToOnlineFileTestFolder);

        assertTrue(Files.exists( Paths.get(pathToOnlineFileTestFolder.toString() + File.separator + "testFile.pdf")));

        try {
            Files.delete(Paths.get(pathToOnlineFileTestFolder.toString() + File.separator + "testFile.pdf"));
        } catch (IOException e) {
            System.out.println("UploadFileTest: ");
            e.printStackTrace();
        }
    }

    @Test
    void createFolder() {
        Folder folder = FileManager.getInstance().createFolder(pathToTestDir, "TestFolder");
        assertEquals("TestFolder" ,folder.getName());

        try {
            Files.delete(Paths.get(pathToTestDir + File.separator + "TestFolder"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void deleteFile() {
    }
}