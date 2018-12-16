package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Document extends AbstractFile {
    private final int ID;
    private String lastModified;

    private transient final static DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm");

    /**
     * Used DocumentBuilder to create a document so that it gets the correct ID.
     * @param path path to the file.
     * @param ID ID of the file. Given through the DocumentBuilder.
     */
    protected Document(String path, int ID) {
        super(path);
        this.ID = ID;
        this.lastModified = DATE_TIME_FORMATTER.format(LocalDateTime.now());
    }

    public Document(Document document) {
        super(document);
        this.ID = document.getID();
    }

    public int getID() {
        return ID;
    }

    // Returns the io extension without the punctuation
    public String getFileExtension(){
        String fileName = getName();
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".")+1);
        return "";
    }

    public String getLastModified() {
        return lastModified;
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