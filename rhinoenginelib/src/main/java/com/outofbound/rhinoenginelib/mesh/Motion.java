package com.outofbound.rhinoenginelib.mesh;


import com.outofbound.rhinoenginelib.util.vector.Vector3f;

public abstract class Motion {

    public Vector3f position;
    public Vector3f speed;
    public Vector3f acceleration;
    public Vector3f rotation;
    public Vector3f scale;

    public Motion(){
        position = new Vector3f(0,0,0);
        speed = new Vector3f(0,0,0);
        acceleration = new Vector3f(0,0,0);
        rotation = new Vector3f(0,0,0);
        scale = new Vector3f(1,1,1);
    }

    public abstract void move(long ms);
}
