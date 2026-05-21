package com.main.CoreWorks.Recipe;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.main.CoreWorks.Resources.Resource;
import com.main.CoreWorks.database.ResourceDatabase;

import java.util.Arrays;

public class Recipe {
    protected final Array<Resource> input;
    protected final Array<Integer> inputMult;
    protected final Array<Resource> output;
    protected final Array<Integer> outputMult;
    protected final int duration;
    protected final String name;
    protected final String id;

    public Recipe(Array<Resource> inputs,
                  Array<Resource> outputs,
                  Array<Integer> inputMultiple,
                  Array<Integer> outputMultiple,
                  int dur,
                  String name,
                  String id) {
        this.input = inputs;
        this.inputMult = inputMultiple;
        this.output = outputs;
        this.outputMult = outputMultiple;
        this.duration = dur;
        this.name = name;
        this.id = id;
        while (this.inputMult.size < this.input.size) {
            this.inputMult.add(0);
        }
        while (this.outputMult.size < this.output.size) {
            this.inputMult.add(0);
        }
    }

    public Recipe(Resource[] inputs,
                  Resource[] outputs,
                  Integer[] inputMultiple,
                  Integer[] outputMultiple,
                  int dur,
                  String name,
                  String id) {
        this.input = new Array<Resource>(inputs);
        this.output = new Array<Resource>(outputs);
        this.inputMult = new Array<Integer>(inputMultiple);
        this.outputMult = new Array<Integer>(outputMultiple);
        this.duration = dur;
        this.name = name;
        this.id = id;
        while (this.inputMult.size < this.input.size) {
            this.inputMult.add(0);
        }
        while (this.outputMult.size < this.output.size) {
            this.inputMult.add(0);
        }
    }

    public Recipe(JsonValue data) {
        this.input = new Array<Resource>(
            (Resource[]) Arrays.stream(data.get("InputId").asStringArray())
                .map(ResourceDatabase::get)
                .toArray());
        this.output = new Array<Resource>(
            (Resource[]) Arrays.stream(data.get("OutputId").asStringArray())
                .map(ResourceDatabase::get)
                .toArray());
        this.inputMult = new Array<Integer>(
            Arrays.stream(
                data.get("InputMult")
                    .asIntArray())
                .boxed()
                .toArray(Integer[]::new));
        this.outputMult = new Array<Integer>(
            Arrays.stream(
                data.get("OutputMult")
                    .asIntArray())
                .boxed()
                .toArray(Integer[]::new));
        this.duration = data.getInt("duration");
        this.name = data.getString("Name");
        this.id = data.getString("id");
        while (this.inputMult.size < this.input.size) {
            this.inputMult.add(0);
        }
        while (this.outputMult.size < this.output.size) {
            this.inputMult.add(0);
        }
    }

    @Override
    public String toString() {
        StringBuilder inputStr = new StringBuilder(" ");
        for (int i = 0; i < this.input.size; i++) {
            inputStr.append(input.get(i).toString()).append(" x");
            try {
                inputStr.append(input.get(i).toString());
            } catch (Exception e) {
                inputStr.append("0");
            }
            inputStr.append(" ");
        }
        StringBuilder outputStr = new StringBuilder(" ");
        for (int i = 0; i < this.input.size; i++) {
            outputStr.append(input.get(i).toString()).append(" x");
            try {
                outputStr.append(input.get(i).toString());
            } catch (Exception e) {
                outputStr.append("0");
            }
            outputStr.append(" ");
        }
        return this.name + ":\n" + "["  + inputStr + "]\n" + duration + "\n["  + outputStr + "]";
    }

    public Array<Resource> getInputs() {
        return input;
    }

    public Array<Integer> getInputMultipliers() {
        return inputMult;
    }

    public Array<Resource> getOutputs() {
        return output;
    }

    public Array<Integer> getOutputMultipliers() {
        return outputMult;
    }

    public int getDuration() {
        return duration;
    }
}
