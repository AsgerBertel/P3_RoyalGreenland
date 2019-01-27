package gui.custom_node;

import model.AbstractFile;
import model.Document;
import model.Folder;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class FileButton extends Button {

    private AbstractFile file;
    private Image icon;

    private static final Image wordFileIcon = new Image("/icons/wordIcon.png");
    private static final Image pdfFileIcon = new Image("/icons/pdfIcon.png");
    private static final Image folderIcon = new Image("/icons/bigFolder.png");
    private static final Image genericIcon = new Image("/icons/genericIcon.png");
    private static final Image imageIcon = new Image("/icons/imageIcon.png");

    private static final Image defaultIcon = wordFileIcon;
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
        } else if (file instanceof Document) {
            String fileExtension = ((Document) file).getFileExtension();
            if (fileExtension.contains("docx") || fileExtension.contains("doc")) {
                icon = wordFileIcon;
            } else if (fileExtension.contains("pdf")) {
                icon = pdfFileIcon;
            } else if (fileExtension.contains("png") || fileExtension.contains("jpg") || fileExtension.contains("jpeg")){
                icon = imageIcon;
            }
            else {
                icon = genericIcon;
            }
        }

        imageView.setFitHeight(65);
        imageView.setFitWidth(65);

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