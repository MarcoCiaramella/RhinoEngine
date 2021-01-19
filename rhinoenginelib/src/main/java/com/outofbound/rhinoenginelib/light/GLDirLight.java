package com.outofbound.rhinoenginelib.light;

import androidx.annotation.NonNull;

import com.outofbound.rhinoenginelib.util.vector.Vector3f;

public class GLDirLight extends GLLight {

    private final Vector3f direction;

    public GLDirLight(@NonNull Vector3f direction, @NonNull Vector3f ambientColor, @NonNull Vector3f diffuseColor, @NonNull Vector3f specularColor) {
        super(ambientColor, diffuseColor, specularColor);
        this.direction = direction.normalize();
    }

    public Vector3f getDirection(){
        return direction;
    }

    public Vector3f getPositionAlongDirection(float distance){
        Vector3f position = new Vector3f(0,0,0);
        direction.multS(distance,position);
        position.x = -position.x;
        position.y = -position.y;
        position.z = -position.z;
        return position;
    }
}
