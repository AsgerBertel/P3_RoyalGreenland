package Directory;

import java.nio.file.Path;

public abstract class AbstractDocFolder
{
    private Path path;

    public AbstractDocFolder(Path path) {
        this.path = path;
    }
    public Path getPath() {
        return path;
    }
}
