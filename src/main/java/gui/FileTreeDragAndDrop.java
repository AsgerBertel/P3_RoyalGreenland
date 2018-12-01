package gui;

import directory.DirectoryCloner;
import directory.FileManager;
import directory.Settings;
import directory.files.AbstractFile;
import directory.files.Document;
import directory.files.Folder;
import gui.file_administration.FileAdminController;
import gui.log.LogEvent;
import gui.log.LogEventType;
import gui.log.LoggingTools;
import javafx.scene.control.Alert;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.util.Callback;
import jdk.jfr.EventType;
import org.apache.commons.io.FileUtils;

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

        // Root can't be dragged and can't drag nothing
        if (draggedItem == null || draggedItem.getParent() == null) return;
        int selectedCellId = treeCell.getIndex();
        Dragboard db = treeCell.startDragAndDrop(TransferMode.MOVE);
        treeView.getSelectionModel().clearSelection(selectedCellId);
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

    //
    private void drop(DragEvent event, TreeCell<AbstractFile> destinationTreeCell, TreeView<AbstractFile> treeView) {
        // Do nothing if no item was dragged
        if(!event.getDragboard().hasContent(JAVA_FORMAT))
            return;

        // Do nothing if dragged into the folder that it's already in
        if(destinationTreeCell.getTreeItem().equals(draggedItem.getParent()))
            return;

        TreeItem<AbstractFile> newParent = destinationTreeCell.getTreeItem();
        FileManager fileManager = FileManager.getInstance();

        AbstractFile fileToMove = draggedItem.getValue();
        AbstractFile destinationFile = destinationTreeCell.getItem();
        Folder destinationFolder;

        // Determine folder that the file should be moved into
        if (destinationFile instanceof Folder) {
            destinationFolder = (Folder) destinationFile;
        } else {
            // If the file is dropped on a document then set the destination to be the parent folder
            Optional<Folder> destinationParent = FileManager.findParent(fileToMove, fileManager.getMainFilesRoot());
            if (destinationParent.isPresent())
                destinationFolder = destinationParent.get();
            else
                throw new RuntimeException("The document that the file was dropped on does not have a parent"); // todo more specific exception type - Magnus
        }

        // Don't allow folder to be drag into on of its' subfolders
        if (fileToMove instanceof Folder && destinationFolder.isSubFolderOf((Folder) fileToMove))
            return;


        try {
            FileManager.getInstance().moveFile(fileToMove, destinationFolder);
        } catch (IOException e) {
            e.printStackTrace();
        }

        LoggingTools.log(new LogEvent(fileToMove.getName(), destinationFolder.getName(), LogEventType.FILE_MOVED));
        fileManager.save();
        fileAdminController.update();
        treeView.getSelectionModel().select(draggedItem);
        event.setDropCompleted(true);
    }

    private void clearDropLocation() {
        if (dropZone != null) dropZone.setStyle("");
    }
}
