package directory.files;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DocumentBuilder {

    public static Document createDocument(Path path){
        return new Document(path, readAndUpdateCurrentID());
    }

    public static int readAndUpdateCurrentID(){
        int currentID = -1;
        Path currentIDPath = Paths.get("Sample files/currentFileID");

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
