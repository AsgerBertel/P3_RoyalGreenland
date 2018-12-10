package directory;

import app.ApplicationMode;
import directory.files.AbstractFile;
import directory.files.Document;
import directory.files.Folder;
import json.AppFilesManager;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class DirectoryClonerTest extends FileTester {

    ArrayList<AbstractFile> al;
    Path docPath = Paths.get("04_MASKINTØRRET FISK/FL 04 GR_02   Flowdiagram for produktion af maskintørret fisk.pdf");
    Document doc;
    Path folderPath = Paths.get("02_VINTERTØRRET FISK");
    Folder folder;

    @Override
    void setSettings(){
        SettingsManager.loadSettings(ApplicationMode.ADMIN);
        al = FileManager.getInstance().getMainFiles();
        doc = (Document) findInMainFiles(docPath);
        folder = (Folder) findInMainFiles(folderPath);
    }

    @Test
    void publishFiles() throws Exception {
        //asserting that both are equal when publishFiles() is used
        DirectoryCloner.publishFiles();
        assertEquals(AppFilesManager.loadPublishedFileList(), FileManager.getInstance().getMainFiles());

        //asserting not equals when FileManager does something without publishing
        FileManager.getInstance().deleteFile(doc);
        assertNotEquals(AppFilesManager.loadPublishedFileList(), FileManager.getInstance().getMainFiles());

        //asserting equal again when files are published
        DirectoryCloner.publishFiles();
        assertEquals(AppFilesManager.loadPublishedFileList(), FileManager.getInstance().getMainFiles());
    }

    @Test
    void updateLocalFiles() {
    }

    @Test
    void removeOutdatedFiles() {
    }

    @Test
    void addNewFiles() {
    }

    @Test
    void findMissingFiles() {
    }

    @Test
    void copyFolder() {
    }

    @Test
    void mergeFolders() {
    }

    @Test
    void deleteFolder() {
    }
}