package com.outofbound.rhinoengine.light;

import androidx.annotation.NonNull;

import com.outofbound.rhinoengine.util.vector.Vector3f;

import java.util.Arrays;


public class GLLights {

    private Vector3f[] lightsPosition;
    private Vector3f[] lightsColor;
    private float[] lightsIntensity;
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
        lightsPosition = new Vector3f[MAX_SIZE];
        lightsColor = new Vector3f[MAX_SIZE];
        lightsIntensity = new float[MAX_SIZE];
        lightsPositionArray = new float[MAX_SIZE*3];
        lightsColorArray = new float[MAX_SIZE*4];
        lightsIntensityArray = new float[MAX_SIZE];
        Arrays.fill(lightsPosition,null);
        Arrays.fill(lightsColor,null);
        Arrays.fill(lightsIntensity,-1);
        Arrays.fill(lightsPositionArray,0);
        Arrays.fill(lightsColorArray,0);
    }

    /**
     * Add light.
     * @param id the id must be >= MIN_ID and <= MAX_ID.
     * @param position the position of the light.
     * @param color the color of the light.
     * @param intensity the intensity of the light.
     */
    public synchronized void add(int id, @NonNull Vector3f position, @NonNull Vector3f color, float intensity){
        if (id >= MIN_ID && id <= MAX_ID) {
            lightsPosition[id] = position;
            lightsColor[id] = color;
            lightsIntensity[id] = intensity;
        }
    }

    /**
     * Remove light based on id.
     * @param id the id must be >= MIN_ID and <= MAX_ID.
     */
    public synchronized void remove(int id){
        if (id >= MIN_ID && id <= MAX_ID){
            lightsPosition[id] = null;
            lightsColor[id] = null;
            lightsIntensity[id] = -1;
        }
    }

    public synchronized void removeAll(){
        for (int id = MIN_ID; id <= MAX_ID; id++){
            remove(id);
        }
    }

    public synchronized int size(){
        int numLights = 0;
        for (Vector3f lightPosition : lightsPosition){
            if (lightPosition != null){
                numLights++;
            }
        }
        return numLights;
    }

    private float[] toArray(Vector3f[] in, float[] out){
        Arrays.fill(out,0);
        int i = 0;
        for (Vector3f v : in){
            if (v != null) {
                out[i++] = v.x;
                out[i++] = v.y;
                out[i++] = v.z;
            }
        }
        if (i == 0){
            return null;
        }
        return out;
    }

    private float[] toArray(float[] in, float[] out){
        Arrays.fill(out,0);
        int i = 0;
        for (float f : in){
            if (f != -1) {
                out[i++] = f;
            }
        }
        if (i == 0){
            return null;
        }
        return out;
    }

    public synchronized float[] getPositions(){
        return toArray(lightsPosition,lightsPositionArray);
    }

    public synchronized float[] getColors(){
        return toArray(lightsColor,lightsColorArray);
    }

    public synchronized float[] getIntensities(){
        return toArray(lightsIntensity,lightsIntensityArray);
    }

    public synchronized GLLights setPosition(int id, Vector3f lightPosition){
        lightsPosition[id] = lightPosition;
        return this;
    }

    public synchronized GLLights setColor(int id, Vector3f lightColor){
        lightsColor[id] = lightColor;
        return this;
    }

    public synchronized GLLights setIntensity(int id, float intensity){
        lightsIntensity[id] = intensity;
        return this;
    }

    public synchronized Vector3f getPosition(int id){
        return lightsPosition[id];
    }

    public synchronized Vector3f getColor(int id){
        return lightsColor[id];
    }

    public synchronized float getIntensity(int id){
        return lightsIntensity[id];
    }

    public synchronized int nextAvailableID(){
        for (int id = MIN_ID; id <= MAX_ID; id++){
            if (lightsPosition[id] == null){
                return id;
            }
        }
        return NULL_ID;
    }

    public int getMaxSize(){
        return MAX_SIZE;
    }
}
