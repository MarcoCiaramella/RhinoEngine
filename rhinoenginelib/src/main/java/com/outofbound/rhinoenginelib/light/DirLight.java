package com.outofbound.rhinoenginelib.light;

import androidx.annotation.NonNull;

import com.outofbound.rhinoenginelib.util.vector.Vector3f;

public class DirLight extends Light {

    private final Vector3f direction;

    public DirLight(@NonNull Vector3f direction, @NonNull Vector3f ambientColor, @NonNull Vector3f diffuseColor, @NonNull Vector3f specularColor) {
        super(ambientColor, diffuseColor, specularColor);
        this.direction = direction.normalize();
        position = this.direction.clone();
        position.x = -position.x;
        position.y = -position.y;
        position.z = -position.z;
    }

    public Vector3f getDirection(){
        return direction;
    }
}
