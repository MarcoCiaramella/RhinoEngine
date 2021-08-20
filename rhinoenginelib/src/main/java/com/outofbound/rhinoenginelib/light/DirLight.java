package com.outofbound.rhinoenginelib.light;

import androidx.annotation.NonNull;

import com.outofbound.rhinoenginelib.util.vector.Vector3f;

/**
 * Defines a direction light.
 */
public class DirLight extends Light {

    private final Vector3f direction;

    /**
     * The constructor.
     * @param direction the direction of the light
     */
    public DirLight(@NonNull Vector3f direction) {
        super();
        this.direction = direction.normalize();
        position = new Vector3f(-this.direction.x,-this.direction.y,-this.direction.z);
    }

    /**
     * Returns the direction of the light.
     * @return the direction of the light
     */
    public Vector3f getDirection(){
        return direction;
    }
}
