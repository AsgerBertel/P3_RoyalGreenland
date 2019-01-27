package gui;

import model.managing.FileManager;
import model.AbstractFile;
import model.Document;
import model.Folder;
import controller.FileAdminController;
import log.LogEvent;
import log.LogEventType;
import log.LoggingErrorTools;
import log.LogManager;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.util.Callback;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class DragAndDropTreeCellFactory implements Callback<TreeView<AbstractFile>, TreeCell<AbstractFile>> {
    private TreeCell<AbstractFile> dropZone;
    private TreeItem<AbstractFile> draggedItem;

    private static final DataFormat JAVA_FORMAT = new DataFormat("application/x-java-serialized-object");
    private static final String DROP_HINT_STYLE = "-fx-background-color: #6fd59b; ";
    private final Image folderImg = new Image("icons/bigFolder.png");
    private final Image docImg = new Image("icons/wordIcon.png");
    private final Image pdfImg = new Image("icons/pdfIcon.png");
    private final Image genericImg = new Image("icons/genericIcon.png");
    private final Image imageIcon = new Image("icons/imageIcon.png");
    private final FileAdminController fileAdminController;

    public DragAndDropTreeCellFactory(FileAdminController fileAdminController) {
        this.fileAdminController = fileAdminController;
    }

    @Override
    public TreeCell<AbstractFile> call(TreeView<AbstractFile> treeView) {
        TextFieldTreeCell<AbstractFile> cell = new TextFieldTreeCell<AbstractFile>() {
            @Override
            public void updateItem(AbstractFile item, boolean empty) {
                super.updateItem(item, empty);
                ImageView iv1 = new ImageView();
                if (item == null) return;
                if (item instanceof Folder) {
                    iv1.setImage(folderImg);

                } else {
                    String fileExtension = ((Document) item).getFileExtension();
                    if (fileExtension.contains("docx") || fileExtension.contains("doc")) {
                        iv1.setImage(docImg);
                    } else if (fileExtension.contains("pdf")){
                        iv1.setImage(pdfImg);
                    } else if (fileExtension.contains("jpg") || fileExtension.contains("png") || fileExtension.contains("jpeg")){
                        iv1.setImage(imageIcon);
                    }
                    else {
                        iv1.setImage(genericImg);
                    }
                }
                iv1.setFitWidth(32);
                iv1.setFitHeight(32);

                setGraphic(iv1);
                setText(item.getName());
            }
        };
        cell.setOnDragDetected((
                MouseEvent event) ->

                dragDetected(event, cell, treeView));
        cell.setOnDragOver((
                DragEvent event) ->

                dragOver(event, cell, treeView));
        cell.setOnDragDropped((
                DragEvent event) ->

        {
            try {
                drop(event, cell, treeView);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });


        cell.setOnDragDone((
                DragEvent event) ->

                clearDropLocation());
        return cell;
    }

    private void dragDetected(MouseEvent
                                      event, TreeCell<AbstractFile> treeCell, TreeView<AbstractFile> treeView) {
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
    private void drop(DragEvent event, TreeCell<AbstractFile> destinationTreeCell, TreeView<AbstractFile> treeView) throws FileNotFoundException {
        // Do nothing if no item was dragged
        if (!event.getDragboard().hasContent(JAVA_FORMAT))
            return;

        // Do nothing if dragged into the folder that it's already in
        if (destinationTreeCell.getTreeItem().equals(draggedItem.getParent()))
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
            Optional<Folder> destinationParent = FileManager.findParent(destinationFile, fileManager.getMainFilesRoot());
            // If the document is in the same io, nothing should happen.
            if(destinationParent.isPresent() && destinationParent.equals(FileManager.findParent(fileToMove, fileManager.getMainFilesRoot()))){
                return;
            }
            if (destinationParent.isPresent())
                destinationFolder = destinationParent.get();
            else
                throw new FileNotFoundException("The document that the file was dropped on does not have a parent");
        }

        // Don't allow folder to be drag into on of its' sub folders
        if (fileToMove instanceof Folder && destinationFolder.isSubFolderOf((Folder) fileToMove))
            return;


        try {
            FileManager.getInstance().moveFile(fileToMove, destinationFolder);
        } catch (IOException e) {
            e.printStackTrace();
            AlertBuilder.IOExceptionPopUp();
            LoggingErrorTools.log(e);
        }

        LogManager.log(new LogEvent(fileToMove.getName(), destinationFolder.getName(), LogEventType.FILE_MOVED));
        fileManager.save();
        fileAdminController.update();
        treeView.getSelectionModel().select(draggedItem);
        event.setDropCompleted(true);
    }

    private void clearDropLocation() {
        if (dropZone != null) dropZone.setStyle("");
    }
}
