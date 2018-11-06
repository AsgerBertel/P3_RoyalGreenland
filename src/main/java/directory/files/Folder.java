package directory.files;

import javax.naming.InvalidNameException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Folder extends AbstractFile {

    private List<AbstractFile> folderContents = new ArrayList<>();

    public Folder(String path) {
        super(path);
    }

    @Override
    public void renameFile(String newFileName){
        // TODO: 25-10-2018 : add functionality for changing path for all child elements (in relation to the accessmodifier)

        File file = new File(getPath().toString());
        int indexOfLast = getPath().toString().lastIndexOf(File.separator);
        File newFile = new File(getPath().toString().substring(0,indexOfLast)+ File.separator + newFileName);
        this.setPath(newFile.toPath());

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

        Files.walk(getPath(), 1)
                .filter(path1 -> Files.isDirectory(path1) && !path1.equals(getPath()))
                .forEach(file -> folderContents.add(new Folder(file.toAbsolutePath().toString())));

        Files.walk(getPath(), 1)
                .filter(Files::isRegularFile)
                .forEach(file -> folderContents.add(DocumentBuilder.getInstance().createDocument(file.toAbsolutePath())));
    }

}
