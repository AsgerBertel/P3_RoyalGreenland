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
    private ImageView imageView;

    private static Image wordFileIcon = new Image("/icons/document.png");
    private static Image pdfFileIcon = new Image("/icons/document.png");
    private static Image folderIcon = new Image("/icons/big_folder.png");

    private static Image defaultIcon = wordFileIcon; // todo add a default icon for uknown file types

    public FileButton(AbstractFile file) {
        this.file = file;
        this.setText(file.getName());

        assignIcon();
        setGraphic(new ImageView(icon));
    }

    // Sets the icon according to the file type/extension
    private void assignIcon() {
        icon = defaultIcon;

        if (file instanceof Folder) {
            icon = folderIcon;
        } else if (file instanceof Document) {
            String fileExtension = ((Document) file).getFileExtension();

            if (fileExtension.contains("docx") || fileExtension.contains("doc")) {
                icon = wordFileIcon;
            } else if (fileExtension.contains("pdf")) {
                icon = pdfFileIcon;
            }
        }
    }

    public void setFile(AbstractFile file) {
        this.file = file;
    }

    public AbstractFile getFile() {
        return file;
    }
}
