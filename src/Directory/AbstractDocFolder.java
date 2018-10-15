package Directory;

import javafx.scene.image.ImageView;

import java.awt.*;
import java.nio.file.Path;

public abstract class AbstractDocFolder {
    protected Path path;
    protected String name;
    protected ImageView image;


    public AbstractDocFolder(String name, ImageView image) {
        this.name = name;

        this.image = image;
    }

    public Path getPath() {
        return path;
    }

    public Path setPath(Path newPath) {
        this.path = newPath;
        return path;
    }

    public ImageView getImage() {
        return image;
    }


    public String getName() {
        return name;
    }
}
