package com.main.CoreWorks.Recipe;

import com.badlogic.gdx.utils.Array;
import com.main.CoreWorks.Resources.Resource;

public class Recipe {
    protected final Array<Resource> input;
    protected final Array<Integer> inputMult;
    protected final Array<Resource> output;
    protected final Array<Integer> outputMult;
    protected final int duration;
    protected final String name;

    public Recipe(Array<Resource> inputs,
                  Array<Resource> outputs,
                  Array<Integer> inputMultiple,
                  Array<Integer> outputMultiple,
                  int dur,
                  String name) {
        this.input = inputs;
        this.inputMult = inputMultiple;
        this.output = outputs;
        this.outputMult = outputMultiple;
        this.duration = dur;
        this.name = name;
    }

    public Recipe(Resource[] inputs,
                  Resource[] outputs,
                  Integer[] inputMultiple,
                  Integer[] outputMultiple,
                  int dur,
                  String name) {
        this.input = new Array<Resource>(inputs);
        this.output = new Array<Resource>(outputs);
        this.inputMult = new Array<Integer>(inputMultiple);
        this.outputMult = new Array<Integer>(outputMultiple);
        this.duration = dur;
        this.name = name;
    }


    @Override
    public String toString() {
        StringBuilder inputStr = new StringBuilder(" ");
        for (Resource r : this.input) {
            inputStr.append(r).append(" ");
        }
        StringBuilder outputStr = new StringBuilder(" ");
        for (Resource r : this.input) {
            outputStr.append(r).append(" ");
        }
        return this.name + ":\n" + "["  + inputStr + "]\n" + duration + "\n["  + outputStr + "]";
    }

    public Array<Resource> getInputs() {
        return input;
    }

    public Array<Resource> getOutputs() {
        return output;
    }

    public int getDuration() {
        return duration;
    }
}
