package directory.files;

import javax.naming.InvalidNameException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public abstract class AbstractFile {

    private String path;

    AbstractFile(String path) {
        this.path = path;
    }

    abstract void renameFile(String newFileName) throws InvalidNameException;

    public Path getPath() {
        return Paths.get(path);
    }

    public Path getAbsolutePath(){
        return Paths.get(path).toAbsolutePath();
    }

    public Path getParentPath() {
        return Paths.get(path).getParent();
    }

    public String getName() {
        return Paths.get(path).getFileName().toString();
    }

    public void setPath(Path path) {
        this.path = path.toString();
    }
    @Override
    public String toString() {
        return getName();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractFile that = (AbstractFile) o;
        return Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }
}
