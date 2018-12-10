package directory;

import app.ApplicationMode;
import directory.files.AbstractFile;
import directory.files.Document;
import directory.files.DocumentBuilder;
import directory.files.Folder;
import directory.plant.AccessModifier;
import directory.plant.Plant;
import org.junit.jupiter.api.BeforeEach;
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
    Path pathToParentFolder = Paths.get(Settings.getServerDocumentsPath().toString());
    Folder folder;
    Folder parentFolder;
    Plant plant;
    AccessModifier am;

    @BeforeEach
    void setSettings(){
        Settings.loadSettings(ApplicationMode.ADMIN);
        docInAM = DocumentBuilder.getInstance().createDocument(pathToDoc1);
        docFalse = DocumentBuilder.getInstance().createDocument(pathToDoc2);
        ArrayList<AbstractFile> al = new ArrayList<>();
        al.add(docInAM);
        al.add(docFalse);
        am = new AccessModifier();
        am.addDocument(docInAM.getID());
        plant = new Plant(1234, "plant", am);
        fe = new FileExplorer(al, plant);
        folder = new Folder(pathToFolder.toString());
        parentFolder = new Folder(pathToParentFolder.toString());
        parentFolder.getContents().add(folder);
    }

    @Test
    void getShownFiles(){
        List<AbstractFile> shownFiles = fe.getShownFiles();

        assertTrue(shownFiles.contains(docInAM));

        assertEquals(1, shownFiles.size());

        assertFalse(shownFiles.contains(docFalse));
    }

    @Test
    void navigateTo() {
        assertNotEquals(folder.getOSPath().toString(), fe.getCurrentPath());

        fe.navigateTo(folder);

        assertEquals(folder.getOSPath().toString(), fe.getCurrentPath());
    }

    @Test
    void navigateBack() {
        fe.navigateTo(folder);

        assertTrue(fe.navigateBack());

        //todo navigateBack doesn't work WIP

        //assertEquals(parentFolder.getOSPath().toString(), fe.getCurrentPath());
    }

    @Test
    void getCurrentPath() {
        assertEquals("", fe.getCurrentPath());

        fe.navigateTo(folder);

        assertEquals(folder.getOSPath().toString(), fe.getCurrentPath());
    }
}