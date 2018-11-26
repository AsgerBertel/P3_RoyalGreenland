package directory.files;

import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.util.Callback;

import javax.naming.InvalidNameException;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public abstract class AbstractFile implements Callback<TreeView<AbstractFile>, TreeCell<AbstractFile>> , Serializable {

    private String path;

    AbstractFile(String path) {
        this.path = trimPath(path);
    }
    AbstractFile (AbstractFile file) {
        this.path = file.getPath().toString();
    }

    public Path getPath() {
        return Paths.get(path);
    }

    /**
     * Returns the OS directive path, without artificial root folder.
     * @return
     */
    private String trimPath(String path) {
        if(!path.contains("root"))
            return "root"+File.separator+"".concat(path);
        else
            return path;
    }

    public Path getOSPath() {
        return Paths.get(path.replace("root" + File.separator,""));
    }

    public Path getParentPath() {
        return Paths.get(path).getParent();
    }

    public String getName() {
        return Paths.get(path).getFileName().toString();
    }

    public void setName(String name){
        String parent = Paths.get(path).getParent().toString();
        this.path = parent + "/" + name;
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

    @Override
    public TreeCell<AbstractFile> call(TreeView<AbstractFile> param) {
        return null;
    }
}
