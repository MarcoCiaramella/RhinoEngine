package com.outofbound.rhinoengine.light;

import androidx.annotation.NonNull;

import com.outofbound.rhinoengine.util.vector.Vector3f;

public class GLLight {

    private Vector3f position;
    private Vector3f color;
    private float intensity;

    public GLLight(@NonNull Vector3f position, @NonNull Vector3f color, float intensity){
        this.position = position.clone();
        this.color = color.clone();
        this.intensity = intensity;
    }

    public GLLight setPosition(Vector3f position){
        this.position = position.clone();
        return this;
    }

    public GLLight setColor(Vector3f color){
        this.color = color.clone();
        return this;
    }

    public GLLight setIntensity(float intensity){
        this.intensity = intensity;
        return this;
    }

    public Vector3f getPosition(){
        return position;
    }

    public Vector3f getColor(){
        return color;
    }

    public float getIntensity(){
        return intensity;
    }
}
