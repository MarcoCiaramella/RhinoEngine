package com.outofbound.rhinoenginelib.physics;


public class Gravity {

    private final float gS;
    private float position;
    private float speed;

    public Gravity(float gS, float position){
        this.gS = gS;
        this.position = position;
        speed = 0;
    }

    public float calc(long ms){
        float s = ms/1000f;
        position += 0.5f*gS*s*s + speed*s;
        speed += gS*s;
        return position;
    }
}
