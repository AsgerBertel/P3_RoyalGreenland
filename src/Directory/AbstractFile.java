package Directory;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.naming.InvalidNameException;
import java.io.IOException;
import java.nio.file.Path;

public abstract class AbstractFile {
    protected Path path;
    protected Image image;

    public AbstractFile(Path path) {
        // todo set default image
        this.path = path;
    }

    public abstract void renameFile(String newFileName) throws InvalidNameException;
    public abstract void deleteFile(Path path) throws IOException;

    public Path getPath() {
        return path;
    }

    public Path getParentPath() {
        return path.getParent();
    }

    public Image getImage() {
        return image;
    }

    public String getName() {
        return path.getFileName().toString();
    }

}
