package Directory;

import javafx.scene.image.ImageView;

import java.awt.*;
import java.nio.file.Path;

public abstract class AbstractDocFolder {
    protected Path path;
    protected String name;
    protected String fileType;
    protected ImageView image;


    public AbstractDocFolder(ImageView image,String name,String filetype ) {
        this.name = name;
        this.fileType = filetype;
        this.image = image;
    }

    public Path getPath() {
        return path;
    }

    public Path setPath(Path newPath) {
        this.path = newPath;
        return path;
    }

    public String getFileType() {
        return fileType;
    }

    public ImageView getImage() {
        return image;
    }


    public String getName() {
        return name;
    }
}
