package com.outofbound.rhinoenginelib.light;

import androidx.annotation.NonNull;

import com.outofbound.rhinoenginelib.util.vector.Vector3f;

public class DirLight extends Light {

    private final Vector3f direction;

    public DirLight(@NonNull Vector3f direction) {
        super();
        this.direction = direction.normalize();
        position = new Vector3f(-this.direction.x,-this.direction.y,-this.direction.z);
    }

    public Vector3f getDirection(){
        return direction;
    }
}
