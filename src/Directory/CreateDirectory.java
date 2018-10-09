package Directory;

import java.io.File;
import java.io.IOException;

public class CreateDirectory {

    public void CreateFolder() {

        String path = "C:" + File.separator + "hello" + File.separator + "hi.txt";
// Use relative path for Unix systems
        File f = new File(path);

        f.getParentFile().mkdirs();
        try {
            f.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
