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

    public void renameFile(String newFolderName){
        String newPath = null;
        String oldPath = getOSPath().toString();

        if(getOSPath().toString().contains("/")){
            int indexOfLast = getPath().toString().lastIndexOf('/');
            newPath = getOSPath().toString().substring(0, indexOfLast) + newFolderName;
        }
        else {
            newPath = getOSPath().toString().replace(getName(),"") + newFolderName;
        }

        setPath(Paths.get(newPath));

        File file = new File(Settings.getServerDocumentsPath() + oldPath);
        File newFile = new File(Settings.getServerDocumentsPath() + getPath().toString());

        if(file.renameTo(newFile)) {
            changeChildrenPath(this, oldPath, newPath);
        }
        AppFilesManager.save(FileManager.getInstance());
        LoggingTools.log(new LogEvent(getName(), LogEventType.FOLDER_RENAMED));
    }

    private void changeChildrenPath(Folder folder, String oldPath, String newPath){
        for(AbstractFile file : folder.getContents()){
            if(file instanceof Document){
                newPath = file.getPath().toString().replace(oldPath, newPath);
                file.setPath(Paths.get(newPath));
            }
            if(file instanceof Folder){
                newPath = file.getPath().toString().replace(oldPath, newPath);
                file.setPath(Paths.get(newPath));
                ((Folder) file).changeChildrenPath((Folder)file, oldPath, newPath);
            }

        }
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

    public Folder findParent(Folder child){
        for(AbstractFile file : getContents()){
            if(file instanceof Folder){
                if(((Folder) file).getContents().contains(child)){
                    return (Folder)file;
                }
                else{
                    return ((Folder) file).findParent(child);
                }
            }
        }
        return child;
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
}
