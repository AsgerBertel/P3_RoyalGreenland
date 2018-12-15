package directory;

import app.ApplicationMode;
import directory.files.AbstractFile;
import directory.files.Document;
import directory.files.DocumentBuilder;
import directory.files.Folder;
import directory.plant.AccessModifier;
import directory.plant.Plant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileExplorerTest extends FileTester {

    FileExplorer fe;
    Document docInAM;
    Document docFalse;
    Path pathToDoc1 = Paths.get("04_MASKINTØRRET FISK/FL 04 GR_02   Flowdiagram for produktion af maskintørret fisk.pdf");
    Path pathToDoc2 = Paths.get("04_MASKINTØRRET FISK/PB 04 GR_02   Procesbeskrivelse for produktion af maskintørret fisk.pdf");
    Path pathToFolder = Paths.get("04_MASKINTØRRET FISK");
    Folder folder;
    Plant plant;
    AccessModifier am;

    Path pathToKAL = Paths.get("02_VINTERTØRRET FISK/KAL");
    Folder KALFolder;
    Path pathToKALParent = Paths.get("02_VINTERTØRRET FISK");
    Folder KALParentFolder;
    Path pathToKALDoc = Paths.get("02_VINTERTØRRET FISK/KAL/GFL 02 GR_02 Flowdiagram for produktion af vintertørret fisk.pdf");
    Document KALDoc;

    @BeforeEach
    protected void setSettings(){
        SettingsManager.loadSettings(ApplicationMode.ADMIN);
        docInAM = (Document) findInMainFiles(pathToDoc1);
        docFalse = (Document) findInMainFiles(pathToDoc2);
        KALDoc = (Document) findInMainFiles(pathToKALDoc);
        am = new AccessModifier();
        am.addDocument(docInAM.getID());
        am.addDocument(KALDoc.getID());
        plant = new Plant(1234, "plant", am);
        fe = new FileExplorer(FileManager.getInstance().getMainFiles(), plant);
        folder = (Folder) findInMainFiles(pathToFolder);
        KALFolder = (Folder) findInMainFiles(pathToKAL);
        KALParentFolder = (Folder) findInMainFiles(pathToKALParent);
    }

    @RepeatedTest(value = 2)
    void getShownFiles(){

        List<AbstractFile> shownFiles = fe.getShownFiles();

        assertTrue(shownFiles.contains(KALParentFolder));

        assertEquals(2, shownFiles.size());

        assertFalse(shownFiles.contains(docFalse));
    }

    @RepeatedTest(value = 2)
    void navigateTo() {
        assertNotEquals(folder.getOSPath().toString(), fe.getCurrentPath());

        fe.navigateTo(folder);

        assertEquals(folder.getOSPath().toString(), fe.getCurrentPath());
    }

    @RepeatedTest(value = 2)
    void navigateBack() {
        fe.navigateTo(KALFolder);

        assertTrue(fe.navigateBack());

        assertEquals(KALParentFolder.getOSPath().toString(), fe.getCurrentPath());
    }

    @RepeatedTest(value = 2)
    void getCurrentPath() {
        assertEquals("", fe.getCurrentPath());

        fe.navigateTo(folder);

        assertEquals(folder.getOSPath().toString(), fe.getCurrentPath());
    }
}