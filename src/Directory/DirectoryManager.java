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
    ObservableList<abstractDocFolder> listOfFiles = FXCollections.observableArrayList();
    Path path = Paths.get("C:/p3_folders/");

    public Folder folder = new Folder("p3_folders", path);


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
    public void renameFile(abstractDocFolder oldFile, String newFileName){
        Path newFilePath = Paths.get(oldFile.getPath().getParent() + newFileName);
        try {
            Files.move(oldFile.getPath(), newFilePath, REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void displayFiles(TableColumn name, TableColumn image, TableView files) {
        files.getItems().clear();
        try {
            folder.readContent();
        } catch (IOException e) {

        }

        listOfFiles = folder.folderContents;

        image.setCellValueFactory(new PropertyValueFactory<abstractDocFolder, ImageView>("image"));

        name.setCellValueFactory(new PropertyValueFactory<abstractDocFolder, String>("name"));
        files.setItems(listOfFiles);
    }

    public void openFolder(Path path) {
        folder.setPath(path);
    }

    public void openPrevFolder(Path path) {
        folder.setPath(path.getParent());
        System.out.println(path.getParent());
    }

}
