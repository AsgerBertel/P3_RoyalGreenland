package io.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.AbstractFile;
import model.Document;
import model.Folder;

class JsonParser
{
    private static final Gson gson;

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
