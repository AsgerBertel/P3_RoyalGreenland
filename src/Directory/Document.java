package Directory;


import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.naming.InvalidNameException;
import java.awt.*;
import java.io.File;
import java.io.IOException;

import java.nio.file.Path;

public class Document extends AbstractFile
{

    private static Image standardDocImage = new Image("icons/document.png"); // todo add image shit (maybe in superclass)

    public Document(Path path) {
        super(path);
        this.image = standardDocImage;
    }

    @Override
    public void renameFile(String newFileName) throws InvalidNameException {

    }

    @Override
    public void deleteFile(Path path) throws IOException {
        // todo add delete functionality
    }

    public void openDocument() throws IOException {
        File file = new File(path.toAbsolutePath().toString()); // todo is this correctly implemented??
        Desktop.getDesktop().open(file);
    }

}
