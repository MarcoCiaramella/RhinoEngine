package com.outofbound.rhinoenginelib.light;

import androidx.annotation.NonNull;

import com.outofbound.rhinoenginelib.renderer.fx.GLShadowMap;
import com.outofbound.rhinoenginelib.util.vector.Vector3f;

public abstract class GLLight {

    private final Vector3f ambientColor;
    private final Vector3f diffuseColor;
    private final Vector3f specularColor;
    private boolean shadowEnabled = false;
    protected GLShadowMap glShadowMap = null;

    public GLLight(@NonNull Vector3f ambientColor, @NonNull Vector3f diffuseColor, @NonNull Vector3f specularColor){
        this.ambientColor = ambientColor;
        this.diffuseColor = diffuseColor;
        this.specularColor = specularColor;
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

    public GLLight enableShadow(){
        shadowEnabled = true;
        return this;
    }

    public GLLight disableShadow(){
        shadowEnabled = false;
        return this;
    }

    public boolean isShadowEnabled(){
        return shadowEnabled;
    }

    public GLShadowMap getGLShadowMap(){
        return glShadowMap;
    }
}
