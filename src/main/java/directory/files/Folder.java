package directory.files;


import javax.naming.InvalidNameException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Folder extends AbstractFile {

    private List<AbstractFile> folderContents = new ArrayList<>();

    public Folder(Path path) {
        super(path);
    }

    @Override
    void renameFile(String newFileName) throws InvalidNameException {
        File currentFile = this.getPath().toFile();
        File renamedFile = new File(path.getParent().toAbsolutePath() + File.separator + newFileName);
        if(!currentFile.renameTo(renamedFile))
            throw new InvalidNameException();
        this.path = Paths.get(renamedFile.getPath());

        for(AbstractFile file : folderContents){
            String strPath = file.getPath().toString();
            int i = strPath.lastIndexOf(File.separator);
            System.out.println("Before: " + strPath);
            strPath = strPath.replace(this.getName(), newFileName);
            file.setPath(Paths.get(strPath));
            System.out.println("After: " + strPath);
        }
        // TODO: 25-10-2018 : add functionality for changing path for all child elements.
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
