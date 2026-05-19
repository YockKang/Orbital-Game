package com.main.CoreWorks.database;

import com.badlogic.gdx.utils.Array;
import com.main.CoreWorks.Recipe.Recipe;
import com.main.CoreWorks.Resources.Resource;

import java.util.HashMap;
import java.util.Map;

/*
Starter Recipes
Mine IronOre
IronOre -> IronIngot
IronIngot -> CannonBall
 */
public class RecipeDatabase {
    private static final Map<String, Recipe> RecipeDB = new HashMap<>();

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

    public static Recipe get(String id) {
        return RecipeDB.get(id);
    }
}
