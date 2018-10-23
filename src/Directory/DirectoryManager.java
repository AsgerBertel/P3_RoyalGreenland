package Directory;

import Main.Controller;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.spi.AbstractResourceBundleProvider;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;


public class DirectoryManager {
    ObservableList<AbstractDocFolder> listOfFiles = FXCollections.observableArrayList();
    Path path = Paths.get("C:/p3_folders/");

    public Folder folder = new Folder("p3_folders", path);


    public void CreateFolder(String Path, String Name) {
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
    public void renameFile(AbstractDocFolder oldFile, String newFileName){
        Path newFilePath = Paths.get(oldFile.getPath().getParent() + newFileName);
        try {
            Files.move(oldFile.getPath(), newFilePath, REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void DisplayFiles(TableColumn name, TableColumn image, TableView files) {
        files.getItems().clear();
        try {
            folder.readContent();
        } catch (IOException e) {

        }

        listOfFiles = folder.folderContents;

        image.setCellValueFactory(new PropertyValueFactory<AbstractDocFolder, ImageView>("image"));

        name.setCellValueFactory(new PropertyValueFactory<AbstractDocFolder, String>("name"));
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
