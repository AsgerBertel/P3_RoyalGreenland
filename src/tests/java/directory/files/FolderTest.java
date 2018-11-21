package directory.files;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

class FolderTest {
    private File resourcesDirectory = new File("src/tests/resTest" + File.separator);
    private Path pathToJsonTest = Paths.get(resourcesDirectory.getAbsolutePath() + File.separator + "Main Files Test/RLFiles/Server/App Files/allFiles.JSON");

/*    @Test
    void renameFile() {
        Path pathToTestDir = Paths.get("src/tests/resTest/Main Files Test");
        File newDirectory = new File(pathToTestDir.toString());

        if (!newDirectory.exists())
            newDirectory.mkdirs();

        FileManager.getTestInstance().setPathToJson(pathToJsonTest.toString());

        Folder folder = FileManager.getTestInstance().createFolder(newDirectory.toPath(), "renameTestFolder");

        folder.renameFile("renamedTestFolder");
        assertEquals("renamedTestFolder", folder.getName());
        assertTrue(new File(pathToTestDir.toString() + File.separator + "renamedTestFolder").exists());
        assertTrue(new File(pathToTestDir.toString() + File.separator + "renamedTestFolder").delete());
    }

    @Test
    void getContents() {
    }*/
}