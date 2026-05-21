package com.main.CoreWorks.Factory.BuildingTemplate;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.main.CoreWorks.Factory.Building;
import com.main.CoreWorks.JsonProcessor;

public class BuildingTemplate {
    Class<? extends Building> clazz;
    JsonValue buildingData;

    public BuildingTemplate(FileHandle file) {
        JsonValue parse = JsonProcessor.read(file);
        String name = "com.main.CoreWorks.Factory.Building." + parse.getString("Name");

        try {
            Class<?> raw = ClassReflection.forName(name);
            if (!Building.class.isAssignableFrom(raw)) {
                throw new IllegalArgumentException(name + " is not a Building");
            }
            clazz = raw.asSubclass(Building.class);
        } catch (ReflectionException e) {
            throw new RuntimeException(e);
        }
        buildingData = parse;
    };

    public Building of() {
        try {
            return clazz.getConstructor(JsonValue.class).newInstance(buildingData);
        } catch (Exception e) {
            return null;
        }
    }

}
