package directory;

import directory.files.AbstractFile;
import directory.files.DocumentBuilder;
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

    public static void deleteFile(AbstractFile file){

    }

}
