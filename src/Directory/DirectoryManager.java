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

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;


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

    public ArrayList ContextMenuItems(TableView<AbstractDocFolder> files) {


        AbstractDocFolder chosenRow = files.getSelectionModel().getSelectedItem();
        String fileType = chosenRow.getFileType();
        ArrayList<MenuItem> menuItems = new ArrayList<>();

        if (fileType == "folder") {
            menuItems.add(new MenuItem("Open"));
            menuItems.add(new MenuItem("Rename"));
            menuItems.add(new MenuItem("Upload File"));
            menuItems.add(new MenuItem("Delete Folder"));
        } else {
            menuItems.add(new MenuItem("Open"));
            menuItems.add(new MenuItem("Rename"));
            menuItems.add(new MenuItem("Delete File"));
        }
        return menuItems;
    }

    public void openFolder(Path path) {
        folder.setPath(path);
    }
    public void openPrevFolder(Path path){
        folder.setPath(path.getParent());
        System.out.println(path.getParent());
    }

}
