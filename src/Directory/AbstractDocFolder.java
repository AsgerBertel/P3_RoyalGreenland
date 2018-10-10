package Directory;

import java.awt.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class AbstractDocFolder
{
    protected Path path;
    protected String name;


    public AbstractDocFolder(String name, Path path) {
        this.name = name;
        this.path = path;
    }
}
