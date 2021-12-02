package com.outofbound.rhinoenginelib.light;


import com.outofbound.rhinoenginelib.util.vector.Vector3f;

/**
 * The abstract class for directional light and point light.
 */
public abstract class Light {

    private boolean shadowEnabled;
    private ShadowMap shadowMap;
    private boolean on;
    protected Vector3f position;

    /**
     * The constructor.
     */
    public Light(){
        shadowEnabled = false;
        shadowMap = null;
        on = true;
    }

    /**
     * Enables shadow.
     * @return this light
     */
    public Light enableShadow(){
        shadowEnabled = true;
        return this;
    }

    /**
     * Disables shadow.
     * @return this light
     */
    public Light disableShadow(){
        shadowEnabled = false;
        return this;
    }

    /**
     * Returns true if the shadow is enabled, false otherwise.
     * @return true if the shadow is enabled, false otherwise
     */
    public boolean isShadowEnabled(){
        return shadowEnabled;
    }

    /**
     * Returns the ShadowMap object.
     * @return the ShadowMap object
     */
    public ShadowMap getShadowMap(){
        return shadowMap;
    }

    /**
     * Turns on this light.
     * @return this light
     */
    public Light on(){
        on = true;
        return this;
    }

    /**
     * Turns off this light.
     * @return this light
     */
    public Light off(){
        on = false;
        return this;
    }

    /**
     * Returns true if this light is on, false otherwise.
     * @return true if this light is on, false otherwise
     */
    public boolean isOn(){
        return on;
    }

    /**
     * Configures the shadow.
     * @param near the near clipping plane. The objects outside this plane will not have the shadow.
     * @param far the far clipping plane. The objects outside this plane will not have the shadow.
     * @param clippingCubeSize the size of the clipping cube. The objects outside this cube will not have the shadow.
     * @return this light
     */
    public Light configShadow(float near, float far, float clippingCubeSize){
        shadowMap = new ShadowMap(position,near,far,clippingCubeSize);
        return this;
    }
}
