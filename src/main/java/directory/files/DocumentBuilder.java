package directory.files;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Use this class to create document objects representing files in the filesystem.
 * CurrentIDPath is the path to the CurrentFileID.
 */

public class DocumentBuilder {
    private static Path currentIDPath = Paths.get("Sample files/currentFileID");

    public static Document createDocument(Path path){
        return new Document(path, readAndUpdateCurrentID());
    }

    /**
     * Reads from the currentFileID file to get ID for the file.
     * This also increments the currentID in the furrentFileID file.
     * @return a new ID for the new file.
     */
    public static int readAndUpdateCurrentID(){
        int currentID = -1;

        try(BufferedReader reader = Files.newBufferedReader(currentIDPath))
        {
            String str = reader.readLine();
            currentID = Integer.parseInt(str);
        } catch (IOException e){
            System.out.println("Could not read file" + e.getMessage());
        }

        try(BufferedWriter writer = Files.newBufferedWriter(currentIDPath)) {
            String ID = "" + (currentID + 1);
            writer.write(ID);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return currentID;
    }
}
