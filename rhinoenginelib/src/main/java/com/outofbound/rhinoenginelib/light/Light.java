package com.outofbound.rhinoenginelib.light;


import com.outofbound.rhinoenginelib.renderer.fx.ShadowMap;
import com.outofbound.rhinoenginelib.util.vector.Vector3f;

public abstract class Light {

    private boolean shadowEnabled;
    private ShadowMap shadowMap;
    private boolean on;
    protected Vector3f position;

    public Light(){
        shadowEnabled = false;
        shadowMap = null;
        on = true;
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

    public Light configShadow(int resolution, float near, float far, float clippingCubeSize){
        shadowMap = new ShadowMap(resolution,position,near,far,clippingCubeSize);
        return this;
    }
}
