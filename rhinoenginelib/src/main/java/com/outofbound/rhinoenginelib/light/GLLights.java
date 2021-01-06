package com.outofbound.rhinoenginelib.light;

import androidx.annotation.NonNull;

import com.outofbound.rhinoenginelib.util.vector.Vector3f;

import java.util.Arrays;


public class GLLights {

    private GLLight[] lights;
    private float[] lightsPositionArray;
    private float[] lightsColorArray;
    private float[] lightsIntensityArray;
    private final int MAX_SIZE;
    private final int MIN_ID = 0;
    private final int MAX_ID;
    public static final int NULL_ID = -1;


    public GLLights(int maxSize){
        MAX_SIZE = maxSize;
        MAX_ID = MAX_SIZE-1;
        lights = new GLLight[MAX_SIZE];
        lightsPositionArray = new float[MAX_SIZE*3];
        lightsColorArray = new float[MAX_SIZE*4];
        lightsIntensityArray = new float[MAX_SIZE];
        Arrays.fill(lights,null);
    }

    /**
     * Add light.
     * @param id the id must be >= MIN_ID and <= MAX_ID.
     * @param light the GLLight to add.
     */
    public synchronized void add(int id, @NonNull GLLight light){
        if (id >= MIN_ID && id <= MAX_ID) {
            lights[id] = light;
        }
    }

    /**
     * Return the light with the specified id.
     * @param id the id must be >= MIN_ID and <= MAX_ID.
     * @return the GLLight, null if id is out of range.
     */
    public GLLight getLight(int id){
        if (id >= MIN_ID && id <= MAX_ID) {
            return lights[id];
        }
        return null;
    }

    /**
     * Remove light based on id.
     * @param id the id must be >= MIN_ID and <= MAX_ID.
     */
    public synchronized void remove(int id){
        if (id >= MIN_ID && id <= MAX_ID){
            lights[id] = null;
        }
    }

    public synchronized void removeAll(){
        for (int id = MIN_ID; id <= MAX_ID; id++){
            remove(id);
        }
    }

    public synchronized int size(){
        int numLights = 0;
        for (GLLight light : lights){
            if (light != null){
                numLights++;
            }
        }
        return numLights;
    }

    private int toArray(Vector3f v, float[] out, int index){
        out[index++] = v.x;
        out[index++] = v.y;
        out[index++] = v.z;
        return index;
    }

    private int toArray(float f, float[] out, int index){
        out[index++] = f;
        return index;
    }

    public synchronized float[] getPositions(){
        int index = 0;
        for (GLLight light : lights){
            if (light != null) {
                index = toArray(light.getPosition(), lightsPositionArray, index);
            }
        }
        if (index == 0){
            return null;
        }
        return lightsPositionArray;
    }

    public synchronized float[] getColors(){
        int index = 0;
        for (GLLight light : lights){
            if (light != null) {
                index = toArray(light.getColor(), lightsColorArray, index);
            }
        }
        if (index == 0){
            return null;
        }
        return lightsColorArray;
    }

    public synchronized float[] getIntensities(){
        int index = 0;
        for (GLLight light : lights){
            if (light != null) {
                index = toArray(light.getIntensity(), lightsIntensityArray, index);
            }
        }
        if (index == 0){
            return null;
        }
        return lightsIntensityArray;
    }

    public synchronized int nextAvailableID(){
        for (int id = MIN_ID; id <= MAX_ID; id++){
            if (lights[id] == null){
                return id;
            }
        }
        return NULL_ID;
    }

    public int getMaxSize(){
        return MAX_SIZE;
    }

    public GLLight getFirstLight(){
        for (GLLight glLight : lights){
            if (glLight != null){
                return glLight;
            }
        }
        return null;
    }
}
