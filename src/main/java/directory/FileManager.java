package directory;

import directory.files.Document;
import directory.files.Folder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileManager {
    // todo Archive folder path should be set on setup
    Path pathToArchive = Paths.get("Sample files/Archive");

    public static void uploadFile(Path src, Path dst){
        File file = new File(src.toString());
        Path dest = Paths.get(dst.toString() + File.separator + file.getName());
        try {
            Files.copy(src, dest);
        } catch (IOException e) {
            System.out.println("Could not copy/upload file");
            e.printStackTrace();
        } // todo Error handling.
        // todo do we first create the file, when we upload it? Is this correctly implemented then?
    }

    public static Folder createFolder(Path path, String name){
        // Todo Error handling
        String pathToFolder = path.toString() + File.separator + name;
        Folder folder = new Folder(Paths.get(pathToFolder));
        new File(path.toString() + File.separator + name).mkdirs();
        return folder;
    }

    public void deleteDocument(Document file) throws IOException {
        Path pathWithName = Paths.get(pathToArchive.toAbsolutePath() + File.separator + file.getName());
        Files.move(file.getPath(), pathWithName);

        //deleteEmptyFolders(file.getPath());
    }

    public void restoreDocument (Document file) throws IOException {

        Path file1 = Paths.get(pathToArchive.toAbsolutePath() + File.separator + file.getName());

        if (Files.exists(file.getParentPath())) {
            Files.move(file1, file.getPath());
        } else {
            file.getParentPath().toFile().mkdirs();
            Files.move(file1, file.getPath());
        }
    }

    /*
    private void deleteEmptyFolders(Path path) throws IOException {

        Folder folder = new Folder(path.getParent());

        File file = new File(folder.getPath().toString());

        while (file.isDirectory() && file.length() == 0){
            Files.delete(folder.getPath());
            folder = new Folder(folder.getParentPath());
            file = new File(folder.getPath().toString());
        }
    }*/

    public void deleteFolder(){

    }

}
