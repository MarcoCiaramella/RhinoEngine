package com.outofbound.rhinoengine.shader;

import android.opengl.GLES20;

import com.outofbound.rhinoengine.engine.GLEngine;
import com.outofbound.rhinoengine.util.file.TextFileReader;


public class GLShader {

    public final int programShader;
    public int uMVPMatrixLocation;
    public int uMVMatrixLocation;
    public int aPositionLocation;
    public int aColorLocation;
    public int aNormalLocation;
    public int uTimeLocation;
    public int uLightsPositionLocation;
    public int uLightsColorLocation;
    public int uLightsIntensityLocation;
    public int uNumLightsLocation;
    public int uFloatArrayLocation;
    public int uNumFloatLocation;

    public GLShader(String vs, String fs){
        programShader = GLES20.glCreateProgram();

        GLES20.glAttachShader(programShader, loadShader(GLES20.GL_FRAGMENT_SHADER,
                TextFileReader.getString(GLEngine.getInstance().getContext(), fs)));
        GLES20.glAttachShader(programShader, loadShader(GLES20.GL_VERTEX_SHADER,
                TextFileReader.getString(GLEngine.getInstance().getContext(), vs)));
        GLES20.glLinkProgram(programShader);

        uMVPMatrixLocation = -1;
        uMVMatrixLocation = -1;
        aPositionLocation = -1;
        aColorLocation = -1;
        aNormalLocation = -1;
        uTimeLocation = -1;
        uLightsPositionLocation = -1;
        uLightsColorLocation = -1;
        uLightsIntensityLocation = -1;
        uNumLightsLocation = -1;
        uFloatArrayLocation = -1;
        uNumFloatLocation = -1;
    }

    public void config(String aPosition,
                       String aColor,
                       String aNormal,
                       String uMVPMatrix,
                       String uMVMatrix,
                       String uTime,
                       String uLightsPosition,
                       String uLightsColor,
                       String uLightsIntensity,
                       String uNumLights,
                       String uFloatArray,
                       String uNumFloat){

        GLES20.glUseProgram(programShader);

        aPositionLocation = isEmpty(aPosition) ? -1 : GLES20.glGetAttribLocation(programShader,aPosition);
        aColorLocation = isEmpty(aColor) ? -1 : GLES20.glGetAttribLocation(programShader,aColor);
        aNormalLocation = isEmpty(aNormal) ? -1 : GLES20.glGetAttribLocation(programShader,aNormal);
        uMVPMatrixLocation = isEmpty(uMVPMatrix) ? -1 : GLES20.glGetUniformLocation(programShader,uMVPMatrix);
        uMVMatrixLocation = isEmpty(uMVMatrix) ? -1 : GLES20.glGetUniformLocation(programShader,uMVMatrix);
        uTimeLocation = isEmpty(uTime) ? -1 : GLES20.glGetUniformLocation(programShader,uTime);
        uLightsPositionLocation = isEmpty(uLightsPosition) ? -1 : GLES20.glGetUniformLocation(programShader,uLightsPosition);
        uLightsColorLocation = isEmpty(uLightsColor) ? -1 : GLES20.glGetUniformLocation(programShader,uLightsColor);
        uLightsIntensityLocation = isEmpty(uLightsIntensity) ? -1 : GLES20.glGetUniformLocation(programShader,uLightsIntensity);
        uNumLightsLocation = isEmpty(uNumLights) ? -1 : GLES20.glGetUniformLocation(programShader,uNumLights);
        uFloatArrayLocation = isEmpty(uFloatArray) ? -1 : GLES20.glGetUniformLocation(programShader,uFloatArray);
        uNumFloatLocation = isEmpty(uNumFloat) ? -1 : GLES20.glGetUniformLocation(programShader,uNumFloat);

    }

    private boolean isEmpty(String s){
        return s == null || s.isEmpty();
    }


    public static int loadShader(int type, String shaderCode){

        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }
}
