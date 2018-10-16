package Directory;


import javafx.scene.image.ImageView;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class Document extends AbstractDocFolder
{
    public Document(String name, ImageView image, String filetype) {
        super(image, name,filetype);
    }

    public void openDocument() throws IOException {
        File file = new File(path.toAbsolutePath().toString()+name);
        Desktop.getDesktop().open(file);
    }

}
