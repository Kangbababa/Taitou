package com.example.leon.taitou;

import android.content.Context;
import android.content.res.AssetManager;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;


public class ActivityInference {
    static {
        System.loadLibrary("tensorflow_inference");
    }

    private static ActivityInference activityInferenceInstance;
    private TensorFlowInferenceInterface inferenceInterface;
    private static final String MODEL_FILE = "file:///android_asset/fixed_frozen_har.pb";
    private static final String INPUT_NODE = "in/X";
    private static final String[] OUTPUT_NODES = {"out/Softmax:0"};
    private static final String OUTPUT_NODE = "out/Softmax:0";
    //private static final long[] INPUT_SIZE = {1,270};
    private static final int OUTPUT_SIZE = 5;
    private static AssetManager assetManager;

    public static ActivityInference getInstance(final Context context)
    {
        if (activityInferenceInstance == null)
        {
            activityInferenceInstance = new ActivityInference(context);
        }
        return activityInferenceInstance;
    }

    public ActivityInference(final Context context) {
        this.assetManager = context.getAssets();
        inferenceInterface = new TensorFlowInferenceInterface(assetManager, MODEL_FILE);
    }

    public float[] getActivityProb(float[] input_signal)
    {
        float[] result = new float[OUTPUT_SIZE];
        inferenceInterface.feed(INPUT_NODE,input_signal,1, 227, 227, 3);
        inferenceInterface.run(OUTPUT_NODES);
        inferenceInterface.fetch(OUTPUT_NODE,result);
        //Downstairs	Jogging	  Sitting	Standing	Upstairs	Walking
        return result;
    }
}
