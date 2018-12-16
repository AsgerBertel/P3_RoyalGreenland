package model;

import app.ApplicationMode;
import model.managing.SettingsManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class FolderTest extends FileTester {
    private final Path pathToTestFolder = Paths.get("03_URENSET STENBIDERROGN");
    private final Path pathToNewFolder = Paths.get("02_VINTERTØRRET FISK");
    private final Path pathToDoc = Paths.get("03_URENSET STENBIDERROGN/GMP 03 GR_02.pdf");
    private Folder folderTest;
    private Document doc;
    private Plant plant;
    private AccessModifier am;

    @BeforeEach
    protected void setSettings(){
        SettingsManager.loadSettings(ApplicationMode.ADMIN);
        folderTest = (Folder) findInMainFiles(pathToTestFolder);
        doc = (Document) findInMainFiles(pathToDoc);
        am = new AccessModifier();
        plant = new Plant(1234, "plant", am);
    }

    @Test
    void setPath(){
        assertEquals(pathToTestFolder, folderTest.getOSPath());

        folderTest.setPath(pathToNewFolder);

        assertEquals(pathToNewFolder, folderTest.getOSPath());
    }

    @Test
    void setName(){
        String newName = "04_RENSET STENBIDERROGN";

        assertNotEquals(newName, folderTest.getName());

        folderTest.setName(newName);

        assertEquals(newName, folderTest.getName());
    }

    @Test
    void updateChildrenPaths(){
        String newName = "new name";

        assertNotEquals(newName, doc.getParentPath().getFileName().toString());

        folderTest.setName(newName);

        assertEquals(newName, doc.getParentPath().getFileName().toString());
    }

    @Test
    void getContents(){
        assertTrue(folderTest.getContents().contains(doc));

        folderTest.getContents().remove(doc);

        assertFalse(folderTest.getContents().contains(doc));
    }

    @Test
    void containsFromAccessModifier(){

        assertFalse(folderTest.containsFromAccessModifier(plant.getAccessModifier()));

        plant.getAccessModifier().addDocument(doc.getID());

        assertTrue(folderTest.containsFromAccessModifier(plant.getAccessModifier()));
    }

    @Test
    void isSubFolderOf(){

        Folder parentFolder = new Folder(folderTest.getParentPath().toString());

        parentFolder.getContents().add(folderTest);

        assertTrue(folderTest.isSubFolderOf(parentFolder));
        assertFalse(parentFolder.isSubFolderOf(folderTest));
        assertFalse(folderTest.isSubFolderOf(folderTest));
    }
}