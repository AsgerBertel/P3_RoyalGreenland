package gui.file_overview;

import directory.files.AbstractFile;
import directory.files.Document;
import directory.files.Folder;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class FileButton extends Button {

    private AbstractFile file;
    private Image icon;

    private static Image wordFileIcon = new Image("/icons/bigWordDoc.png");
    private static Image pdfFileIcon = new Image("/icons/bigPdfDoc.png");
    private static Image folderIcon = new Image("/icons/bigFolder.png");

    private static Image defaultIcon = wordFileIcon;
    private ImageView imageView;

    public FileButton(AbstractFile file) {
        this.file = file;
        this.setText(file.getName());

        assignIcon();
    }

    // Sets the icon according to the file type/extension
    private void assignIcon() {
        imageView = new ImageView();

        icon = defaultIcon;

        if (file instanceof Folder) {
            icon = folderIcon;
            imageView.setFitHeight(65);
            imageView.setFitWidth(65);
        } else if (file instanceof Document) {
            String fileExtension = ((Document) file).getFileExtension();
            if (fileExtension.contains("docx") || fileExtension.contains("doc")) {
                icon = wordFileIcon;
            } else if (fileExtension.contains("pdf")) {
                icon = pdfFileIcon;
            }
            imageView.setFitHeight(54);
            imageView.setFitWidth(46);
        }

        imageView.setImage(icon);
        setGraphic(imageView);
    }

    public void setFile(AbstractFile file) {
        this.file = file;
    }

    public AbstractFile getFile() {
        return file;
    }
}
