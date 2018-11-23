package directory.files;

import com.sun.nio.file.SensitivityWatchEventModifier;
import directory.FileManager;
import gui.log.LogEvent;
import gui.log.LogEventType;
import gui.log.LoggingTools;
import json.AppFilesManager;

import javax.naming.InvalidNameException;
import java.awt.*;
import java.io.File;
import java.io.IOException;

import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Document extends AbstractFile {
    private int ID;
    private String lastModified;

    private transient final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm");

    /**
     * Used DocumentBuilder to create a document so that it gets the correct ID.
     * @param path path to the file.
     * @param ID ID of the file. Given through the DocumentBuilder.
     */
    Document(String path, int ID) {
        super(path);
        this.ID = ID;
        this.lastModified = DATE_TIME_FORMATTER.format(LocalDateTime.now());
    }

    public Document(Document document) {
        super(document.getPath().toString());
        this.ID = document.getID();
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
        Path tempTargetPath = Paths.get(targetPath + File.separator + this.getName());
        Path temp = Files.move(getPath(), tempTargetPath);
        setPath(tempTargetPath);

        if(temp == null) // todo temp always null? Implement differently
            throw new IOException("Failed to move file");
    }

    // Opens the document in a window
    public void openDocument() throws IOException {
        File file = new File(getPath().toString()); // todo is this correctly implemented??
        Desktop.getDesktop().open(file); // Todo Implementation seems alright on mac, but it uses IO instead of NIO?

        Path dirPath = getParentPath();
    }

    @Override
    public void renameFile(String newFileName) throws InvalidNameException {
        File currentFile = getPath().toFile();
        File renamedFile = new File(getPath().getParent() + File.separator + newFileName);
        setPath(Paths.get(renamedFile.getPath()));

        // Rename file and throw exception if it failed
        if(!currentFile.renameTo(renamedFile))
            throw new InvalidNameException();

        LoggingTools lt = new LoggingTools();
        LoggingTools.log(new LogEvent(getName(), LogEventType.RENAMED));
        AppFilesManager.save(FileManager.getInstance());
    }

    public void setLastModified(LocalDateTime localDateTime){
        this.lastModified = DATE_TIME_FORMATTER.format(localDateTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Document document = (Document) o;
        return ID == document.ID &&
                Objects.equals(lastModified, document.lastModified);
    }

    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), ID, lastModified);
    }
}