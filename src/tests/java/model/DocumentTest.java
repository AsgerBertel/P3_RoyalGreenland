package model;

import app.ApplicationMode;
import model.managing.SettingsManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

class DocumentTest extends FileTester {
    //private File resourcesDirectory = new File(TestUtil.getTestServerDocuments().toString());
    private final Path pathToTestFileExt = Paths.get("03_URENSET STENBIDERROGN/GMP 03 GR_02.pdf");
    private Document docExt;
    private final Path pathToWrongFile = Paths.get("03_URENSET STENBIDERROGN/GMP 03 GR_02.doc");
    private Document docWrong;
    private transient final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm");


    @BeforeEach
    void setSettings(){
        SettingsManager.loadSettings(ApplicationMode.ADMIN);
        docExt = DocumentBuilder.getInstance().createDocument(pathToTestFileExt);
        docWrong = DocumentBuilder.getInstance().createDocument(pathToWrongFile);
    }

    @Test
    void getID() {
        assertEquals(docExt.getID(), docExt.getID());
    }

    @Test
    void getFileExtension() {

        //Gets right extension
        assertEquals("pdf" ,docExt.getFileExtension());
        //No extension, sends nothing back
        assertEquals("doc", docWrong.getFileExtension());
    }

    @Test
    void setLastModified() {
        LocalDateTime ldt = LocalDateTime.now();

        docExt.setLastModified(ldt);

        assertEquals(ldt.format(DATE_TIME_FORMATTER), docExt.getLastModified());
    }

}