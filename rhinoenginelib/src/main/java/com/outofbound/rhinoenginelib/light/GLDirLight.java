package com.outofbound.rhinoenginelib.light;

import androidx.annotation.NonNull;

import com.outofbound.rhinoenginelib.util.vector.Vector3f;

public class GLDirLight extends GLLight {

    private final Vector3f direction;

    public GLDirLight(@NonNull Vector3f direction, @NonNull Vector3f ambientColor, @NonNull Vector3f diffuseColor, @NonNull Vector3f specularColor) {
        super(ambientColor, diffuseColor, specularColor);
        this.direction = direction;
    }

    public Vector3f getDirection(){
        return direction;
    }

    public Vector3f getPositionAlongDirection(){
        Vector3f pos = new Vector3f(0,0,0);
        direction.multS(100,pos);
        pos.x = -pos.x;
        pos.y = -pos.y;
        pos.z = -pos.z;
        return pos;
    }
}
