package directory.files;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

class FolderTest {
    private File resourcesDirectory = new File("src/tests/resTest" + File.separator);
    private Path pathToJsonTest = Paths.get(resourcesDirectory.getAbsolutePath() + File.separator + "Main Files Test/RLFiles/Server/App Files/allFiles.JSON");


    @Test
    public void setPath(){

    }

    @Test
    public void setName(){

    }

    @Test
    public void updateChildrenPaths(){

    }

    @Test
    public void getContents(){

    }

    @Test
    public void containsFromAccessModifier(){

    }

    @Test
    public void isSubFolderOf(){

    }
}