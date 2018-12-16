package model.managing;

import model.Document;
import model.AbstractFile;
import model.Folder;
import model.Plant;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Represents a file explorer that maneuvers through the file system
// and provides a list of the io in the current folder

public class FileExplorer {
    private Folder currentFolder;
    private final Plant viewingPlant;

    private final ArrayList<AbstractFile> files;

    public FileExplorer(ArrayList<AbstractFile> files, Plant viewingPlant) {
        this.files = files;
        this.viewingPlant = viewingPlant;
    }

    public FileExplorer(ArrayList<AbstractFile> files) {
        this.files = files;
        this.viewingPlant = null;
    }

    // Returns the io currently shown in the explorer
    public List<AbstractFile> getShownFiles() {
        ArrayList<AbstractFile> allFiles;

        if (currentFolder == null)
            allFiles = files;
        else
            allFiles = currentFolder.getContents();

        List<AbstractFile> shownFiles = new ArrayList<>();

        // Add io if they are contained in the plant's access modifier
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

    // Navigates to the parent io
    public boolean navigateBack() {
        // Can't navigate further back if not currently in a folder
        if (currentFolder == null)
            return false;

        Folder root = new Folder("", files);
        Optional<Folder> folder = FileManager.findParent(currentFolder, root);
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
}
