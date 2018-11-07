package directory.files;

import directory.FileManager;
import directory.plant.AccessModifier;

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

    public Folder(String path, boolean firstTime) {
        super(path);
        if(firstTime){
            folderInit();
        }

    }

    @Override
    public void renameFile(String newFileName){
        // TODO: 25-10-2018 : add functionality for changing path for all child elements (in relation to the accessmodifier)

        File file = new File(getPath().toString());
        int indexOfLast = getPath().toString().lastIndexOf(File.separator);
        File newFile = new File(getPath().toString().substring(0,indexOfLast)+ File.separator + newFileName);
        this.setPath(newFile.toPath());

        if(file.renameTo(newFile)) {
            FileManager.getInstance().updateFilesJson();
        }
    }


    // Reads the content o path its given
    public List<AbstractFile> getContents(){
        return folderContents;
    }

    // Reads the list of files within the folder
    private void folderInit() {
        folderContents.clear();

        try {
            Files.walk(getPath(), 1)
                    .filter(path1 -> Files.isDirectory(path1) && !path1.equals(getPath()))
                    .forEach(file -> folderContents.add(new Folder(file.toString())));

            Files.walk(getPath(), 1)
                    .filter(Files::isRegularFile)
                    .forEach(file -> folderContents.add(DocumentBuilder.getInstance().createDocument(file)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean containsFromAccessModifier(AccessModifier am){
        List<AbstractFile> allContent = getContents();

        if(allContent.isEmpty()){
            return false;
        }

        for(AbstractFile file : allContent){
            if(file instanceof Folder){
                if(((Folder) file).containsFromAccessModifier(am)){
                    return true;
                }
            }
            if(file instanceof Document){
                if(am.contains(((Document) file).getID())){
                    return true;
                }
            }
        }
        return false;
    }
}
