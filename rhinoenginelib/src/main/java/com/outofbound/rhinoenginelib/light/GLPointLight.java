package com.outofbound.rhinoenginelib.light;

import androidx.annotation.NonNull;

import com.outofbound.rhinoenginelib.util.vector.Vector3f;

public class GLPointLight extends GLLight {

    private final Vector3f position;
    private final float constant;
    private final float linear;
    private final float quadratic;

    public GLPointLight(@NonNull Vector3f position, @NonNull Vector3f ambientColor, @NonNull Vector3f diffuseColor, @NonNull Vector3f specularColor, float constant, float linear, float quadratic) {
        super(ambientColor, diffuseColor, specularColor);
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
