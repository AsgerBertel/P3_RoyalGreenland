package directory;

import directory.files.Document;
import directory.plant.AccessModifier;
import directory.files.AbstractFile;
import directory.files.Folder;
import directory.plant.Plant;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

// Represents a file explorer that maneuvers through the file system
// and provides a list of the files in the current folder

public class FileExplorer {
    private Folder rootDirectory;
    private Folder currentFolder;
    private Plant selectedPlant;

    public FileExplorer(Folder startingFolder, Plant selectedPlant) {
        currentFolder = startingFolder;
        this.selectedPlant = selectedPlant;
        rootDirectory = startingFolder;
    }

    // Returns the files currently shown in the explorer
    public List<AbstractFile> getShownFiles() {
        // todo use selectedPlant.getAccessModifier().contains(file) in an algorithm for finding all shown folder/documents
        ArrayList<AbstractFile> filesWithAccess = new ArrayList<>();
        List<AbstractFile> allFiles = currentFolder.getContents();

        for(AbstractFile file : allFiles){
            if(file instanceof Folder){
                if(((Folder) file).containsFromAccessModifier(selectedPlant.getAccessModifier())){
                    filesWithAccess.add(file);
                }
            }
            if(file instanceof Document){
                if(selectedPlant.getAccessModifier().contains(((Document) file).getID())){
                    filesWithAccess.add(file);
                }
            }
        }

        return filesWithAccess;
    }

    public void navigateTo(Folder newFolder) {
        currentFolder = newFolder; // todo error check
    }

    // Navigates to the parent directory
    public boolean navigateBack() {
        if (!(currentFolder.getPath().equals(rootDirectory.getPath()))) {
            currentFolder = FileManager.getInstance().findParent(currentFolder);
        }

        return false;
    }

    public Folder getCurrentFolder() {
        return currentFolder;
    }
}
