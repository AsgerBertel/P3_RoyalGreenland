package directory.files;

import directory.FileManager;
import directory.Settings;
import directory.plant.AccessModifier;
import gui.log.LogEvent;
import gui.log.LogEventType;
import gui.log.LoggingTools;
import json.AppFilesManager;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Folder extends AbstractFile {
    private ArrayList<AbstractFile> folderContents = new ArrayList<>();

    public Folder(String path) {
        super(path);
    }

    public Folder(String path, ArrayList<AbstractFile> content) {
        super(path);
        this.folderContents = content;
    }
    public Folder(Folder folder) {
        super(folder.getPath().toString());
        this.folderContents = new ArrayList<>(folder.getContents());
    }

    @Override
    public void setPath(Path path) {
        super.setPath(path);
        updateChildrenPaths();
    }

    @Override
    public void setName(String name) {
        super.setName(name);
        updateChildrenPaths();
    }

    private void updateChildrenPaths(){
        for(AbstractFile child : folderContents)
            child.setPath(getPath().resolve(child.getName()));
    }

    // Reads the content o path its given
    public ArrayList<AbstractFile> getContents(){
        return folderContents;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Folder folder = (Folder) o;
        return Objects.equals(folderContents, folder.folderContents);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), folderContents);
    }


    public boolean isSubFolderOf(Folder parent) {
        if(getOSPath().equals(parent.getOSPath())) return false;
        return getOSPath().toString().contains(parent.getOSPath().toString());
    }

}
