package Directory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.control.cell.PropertyValueFactory;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;


public class DirectoryManager {

/*  todo reimplement a version that creates a folder in the filesystem and returns the corresponding folder object
    todo Should throw exceptions in case of error (maybe both an InvalidNameException and a general IOException)
    public void createFolder(String Path, String Name) {
        String fileName = Path + Name;
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
*/

}
