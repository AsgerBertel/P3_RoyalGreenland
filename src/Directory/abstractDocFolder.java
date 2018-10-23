package Directory;

import javafx.scene.image.ImageView;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class abstractDocFolder {
    protected Path path;
    protected String name;
    protected String fileType;
    protected ImageView image;


    public abstractDocFolder(ImageView image, String name, String filetype) {
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

    public void deleteFile(Path path) throws IOException {
        if (Files.isRegularFile(path)) {
            Files.deleteIfExists(path);
        } else {
            if (Files.size(path) == 0) {
                Files.deleteIfExists(path);
            }
            //Delete all files in a directory via Files.Walk
        }
    }
}
