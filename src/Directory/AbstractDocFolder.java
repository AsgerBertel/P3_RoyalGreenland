package Directory;

import java.awt.*;
import java.nio.file.Path;

public abstract class AbstractDocFolder
{
    protected Path path;
    protected String name;
    protected String imageName;


    public AbstractDocFolder(String name, Path path) {
        this.name = name;
        this.path = path;
    }
    public Path getPath(){
        return path;
    }
    public Path setPath(Path newPath){
        this.path = newPath;
        return path;
    }
}
