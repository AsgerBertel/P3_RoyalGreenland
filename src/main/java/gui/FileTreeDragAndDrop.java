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

        // root can't be dragged
        if (draggedItem.getParent() == null) return;
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

    private void drop(DragEvent event, TreeCell<AbstractFile> treeCell, TreeView<AbstractFile> treeView) {
        Dragboard db = event.getDragboard();
        boolean success = false;
        TreeItem<AbstractFile> newParent = treeCell.getTreeItem();
        TreeItem<AbstractFile> itemToBeMoved = draggedItem;
        FileManager fileManager = FileManager.getInstance();


        Optional<Folder> toBeMovedParent = FileManager.findParent(itemToBeMoved.getValue(), fileManager.getMainFilesRoot());
        if (!db.hasContent(JAVA_FORMAT)) return;
        if (toBeMovedParent.isPresent()) {
            Folder NewFolderParent = toBeMovedParent.get();
            NewFolderParent.getContents().remove(itemToBeMoved.getValue());
        } else {
            fileManager.getMainFiles().remove(itemToBeMoved.getValue());
        }
        if (itemToBeMoved.getValue() instanceof Folder) {
            if (isSubFolder((Folder) newParent.getValue(), itemToBeMoved.getValue()) && isNameAvaliable((Folder) newParent.getValue(), itemToBeMoved.getValue())) {
                Folder parentFolderToBeMoved;
                parentFolderToBeMoved = (Folder) itemToBeMoved.getValue();
                parentFolderToBeMoved.changeChildrenPath(parentFolderToBeMoved, parentFolderToBeMoved.getPath().toString(), parentFolderToBeMoved.getPath() + "/" + newParent.getValue().getName());
                Folder newParentFolder = (Folder) newParent.getValue();
                newParentFolder.getContents().add(itemToBeMoved.getValue());
                Path oldPath = Paths.get(Settings.getServerDocumentsPath() + itemToBeMoved.getValue().getOSPath().toString());
                Path newPath = Paths.get(Settings.getServerDocumentsPath() + newParent.getValue().getOSPath().toString());
                try {
                    FileUtils.moveToDirectory(
                            FileUtils.getFile(oldPath.toFile()),
                            FileUtils.getFile(newPath.toFile()), false);
                    //     Files.move(Paths.get(, Paths.get());
                    itemToBeMoved.getValue().setPath(Paths.get(newParent.getValue().getPath() + "/" + itemToBeMoved.getValue().getName()));
                } catch (IOException e) {

                    e.printStackTrace();
                }
                DirectoryCloner.printTree(fileManager.getInstance().getMainFilesRoot().getContents(),2);
                LoggingTools.log(new LogEvent(parentFolderToBeMoved.getName(), newParent.getValue().getName(), LogEventType.FILE_MOVED));
            }
        } else {
            if (isNameAvaliable((Folder) newParent.getValue(), itemToBeMoved.getValue())) {
                Folder newParentFolder = (Folder) newParent.getValue();
                newParentFolder.getContents().add(itemToBeMoved.getValue());
                Path oldPath = Paths.get(Settings.getServerDocumentsPath() + itemToBeMoved.getValue().getOSPath().toString());
                Path newPath = Paths.get(Settings.getServerDocumentsPath() + newParent.getValue().getOSPath().toString());
                try {
                    FileUtils.moveToDirectory(
                            FileUtils.getFile(oldPath.toFile()),
                            FileUtils.getFile(newPath.toFile()), false);

                } catch (IOException e) {
                }
                LoggingTools.log(new LogEvent(itemToBeMoved.getValue().getName(), newParentFolder.getName(), LogEventType.FILE_MOVED));
            }
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

    private boolean isNameAvaliable(Folder newParent, AbstractFile fileToBeMoved) {

        ArrayList<AbstractFile> listOfFiles = newParent.getContents();
        if (fileToBeMoved.getParentPath().equals(newParent.getPath()))
            return false;
        for (AbstractFile file : listOfFiles) {
            if (file instanceof Folder && file.getName().equals(fileToBeMoved.getName())) {
                return false;
            } else if (file instanceof Document && file.getName().equals(fileToBeMoved.getName())) {
                return false;
            }
        }

        return true;
    }

    private boolean isSubFolder(Folder newParent, AbstractFile fileToBeMoved) {

        if (newParent.getOSPath().toString().contains(fileToBeMoved.getOSPath().toString())) {
            return false;

        } else {
            return true;
        }
    }
}
