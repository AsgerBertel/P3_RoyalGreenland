package Directory;

import Main.Controller;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

        ImageView imgv = new ImageView();
        imgv.setImage(folderImg);

        ObservableList<AbstractDocFolder> ListOfFiles = FXCollections.observableArrayList(
                new Folder("Jacob", imgv),
                new Folder("Jacob", imgv),
                new Folder("Jacob", imgv),
                new Folder("Jacob", imgv),
                new Folder("Jacob", imgv),
                new Folder("Jacob", imgv));

        /*
        name.setCellValueFactory(new PropertyValueFactory<AbstractDocFolder,String>("name"));
        image.setCellValueFactory(new PropertyValueFactory<AbstractDocFolder,ImageView>("image"));


        files.setItems(ListOfFiles);
        files.getColumns().addAll(name,image);

        */
















      /*  Controller controller = new Controller();
        ListView<AbstractDocFolder> ListOfFiles = controller.listviewTest;
        Path path = Paths.get("C:\\p3_folders");
        ObservableList<AbstractDocFolder> files = controller.processingList;*/

            /*    files.add(new Folder("Test",path));
        files.add(new Folder("shit",path));
        files.add(new Folder("waffel",path));
        files.add(new Folder("jelly",path));
        files.add(new Folder("ludo",path));
        files.add(new Document("jelly",path));
        files.add(new Document("ludo",path));*/

        //ListOfFiles.getItems();
        /*   ListView<AbstractDocFolder> ListOfFiles;
        ListView lv = (ListView) findViewById(id.listview);
        ListOfFiles = new ListView<>(files);
        ListOfFiles.setItems(files);
      //  ListOfFiles.getItems();
        ListView listView1 = (ListView) findViewById(R.id.listView1);
        ListOfFiles.setCellFactory(ComboBoxListCell.forListView(files));
      //  controller.paneTest.getChildren().add(ListOfFiles);
    if(controller.paneTest == null){

        System.out.println("shit");
    }
    else {

    }*/


        //  Folder folder = new Folder("hej", "C:\p3_folders");
        // ObservableList<AbstractDocFolder> files;


    }


}
