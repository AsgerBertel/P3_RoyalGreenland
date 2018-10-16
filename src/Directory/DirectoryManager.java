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

import javafx.scene.control.MenuItem;
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
    ObservableList<AbstractDocFolder> listOfFiles = FXCollections.observableArrayList();
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



        listOfFiles.add(new Folder("test",new ImageView(folderImg),"folder"));
        listOfFiles.add(new Folder("test",new ImageView(folderImg),"folder"));
        listOfFiles.add(new Document("test",new ImageView(folderImg),"folder"));
        listOfFiles.add(new Document("test",new ImageView(documentImg),"document"));
        listOfFiles.add(new Document("test",new ImageView(documentImg),"document"));
        listOfFiles.add(new Document("test",new ImageView(documentImg),"document"));


        image.setCellValueFactory(new PropertyValueFactory<AbstractDocFolder, ImageView>("image"));

        name.setCellValueFactory(new PropertyValueFactory<AbstractDocFolder, String>("name"));
        files.setItems(listOfFiles);

    }

    public ArrayList ContextMenuItems(TableView<AbstractDocFolder> files){
        AbstractDocFolder chosenRow = files.getSelectionModel().getSelectedItem();
        Image folderImg = new Image("Images/folder.png");
        Image documentImg = new Image("Images/document.png");
        ArrayList<MenuItem> menuItems = new ArrayList<>();


        ImageView folderImgv = new ImageView();
        folderImgv.setImage(folderImg);
        //Must revise for proper if statement
        if(chosenRow.getFileType() == "folder"){
            menuItems.add(new MenuItem("Open"));
            menuItems.add(new MenuItem("Rename"));
            menuItems.add(new MenuItem("Upload File"));
            menuItems.add(new MenuItem("Delete Folder"));
        }else{
            menuItems.add(new MenuItem("Open"));
            menuItems.add(new MenuItem("Rename"));
            menuItems.add(new MenuItem("Delete File"));
        }
        return menuItems;
    }


}
