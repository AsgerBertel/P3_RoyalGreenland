package json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import directory.files.AbstractFile;
import directory.files.Document;
import directory.files.Folder;

import java.nio.file.Path;

public class JsonParser
{
    private static Gson gson = new Gson();

    static {
        GsonBuilder gb = new GsonBuilder();

        RuntimeTypeAdapterFactory<AbstractFile> typeFactory = RuntimeTypeAdapterFactory
                .of(AbstractFile.class, "type")
                .registerSubtype(Document.class, "document")
                .registerSubtype(Folder.class, "folder");

        gb.registerTypeAdapterFactory(typeFactory);
        gb.registerTypeAdapter(Path.class, PathSerializer.class);
        gb.registerTypeAdapter(Path.class, PathDeserializer.class);

        gson = gb.create();
    }

    public static Gson getGson() {
        return gson;
    }
}
