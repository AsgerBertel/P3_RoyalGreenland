package directory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import directory.files.AbstractFile;
import directory.files.Document;
import directory.files.JSonTest;

public class FileManager {

    // TEST CLASS for later deletion
    Gson gson;

    public FileManager() {
        gson = new GsonBuilder().create();
    }

    public void printFile(Document document){
        gson.toJson(new JSonTest("name", 15654), System.out);
    }




}
