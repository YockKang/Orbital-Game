package com.main.CoreWorks.database;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;
import com.main.CoreWorks.Recipe.Recipe;
import com.main.CoreWorks.Resources.Resource;

/*
Starter Recipes
Mine IronOre
IronOre -> IronIngot
IronIngot -> CannonBall
 */
public class RecipeDatabase {
    private static final ObjectMap<String, Recipe> RecipeDB = new ObjectMap<>();

    public static Recipe register(Array<Resource> inputs,
                                  Array<Resource> outputs,
                                  Array<Integer> inputMultiple,
                                  Array<Integer> outputMultiple,
                                  int dur,
                                  String name,
                                  String id) {
        Recipe type = new Recipe(inputs,
            outputs,
            inputMultiple,
            outputMultiple,
            dur,
            name,
            id);
        RecipeDB.put(id, type);
        return type;
    }

    public static Recipe register(Resource[] inputs,
                                  Resource[] outputs,
                                  Integer[] inputMultiple,
                                  Integer[] outputMultiple,
                                  int dur,
                                  String name,
                                  String id) {
        Recipe type = new Recipe(inputs,
            outputs,
            inputMultiple,
            outputMultiple,
            dur,
            name,
            id);
        RecipeDB.put(id, type);
        return type;
    }

    public static Recipe register(JsonValue data) {
        if (data.isArray()){
            data.forEach(RecipeDatabase::register);
            return null;
        } else {
            Recipe type = new Recipe(data);
            RecipeDB.put(data.getString("id"), type);
            return type;
        }
    }

    public static Recipe get(String id) {
        return RecipeDB.get(id);
    }
}
