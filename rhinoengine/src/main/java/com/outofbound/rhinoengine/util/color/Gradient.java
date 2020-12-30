package com.outofbound.rhinoengine.util.color;

public class Gradient {

    public int colorMin;
    public int colorMax;
    public float min;
    public float max;

    public Gradient(int colorMin, int colorMax, float min, float max){
        this.colorMin = colorMin;
        this.colorMax = colorMax;
        this.min = min;
        this.max = max;
    }
}
