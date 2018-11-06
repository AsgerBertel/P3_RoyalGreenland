package json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import directory.files.AbstractFile;
import directory.files.Document;
import directory.files.Folder;

public class JsonParser
{
    private static Gson gson;

    static {
        GsonBuilder gb = new GsonBuilder();

        RuntimeTypeAdapterFactory<AbstractFile> typeFactory = RuntimeTypeAdapterFactory
                .of(AbstractFile.class, "type")
                .registerSubtype(Document.class, "document")
                .registerSubtype(Folder.class, "folder");

        gb.registerTypeAdapterFactory(typeFactory);

        gson = gb.create();
    }

    public static Gson getJsonParser() {
        return gson;
    }
}
