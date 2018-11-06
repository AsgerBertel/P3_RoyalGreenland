package directory.files;

import javax.naming.InvalidNameException;
import java.awt.*;
import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Document extends AbstractFile {
    private int ID;

    /**
     * Used DocumentBuilder to create a document so that it gets the correct ID.
     * @param path path to the file.
     * @param ID ID of the file. Given through the DocumentBuilder.
     */
    Document(String path, int ID) {
        super(path);
        this.ID = ID;
    }

    public int getID() {
        return ID;
    }

    // Returns the files extension without the punctuation
    public String getFileExtension(){
        String fileName = getName();
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".")+1);
        return "";
    }

    public void moveFile(Path targetPath) throws IOException{
        // To make sure, that the name is also included in the path.
        Path tempTargetPath = Paths.get(targetPath.toAbsolutePath() + File.separator + this.getName());
        Path temp = Files.move(getPath(), tempTargetPath);
        setPath(tempTargetPath);

        if(temp == null){ // todo temp always null? Implement differently
            throw new IOException("Failed to move file");
        }
    }

    // Opens the document in a window
    public void openDocument() throws IOException {
        File file = new File(getPath().toString()); // todo is this correctly implemented??
        Desktop.getDesktop().open(file); // Todo Implementation seems alright on mac, but it uses IO instead of NIO?
    }

    @Override
    public void renameFile(String newFileName) throws InvalidNameException {
        File currentFile = getPath().toFile();
        File renamedFile = new File(getPath().getParent().toAbsolutePath() + File.separator + newFileName);
        setPath(Paths.get(renamedFile.getPath()));


        // Rename file and throw exception if it failed
        if(!currentFile.renameTo(renamedFile))
            throw new InvalidNameException();
    }
}
