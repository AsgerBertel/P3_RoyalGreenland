package directory;

import directory.access.AccessModifier;
import directory.files.AbstractFile;
import directory.files.Folder;

import java.io.IOException;
import java.util.List;

// Represents a file explorer that maneuvers through the file system
// and provides a list of the files in the current folder

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

    public void navigateTo(Folder newFolder){
        currentFolder = newFolder; // todo error check
    }

    // Navigates to the parent directory
    public boolean navigateBack(){
        currentFolder = new Folder(currentFolder.getParentPath());
        // Todo add error handling in case it cant navigate further back
        return false;
    }





}
