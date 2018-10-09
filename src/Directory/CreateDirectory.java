package Directory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CreateDirectory {

    public void CreateFolder() {

        String fileName = "C:\\p3_folders";

        Path path = Paths.get(fileName);

        if (!Files.exists(path)) {

            try {
                Files.createDirectory(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Directory created");
        } else {

            System.out.println("Directory already exists");
        }
    }
}
