package Directory;

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
}
