package com.outofbound.rhinoenginelib.light;

import androidx.annotation.NonNull;

import com.outofbound.rhinoenginelib.renderer.fx.ShadowMap;
import com.outofbound.rhinoenginelib.util.vector.Vector3f;

public class DirLight extends Light {

    private final Vector3f direction;

    public DirLight(@NonNull Vector3f direction, @NonNull Vector3f ambientColor, @NonNull Vector3f diffuseColor, @NonNull Vector3f specularColor) {
        super(ambientColor, diffuseColor, specularColor);
        this.direction = direction.normalize();
    }

    public Vector3f getDirection(){
        return direction;
    }

    private Vector3f getPositionAlongDirection(float distance){
        Vector3f position = new Vector3f(0,0,0);
        direction.multS(distance,position);
        position.x = -position.x;
        position.y = -position.y;
        position.z = -position.z;
        return position;
    }

    public DirLight configShadow(int resolution, float distance, float near, float far, float clippingCubeSize){
        shadowMap = new ShadowMap(resolution,getPositionAlongDirection(distance),near,far,clippingCubeSize);
        return this;
    }
}
