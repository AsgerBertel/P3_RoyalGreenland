package gui;

import directory.FileManager;
import directory.Settings;
import directory.files.AbstractFile;
import directory.files.Folder;
import gui.file_administration.FileAdminController;
import javafx.scene.control.Alert;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class FileTreeDragAndDrop implements Callback<TreeView<AbstractFile>, TreeCell<AbstractFile>> {
    private TreeCell<AbstractFile> dropZone;
    private TreeItem<AbstractFile> draggedItem;

    private static final Image folderImage = new Image("/icons/smallFolder.png");
    private static final Image documentImage = new Image("/icons/smallBlueDoc.png");

    private static final DataFormat JAVA_FORMAT = new DataFormat("application/x-java-serialized-object");
    private static final String DROP_HINT_STYLE = "-fx-background-color: #6fd59b; ";
    Image folderImg = new Image("icons/smallFolder.png");
    Image docImg = new Image("icons/smallBlueDoc.png");
    private FileAdminController fileAdminController;

    public FileTreeDragAndDrop(FileAdminController fileAdminController) {
        this.fileAdminController = fileAdminController;
    }

    @Override
    public TreeCell<AbstractFile> call(TreeView<AbstractFile> treeView) {
        TextFieldTreeCell<AbstractFile> cell = new TextFieldTreeCell<>() {
            @Override
            public void updateItem(AbstractFile item, boolean empty) {
                super.updateItem(item, empty);
                ImageView iv1 = new ImageView();
                if (item == null) return;
                if (item instanceof Folder) {
                    iv1.setImage(folderImg);
                } else {
                    iv1.setImage(docImg);
                }
                setGraphic(iv1);
                setText(item.getName());
            }
        };
        cell.setOnDragDetected((MouseEvent event) -> dragDetected(event, cell, treeView));
        cell.setOnDragOver((DragEvent event) -> dragOver(event, cell, treeView));
        cell.setOnDragDropped((DragEvent event) -> drop(event, cell, treeView));
        cell.setOnDragDone((DragEvent event) -> clearDropLocation());
        return cell;
    }

    private void dragDetected(MouseEvent event, TreeCell<AbstractFile> treeCell, TreeView<AbstractFile> treeView) {
        draggedItem = treeCell.getTreeItem();

        // root can't be dragged
        if (draggedItem.getParent() == null) return;
        Dragboard db = treeCell.startDragAndDrop(TransferMode.MOVE);

        ClipboardContent content = new ClipboardContent();
        content.put(JAVA_FORMAT, draggedItem.getValue());
        db.setContent(content);
        db.setDragView(treeCell.snapshot(null, null));
        event.consume();
    }

    private void dragOver(DragEvent event, TreeCell<AbstractFile> treeCell, TreeView<AbstractFile> treeView) {
        if (!event.getDragboard().hasContent(JAVA_FORMAT)) return;
        TreeItem<AbstractFile> thisItem = treeCell.getTreeItem();

        // can't drop on itself
        if (draggedItem == null || thisItem == null || thisItem == draggedItem) return;
        // ignore if this is the root
        if (draggedItem.getParent() == null) {
            clearDropLocation();
            return;
        }
        event.acceptTransferModes(TransferMode.MOVE);
        if (!Objects.equals(dropZone, treeCell)) {
            clearDropLocation();
            this.dropZone = treeCell;
            dropZone.setStyle(DROP_HINT_STYLE);
        }
    }

    private void drop(DragEvent event, TreeCell<AbstractFile> treeCell, TreeView<AbstractFile> treeView) {
        Dragboard db = event.getDragboard();
        boolean success = false;
        if (!db.hasContent(JAVA_FORMAT)) return;

        TreeItem<AbstractFile> newParent = treeCell.getTreeItem();
        TreeItem<AbstractFile> itemToBeMoved = draggedItem;
        FileManager fileManager = FileManager.getInstance();


        Optional<Folder> toBeMovedParent = FileManager.findParent(itemToBeMoved.getValue(), fileManager.getMainFilesRoot());

        if (toBeMovedParent.isPresent()) {
            Folder folder = toBeMovedParent.get();
            folder.getContents().remove(itemToBeMoved.getValue());
        } else {
            fileManager.getMainFiles().remove(itemToBeMoved.getValue());
        }

        if (itemToBeMoved.getValue() instanceof Folder) {

            subFileExists(newParent.getValue().getAbsolutePath(), itemToBeMoved.getValue().getName());

            Folder parentFolderToBeMoved;
            parentFolderToBeMoved = (Folder) itemToBeMoved.getValue();
            parentFolderToBeMoved.changeChildrenPath(parentFolderToBeMoved, parentFolderToBeMoved.getPath().toString(), parentFolderToBeMoved.getPath() + "/" + newParent.getValue().getName());
            Folder newParentFolder = (Folder) newParent.getValue();
            newParentFolder.getContents().add(itemToBeMoved.getValue());
        }

        try {

            Files.move(Paths.get(Settings.getServerDocumentsPath() + itemToBeMoved.getValue().getPath().toString()), Paths.get(Settings.getServerDocumentsPath() + newParent.getValue().getPath().toString() + "/" + itemToBeMoved.getValue().getName()));
            itemToBeMoved.getValue().setPath(Paths.get(newParent.getValue().getPath() + "/" + itemToBeMoved.getValue().getName()));


        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information Dialog");
            alert.setHeaderText("Look, an Information Dialog");
            alert.setContentText("I have a great message for you!");
            alert.showAndWait();
        }

        fileManager.save();
        fileAdminController.update();
        treeView.getSelectionModel().select(draggedItem);
        event.setDropCompleted(success);


    }

    private void clearDropLocation() {
        if (dropZone != null) dropZone.setStyle("");
    }

    // Returns an image view with an appropriate icon according to the file-type
    private static ImageView getImageView(AbstractFile file) {
        ImageView imageView;
        if (file instanceof Folder) {
            imageView = new ImageView(folderImage);

            // Set scaling of image
            imageView.setFitWidth(16);
            imageView.setFitHeight(16);
        } else {
            // todo add icons for multiple filetypes
            imageView = new ImageView(documentImage);

            //Set scaling of image
            imageView.setFitWidth(14);
            imageView.setFitHeight(16);
        }
        return imageView;
    }

    private boolean subFileExists(Path folderPath, String nameOfFile) {

        // create a file that is really a directory
        File subFolder = new File(folderPath.toString());

        // get a listing of all files in the directory
        File[] filesInDir = subFolder.listFiles();

        // sort the list of files (optional)
        // Arrays.sort(filesInDir);

        // have everything i need, just print it now
        if (filesInDir == null)
            return false;
        System.out.println(filesInDir.length);
        for (int i = 0; i < filesInDir.length; i++) {
            System.out.println("file: " + filesInDir[i]);
        }
        return true;
    }
}
