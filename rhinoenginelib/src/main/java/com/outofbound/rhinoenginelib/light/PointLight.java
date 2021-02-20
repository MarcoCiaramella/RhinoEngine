package com.outofbound.rhinoenginelib.light;

import androidx.annotation.NonNull;

import com.outofbound.rhinoenginelib.util.vector.Vector3f;

public class PointLight extends Light {

    private final float constant;
    private final float linear;
    private final float quadratic;

    public PointLight(@NonNull Vector3f position, float constant, float linear, float quadratic) {
        super();
        this.position = position;
        this.constant = constant;
        this.linear = linear;
        this.quadratic = quadratic;
    }

    public Vector3f getPosition(){
        return position;
    }

    public float getConstant() {
        return constant;
    }

    public float getLinear() {
        return linear;
    }

    public float getQuadratic() {
        return quadratic;
    }
}
