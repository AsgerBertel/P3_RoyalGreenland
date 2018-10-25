package Directory;

import java.io.IOException;
import java.util.List;

public class FileExplorer {

    private Folder currentFolder;
    private AccessModifier accessModifier;

    public FileExplorer(Folder startingFolder, AccessModifier accessModifier){
        currentFolder = startingFolder;
        this.accessModifier = accessModifier;
    }

    // Returns the files currently shown in the explorer
    public List<AbstractFile> getShownFiles(){
        try {
            return currentFolder.getContents();
        } catch (IOException e) {
            e.printStackTrace(); // todo Proper error handling
        }
        return null;
    }

    // Navigates to the parent directory
    public boolean navigateBack(){
        currentFolder = new Folder(currentFolder.getParentPath());
        // Todo add error handling in case it cant navigate further back
        return false;
    }


    public void navigateTo(Folder newFolder){
        currentFolder = newFolder; // todo error check
    }





}
