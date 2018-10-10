package Directory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DirectoryManager {
    public void createDirectory(String Path, String Name) {

        String fileName = Path + Name;

        java.nio.file.Path path = Paths.get(fileName);

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

    public void deleteDirectory(Folder folder){
        if(Files.exists(folder.getPath())) {
            try {
                Files.delete(folder.getPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Directory deleted");
        }
    }


    
}
