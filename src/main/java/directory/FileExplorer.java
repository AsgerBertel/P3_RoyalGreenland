package directory;

import directory.files.Document;
import directory.files.AbstractFile;
import directory.files.Folder;
import directory.plant.AccessModifier;
import directory.plant.Plant;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Represents a file explorer that maneuvers through the file system
// and provides a list of the files in the current folder

public class FileExplorer {
    private Folder currentFolder;
    private Plant viewingPlant;

    private ArrayList<AbstractFile> files;

    public FileExplorer(ArrayList<AbstractFile> files, Plant viewingPlant) {
        this.files = files;
        this.viewingPlant = viewingPlant;
    }

    public FileExplorer(ArrayList<AbstractFile> files) {
        this.files = files;
        this.viewingPlant = null;
    }

    // Returns the files currently shown in the explorer
    public List<AbstractFile> getShownFiles() {
        ArrayList<AbstractFile> allFiles;

        if (currentFolder == null)
            allFiles = files;
        else
            allFiles = currentFolder.getContents();

        List<AbstractFile> shownFiles = new ArrayList<>();

        // Add files if they are contained in the plant's access modifier
        for (AbstractFile file : allFiles) {
            if (file instanceof Folder) {
                if (viewingPlant.getAccessModifier() == null || ((Folder) file).containsFromAccessModifier(viewingPlant.getAccessModifier()))
                    shownFiles.add(file);
            } else if (file instanceof Document) {
                if (viewingPlant.getAccessModifier() == null || viewingPlant.getAccessModifier().contains(((Document) file).getID()))
                    shownFiles.add(file);
            }
        }


        return shownFiles;
    }

    public void navigateTo(Folder newFolder) {
        if(newFolder != null)
            currentFolder = newFolder;
    }

    // Navigates to the parent directory
    public boolean navigateBack() {
        // Can't navigate further back if not currently in a folder
        if (currentFolder == null)
            return false;

        // Find parent folder if it exists
        Optional<Folder> folder = FileManager.findParent(currentFolder, FileManager.getInstance().getMainFilesRoot());
        currentFolder = folder.orElse(null);

        return true;
    }

    public String getCurrentPath() {
        if (currentFolder == null) {
            return "";
        } else {
            return currentFolder.getOSPath().toString();
        }
    }

    public Plant getViewingPlant() {
        return viewingPlant;
    }
}
