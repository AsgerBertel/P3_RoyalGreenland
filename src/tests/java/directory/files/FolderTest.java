package directory.files;

import app.ApplicationMode;
import directory.FileTester;
import directory.SettingsManager;
import directory.plant.AccessModifier;
import directory.plant.Plant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class FolderTest extends FileTester {
    //private File resourcesDirectory = new File("src/tests/resTest" + File.separator);
    //private Path pathToJsonTest = Paths.get(resourcesDirectory.getAbsolutePath() + File.separator + "Main Files Test/RLFiles/Server/App Files/allFiles.JSON");
    private Path pathToTestFolder = Paths.get("03_URENSET STENBIDERROGN");
    private Path pathToNewFolder = Paths.get("02_VINTERTØRRET FISK");
    private Path pathToDoc = Paths.get("03_URENSET STENBIDERROGN/GMP 03 GR_02.pdf");
    Folder folderTest;
    Document doc;
    Plant plant;
    AccessModifier am;

    @BeforeEach
    void setSettings(){
        SettingsManager.loadSettings(ApplicationMode.ADMIN);
        folderTest = new Folder(pathToTestFolder.toString());
        doc = DocumentBuilder.getInstance().createDocument(pathToDoc);
        folderTest.getContents().add(doc);
        am = new AccessModifier();
        plant = new Plant(1234, "plant", am);
    }

    @Test
    public void setPath(){
        assertEquals(pathToTestFolder, folderTest.getOSPath());

        folderTest.setPath(pathToNewFolder);

        assertEquals(pathToNewFolder, folderTest.getOSPath());
    }

    @Test
    public void setName(){
        String newName = "04_RENSET STENBIDERROGN";

        assertNotEquals(newName, folderTest.getName());

        folderTest.setName(newName);

        assertEquals(newName, folderTest.getName());
    }

    @Test
    public void updateChildrenPaths(){
        String newName = "new name";

        assertNotEquals(newName, doc.getParentPath().getFileName().toString());

        folderTest.setName(newName);

        assertEquals(newName, doc.getParentPath().getFileName().toString());
    }

    @Test
    public void getContents(){
        assertTrue(folderTest.getContents().contains(doc));

        folderTest.getContents().remove(doc);

        assertFalse(folderTest.getContents().contains(doc));
    }

    @Test
    public void containsFromAccessModifier(){

        assertFalse(folderTest.containsFromAccessModifier(plant.getAccessModifier()));

        plant.getAccessModifier().addDocument(doc.getID());

        assertTrue(folderTest.containsFromAccessModifier(plant.getAccessModifier()));
    }

    @Test
    public void isSubFolderOf(){

        Folder parentFolder = new Folder(folderTest.getParentPath().toString());

        parentFolder.getContents().add(folderTest);

        assertTrue(folderTest.isSubFolderOf(parentFolder));
        assertFalse(parentFolder.isSubFolderOf(folderTest));
        assertFalse(folderTest.isSubFolderOf(folderTest));
    }
}