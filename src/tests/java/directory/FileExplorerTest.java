package directory;

import app.ApplicationMode;
import directory.files.AbstractFile;
import directory.files.Document;
import directory.files.DocumentBuilder;
import directory.plant.AccessModifier;
import directory.plant.Plant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileExplorerTest extends DocumentsTest{

    FileExplorer fe;
    Document docInAM;
    Document docFalse;
    Path pathToDoc1 = Paths.get("04_MASKINTØRRET FISK/FL 04 GR_02   Flowdiagram for produktion af maskintørret fisk.pdf");
    Path pathToDoc2 = Paths.get("04_MASKINTØRRET FISK/PB 04 GR_02   Procesbeskrivelse for produktion af maskintørret fisk.pdf");
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
    }

    @Test
    void getShownFiles(){

        //todo documents always get ID = -1 in tests.

        List<AbstractFile> shownFiles = fe.getShownFiles();

        assertTrue(shownFiles.contains(docInAM));

        System.out.println(shownFiles.get(0).toString()); // + " and " + shownFiles.get(1).toString()); Du har kun tilføjet 1 fil til accessmodifieren (så get(1) findes ikke)

        System.out.println(docInAM.getID());
        System.out.println(docFalse.getID());

        assertEquals(1, shownFiles.size());

    }

    @Test
    void navigateTo() {

    }

    @Test
    void navigateBack() {
        
    }

    @Test
    void getCurrentPath() {

    }
}