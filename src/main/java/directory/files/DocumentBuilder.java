package directory.files;

import directory.SettingsManager;
import gui.AlertBuilder;
import gui.log.LoggingErrorTools;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Use this class to create document objects representing files in the filesystem.
 * CurrentIDPath is the path to the CurrentFileID.
 */

public class DocumentBuilder {
    private Path currentIDPath = SettingsManager.getServerAppFilesPath().resolve("currentFileID");
    public static DocumentBuilder documentBuilder;

    private DocumentBuilder() {
    }

    public static synchronized DocumentBuilder getInstance(){
        if(documentBuilder == null){
            documentBuilder = new DocumentBuilder();
        }
        return documentBuilder;
    }

    public Document createDocument(Path path) {
        return new Document(path.toString(), readAndUpdateCurrentID());
    }

    public void setCurrentIDPath(Path currentIDPath) {
        DocumentBuilder.getInstance().currentIDPath = currentIDPath;
    }

    /**
     * Reads from the currentFileID file to get ID for the file.
     * This also increments the currentID in the currentFileID file.
     *
     * @return a new ID for the new file.
     */
    public int readAndUpdateCurrentID() {
        int currentID = 0;

        if(!Files.exists(currentIDPath)){
            saveCurrentID(0);
            return 0;
        }

        try (BufferedReader reader = Files.newBufferedReader(currentIDPath)) {
            String str = reader.readLine();
            currentID = Integer.parseInt(str);
        } catch (IOException e) {
            AlertBuilder.IOExceptionPopUp();
            LoggingErrorTools.log(e);
            e.printStackTrace();
        }

        saveCurrentID(currentID);

        return currentID;
    }

    private void saveCurrentID(int currentID){

        if(!Files.exists(currentIDPath)) {
            try {
                currentIDPath.toFile().createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (BufferedWriter writer = Files.newBufferedWriter(currentIDPath)) {
            String ID = "" + (currentID + 1);
            writer.write(ID);
        } catch (IOException e) {
            AlertBuilder.IOExceptionPopUp();
            LoggingErrorTools.log(e);
            e.printStackTrace();
        }
    }
}
