package com.outofbound.rhinoenginelib.light;

import androidx.annotation.NonNull;

import com.outofbound.rhinoenginelib.util.vector.Vector3f;

/**
 * Defines a point light.
 */
public class PointLight extends Light {

    private final float constant;
    private final float linear;
    private final float quadratic;

    /**
     * The constructor.
     * @param position the position of the light
     * @param constant the constant attenuation
     * @param linear the linear attenuation
     * @param quadratic the quadratic attenuation
     */
    public PointLight(@NonNull Vector3f position, float constant, float linear, float quadratic) {
        super();
        this.position = position;
        this.constant = constant;
        this.linear = linear;
        this.quadratic = quadratic;
    }

    /**
     * Returns the position of the light
     * @return the position of the light
     */
    public Vector3f getPosition(){
        return position;
    }

    /**
     * Returns the constant attenuation.
     * @return the constant attenuation
     */
    public float getConstant() {
        return constant;
    }

    /**
     * Returns the linear attenuation.
     * @return the linear attenuation
     */
    public float getLinear() {
        return linear;
    }

    /**
     * Returns the quadratic attenuation.
     * @return the quadratic attenuation
     */
    public float getQuadratic() {
        return quadratic;
    }
}
