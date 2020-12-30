package com.outofbound.rhinoengine.object;

import androidx.annotation.NonNull;

import com.outofbound.rhinoengine.shader.GLShader;

public class TrailShader extends GLShader {

    public TrailShader(String vs, String fs) {
        super(vs, fs);
    }

    @Override
    public void config(@NonNull String aPosition,
                       @NonNull String aColor,
                       String aNormal,
                       @NonNull String uMVPMatrix,
                       String uMVMatrix,
                       String uTime,
                       String uLightsPosition,
                       String uLightsColor,
                       String uLightsIntensity,
                       String uNumLights,
                       String uFloatArray,
                       String uNumFloat){
        super.config(aPosition,aColor,aNormal,uMVPMatrix,uMVMatrix,uTime,uLightsPosition,uLightsColor,uLightsIntensity,uNumLights,uFloatArray,uNumFloat);
    }
}
