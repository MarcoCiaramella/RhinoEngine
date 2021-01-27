package com.outofbound.rhinoenginelib.light;

import androidx.annotation.NonNull;

import com.outofbound.rhinoenginelib.util.vector.Vector3f;

public class DirLight extends Light {

    private final Vector3f direction;

    public DirLight(@NonNull Vector3f direction, @NonNull Vector3f ambientColor, @NonNull Vector3f diffuseColor, @NonNull Vector3f specularColor) {
        super(ambientColor, diffuseColor, specularColor);
        this.direction = direction.normalize();
        position = new Vector3f(-this.direction.x,-this.direction.y,-this.direction.z);
    }

    public Vector3f getDirection(){
        return direction;
    }
}
