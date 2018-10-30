package directory;

import directory.plant.AccessModifier;
import directory.files.AbstractFile;
import directory.files.Folder;
import directory.plant.Plant;

import java.io.IOException;
import java.util.List;

// Represents a file explorer that maneuvers through the file system
// and provides a list of the files in the current folder

public class FileExplorer {

    private Folder currentFolder;
    private Plant selectedPlant;

    public FileExplorer(Folder startingFolder, Plant selectedPlant){
        currentFolder = startingFolder;
        this.selectedPlant = selectedPlant;
    }

    // Returns the files currently shown in the explorer
    public List<AbstractFile> getShownFiles(){

        // todo use selectedPlant.getAccessModifier().contains(file)
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
