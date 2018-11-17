package directory.files;

import directory.PreferencesManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Use this class to create document objects representing files in the filesystem.
 * CurrentIDPath is the path to the CurrentFileID.
 */

public class DocumentBuilder {
    // Todo on setup set currentIDPath
    private Path currentIDPath = Paths.get("Sample files/currentFileID");
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
        int currentID = -1;

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(PreferencesManager.getInstance().getServerAppFilesPath() + "currentFileID"))) {
            String str = reader.readLine();
            currentID = Integer.parseInt(str);
        } catch (IOException e) {
            System.out.println("Could not read file" + e.getMessage());
        }

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(PreferencesManager.getInstance().getServerAppFilesPath() + "currentFileID"))) {
            String ID = "" + (currentID + 1);
            writer.write(ID);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return currentID;
    }
}
