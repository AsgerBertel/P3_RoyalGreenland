package gui;

import directory.files.AbstractFile;
import directory.files.Folder;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.util.Callback;

import java.util.Objects;

public class FileTreeDragAndDrop implements Callback<TreeView<AbstractFile>, TreeCell<AbstractFile>> {
    private TreeCell<AbstractFile> dropZone;
    private TreeItem<AbstractFile> draggedItem;

    private static final Image folderImage = new Image("/icons/smallFolder.png");
    private static final Image documentImage = new Image("/icons/smallBlueDoc.png");

    private static final DataFormat JAVA_FORMAT = new DataFormat("application/x-java-serialized-object");
    private static final String DROP_HINT_STYLE = "-fx-background-color: #6fd59b; ";
    Image folderImg = new Image("icons/smallFolder.png");
    Image docImg = new Image("icons/smallBlueDoc.png");

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

        TreeItem<AbstractFile> thisItem = treeCell.getTreeItem();
        TreeItem<AbstractFile> droppedItemParent = draggedItem.getParent();

        // remove from previous location
        droppedItemParent.getChildren().remove(draggedItem);

        // dropping on parent node makes it the first child
        if (Objects.equals(droppedItemParent, thisItem)) {
            thisItem.getChildren().add(0, draggedItem);
            treeView.getSelectionModel().select(draggedItem);
        } else {
            // add to new location
            int indexInParent = thisItem.getParent().getChildren().indexOf(thisItem);
            thisItem.getParent().getChildren().add(indexInParent + 1, draggedItem);
        }
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
}
