package Directory;

import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ContextMenuHandler {

    public void createFolder(TableView<abstractDocFolder> files, Path currentPath, Path selectedPath) {
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
        } else {
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

    public void renameFile(TableColumn<abstractDocFolder, String> tblcName, TableView files) {
        TextField txtRename = new TextField();
        abstractDocFolder chosenRow = (abstractDocFolder) files.getSelectionModel().getSelectedItem();

        String ChosenRowName = chosenRow.getName();
        txtRename.setText(ChosenRowName);
        Path path = chosenRow.getPath().getParent();

        tblcName.setGraphic(txtRename);

        txtRename.setOnKeyPressed(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    File newFileName = new File(path + "/" + txtRename.getText());
                    File oldFIleName = new File(chosenRow.getPath().toString());
                    oldFIleName.renameTo(newFileName);


                }
            }
        });
    }

}
