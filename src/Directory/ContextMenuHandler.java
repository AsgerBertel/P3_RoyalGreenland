package Directory;

import javafx.scene.control.TableView;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ContextMenuHandler {

    public void CreateFolder(TableView<AbstractDocFolder> files, Path currentPath, Path selectedPath) {
        if (selectedPath == null) {
            Path filename = Paths.get(currentPath + "/" + "New Folder");
            if (!Files.exists(filename)) {

                try {
                    Files.createDirectory(filename);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("Directory created");
            } else {

                System.out.println("Directory already exists");
            }
        }
        else{
            Path filename = Paths.get(selectedPath + "/" + "New Folder");
            if (!Files.exists(filename)) {

                try {
                    Files.createDirectory(filename);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("Dirxectory created");
            } else {

                System.out.println("Directory already exists");
            }
        }
    }
}
