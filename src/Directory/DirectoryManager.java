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

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;


public class DirectoryManager {

    public void CreateFolder(String Path, String Name) {

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

    public void DisplayFiles(TableColumn name, TableColumn image, TableView files) {
        Image folderImg = new Image("Images/folder.png");
        Image documentImg = new Image("Images/document.png");

        //ImageView imgv = new ImageView();
        //.setImage(folderImg);

        ObservableList<AbstractDocFolder> listOfFiles = FXCollections.observableArrayList();

        listOfFiles.add(new Folder("test",new ImageView(folderImg)));
        listOfFiles.add(new Folder("test",new ImageView(folderImg)));
        listOfFiles.add(new Document("test",new ImageView(folderImg)));
        listOfFiles.add(new Document("test",new ImageView(documentImg)));
        listOfFiles.add(new Document("test",new ImageView(documentImg)));
        listOfFiles.add(new Document("test",new ImageView(documentImg)));


        image.setCellValueFactory(new PropertyValueFactory<AbstractDocFolder, ImageView>("image"));

        name.setCellValueFactory(new PropertyValueFactory<AbstractDocFolder, String>("name"));
        files.setItems(listOfFiles);

    }


}
