package com.outofbound.rhinoenginelib.light;

import androidx.annotation.NonNull;

import com.outofbound.rhinoenginelib.renderer.fx.ShadowMap;
import com.outofbound.rhinoenginelib.util.vector.Vector3f;

public abstract class Light {

    private final Vector3f ambientColor;
    private final Vector3f diffuseColor;
    private final Vector3f specularColor;
    private boolean shadowEnabled;
    protected ShadowMap shadowMap;
    private boolean on;

    public Light(@NonNull Vector3f ambientColor, @NonNull Vector3f diffuseColor, @NonNull Vector3f specularColor){
        this.ambientColor = ambientColor;
        this.diffuseColor = diffuseColor;
        this.specularColor = specularColor;
        shadowEnabled = false;
        shadowMap = null;
        on = true;
    }

    public Vector3f getAmbientColor(){
        return ambientColor;
    }

    public Vector3f getDiffuseColor(){
        return diffuseColor;
    }

    public Vector3f getSpecularColor(){
        return specularColor;
    }

    public Light enableShadow(){
        shadowEnabled = true;
        return this;
    }

    public Light disableShadow(){
        shadowEnabled = false;
        return this;
    }

    public boolean isShadowEnabled(){
        return shadowEnabled;
    }

    public ShadowMap getShadowMap(){
        return shadowMap;
    }

    public Light on(){
        on = true;
        return this;
    }

    public Light off(){
        on = false;
        return this;
    }

    public boolean isOn(){
        return on;
    }
}
