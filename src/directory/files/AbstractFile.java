package directory.files;

import javax.naming.InvalidNameException;
import java.io.IOException;
import java.nio.file.Path;

public abstract class AbstractFile {
    protected Path path;

    public AbstractFile(Path path) {
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

    public String getName() {
        return path.getFileName().toString();
    }

}