package Directory;


import javafx.scene.image.Image;

import javax.naming.InvalidNameException;
import java.awt.*;
import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;

public class Document extends AbstractFile
{

    private static Image standardDocIcon = new Image("icons/document.png");

    public Document(Path path) {
        super(path);
        this.image = standardDocIcon; // todo add more icons for different kinds of documents
    }

    @Override
    public void renameFile(String newFileName) throws InvalidNameException {
        File currentFile = path.toFile();
        File renamedFile = new File(path.getParent().toAbsolutePath() + newFileName);

        // Rename file and throw exception if it failed
        if(!currentFile.renameTo(renamedFile))
            throw new InvalidNameException();
    }


    public void moveFile(Path targetPath) throws IOException{
        Path temp = Files.move(path, targetPath);

        if(temp == null){
            throw new IOException("Failed to move file");
        }
    }

    @Override
    public void deleteFile(Path path) throws IOException {
        // todo - add delete functionality. This method should probably just delete the file entirely?
        // todo - Then moving a copy of the file to the trash would be handled from directoryManager/fileManager
    }

    // Opens the document in windows
    public void openDocument() throws IOException {
        File file = new File(path.toAbsolutePath().toString()); // todo is this correctly implemented??
        Desktop.getDesktop().open(file);
    }

}
