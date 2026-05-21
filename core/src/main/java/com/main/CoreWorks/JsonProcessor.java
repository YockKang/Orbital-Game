package com.main.CoreWorks;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

public class JsonProcessor {
    public static JsonValue read(FileHandle file) {
        JsonReader reader = new JsonReader();
        return reader.parse(file);
    }
}
