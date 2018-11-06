package directory.files;

import javax.naming.InvalidNameException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class AbstractFile {

    protected String path;

    AbstractFile(String path) {
        this.path = path;
    }

    abstract void renameFile(String newFileName) throws InvalidNameException;

    public Path getPath() {
        return Paths.get(path);
    }

    public Path getParentPath() {
        return Paths.get(path).getParent();
    }

    public String getName() {
        return Paths.get(path).getFileName().toString();
    }

    public void setPath(Path path) {
        this.path = path.toAbsolutePath().toString();
    }

    public String getPathAsString(){
        return path;
    }
}
