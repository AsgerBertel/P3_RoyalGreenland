package directory.files;


import javafx.collections.FXCollections;
import javafx.scene.image.Image;

import javax.naming.InvalidNameException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Folder extends AbstractFile {

    private List<AbstractFile> folderContents = new ArrayList<>();

    public Folder(Path path) {
        super(path);
    }

    @Override
    public void renameFile(String newFileName) throws InvalidNameException {
        // TODO: 25-10-2018 : add functionality for changing path for all child elements (in relation to the accessmodifier)

        File file = new File(path.toString());
        int indexOfLast = path.toString().lastIndexOf(File.separator);
        File newFile = new File(path.toString().substring(0,indexOfLast)+"\\"+newFileName);
        this.path = newFile.toPath();

        if(file.renameTo(newFile)) {
            try {
                updateContents();
            } catch (IOException e) {
                e.printStackTrace(); // todo error handling
            }
        }
    }

    // Reads the content o path its given
    public List<AbstractFile> getContents(){
        try {
            updateContents();
        } catch (IOException e) {
            e.printStackTrace(); // todo error handling
        }
        return folderContents;
    }

    // Reads the list of files within the folder
    private void updateContents() throws IOException{
        folderContents.clear();

        Files.walk(path, 1)
                .filter(path1 -> Files.isDirectory(path1) && !path1.equals(path))
                .forEach(file -> folderContents.add(new Folder(file.toAbsolutePath())));

        Files.walk(path, 1)
                .filter(Files::isRegularFile)
                .forEach(file -> folderContents.add(DocumentBuilder.getInstance().createDocument(file.toAbsolutePath())));
    }

}
