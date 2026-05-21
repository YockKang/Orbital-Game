package com.main.CoreWorks.database;


import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.ObjectMap;
import com.main.CoreWorks.Factory.Building;
import com.main.CoreWorks.Factory.BuildingTemplate.BuildingTemplate;
import com.main.CoreWorks.JsonProcessor;

public class BuildingDatabase {
    private static final ObjectMap<String, BuildingTemplate> BuildingDB = new ObjectMap<>();

    public static Building register(FileHandle file) {
        BuildingTemplate tp = new BuildingTemplate(file);
        String id = JsonProcessor.read(file).getString("id");
        BuildingDB.put(id, tp);
        return tp.of();
    }

    public static BuildingTemplate getBuildingConstructor(String id) {
        return BuildingDB.get(id);
    }

    public static Building getBuilding(String id) {
        return BuildingDB.get(id).of();
    }

    public static String showDB(){
        return BuildingDB.toString();
    }

}
