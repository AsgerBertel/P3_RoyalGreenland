package model.managing;

import app.ApplicationMode;
import model.FileTester;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileExplorerTest extends FileTester {

    private FileExplorer fe;
    private Document docInAM;
    private Document docFalse;
    private final Path pathToDoc1 = Paths.get("04_MASKINTØRRET FISK/FL 04 GR_02   Flowdiagram for produktion af maskintørret fisk.pdf");
    private final Path pathToDoc2 = Paths.get("04_MASKINTØRRET FISK/PB 04 GR_02   Procesbeskrivelse for produktion af maskintørret fisk.pdf");
    private final Path pathToFolder = Paths.get("04_MASKINTØRRET FISK");
    private Folder folder;
    private Plant plant;
    private AccessModifier am;

    private final Path pathToKAL = Paths.get("02_VINTERTØRRET FISK/KAL");
    private Folder KALFolder;
    private final Path pathToKALParent = Paths.get("02_VINTERTØRRET FISK");
    private Folder KALParentFolder;
    private final Path pathToKALDoc = Paths.get("02_VINTERTØRRET FISK/KAL/GFL 02 GR_02 Flowdiagram for produktion af vintertørret fisk.pdf");
    private Document KALDoc;

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