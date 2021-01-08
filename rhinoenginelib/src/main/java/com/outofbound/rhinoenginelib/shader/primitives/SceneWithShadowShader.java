package com.outofbound.rhinoenginelib.shader.primitives;

import android.opengl.GLES20;

import com.outofbound.rhinoenginelib.light.GLLights;
import com.outofbound.rhinoenginelib.mesh.GLMesh;
import com.outofbound.rhinoenginelib.shader.GLShader;

public class SceneWithShadowShader extends GLShader {

    private int uMVPMatrixLocation;
    private int uMVMatrixLocation;
    private int aPositionLocation;
    private int aColorLocation;
    private int aNormalLocation;
    private int uLightsPositionLocation;
    private int uLightsColorLocation;
    private int uLightsIntensityLocation;
    private int uShadowMapLocation;
    private int uShadowMVPMatrixLocation;
    private GLMesh glMesh;
    private float[] mMatrix;
    private float[] mvpMatrix;
    private GLLights glLights;
    private int shadowMap;
    private float[] shadowMVPMatrix = new float[16];

    public SceneWithShadowShader(){
        super("vs_scene_shadow.glsl", "fs_scene_shadow.glsl");
        uMVPMatrixLocation = -1;
        uMVMatrixLocation = -1;
        aPositionLocation = -1;
        aColorLocation = -1;
        aNormalLocation = -1;
        uLightsPositionLocation = -1;
        uLightsColorLocation = -1;
        uLightsIntensityLocation = -1;
        uShadowMapLocation = -1;
        uShadowMVPMatrixLocation = -1;
    }

    @Override
    public void config(int programShader) {
        GLES20.glUseProgram(programShader);
        aPositionLocation = GLES20.glGetAttribLocation(programShader,"aPosition");
        aColorLocation = GLES20.glGetAttribLocation(programShader,"aColor");
        aNormalLocation = GLES20.glGetAttribLocation(programShader,"aNormal");
        uMVPMatrixLocation = GLES20.glGetUniformLocation(programShader,"uMVPMatrix");
        uMVMatrixLocation = GLES20.glGetUniformLocation(programShader,"uMVMatrix");
        uLightsPositionLocation = GLES20.glGetUniformLocation(programShader,"uLightsPos");
        uLightsColorLocation = GLES20.glGetUniformLocation(programShader,"uLightsColor");
        uLightsIntensityLocation = GLES20.glGetUniformLocation(programShader,"uLightsIntensity");
        uShadowMapLocation = GLES20.glGetUniformLocation(programShader,"uShadowMap");
        uShadowMVPMatrixLocation = GLES20.glGetUniformLocation(programShader,"uShadowMVPMatrix");
    }

    @Override
    public void bindData() {
        GLES20.glUseProgram(getProgramShader());
        GLES20.glEnableVertexAttribArray(aPositionLocation);
        GLES20.glEnableVertexAttribArray(aNormalLocation);
        GLES20.glEnableVertexAttribArray(aColorLocation);
        GLES20.glVertexAttribPointer(aPositionLocation, glMesh.getSizeVertex(), GLES20.GL_FLOAT, false, 0, glMesh.getVertexBuffer());
        GLES20.glVertexAttribPointer(aNormalLocation, 3, GLES20.GL_FLOAT, false, 0, glMesh.getNormalBuffer());
        GLES20.glVertexAttribPointer(aColorLocation, 4, GLES20.GL_FLOAT, false, 0, glMesh.getColorBuffer());
        GLES20.glUniformMatrix4fv(uMVMatrixLocation, 1, false, mMatrix, 0);
        GLES20.glUniformMatrix4fv(uMVPMatrixLocation, 1, false, mvpMatrix, 0);
        GLES20.glUniform3fv(uLightsPositionLocation, glLights.size(), glLights.getPositions(), 0);
        GLES20.glUniform3fv(uLightsColorLocation, glLights.size(), glLights.getColors(), 0);
        GLES20.glUniform1fv(uLightsIntensityLocation, glLights.size(), glLights.getIntensities(), 0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, shadowMap);
        GLES20.glUniform1i(uShadowMapLocation, 0);
        GLES20.glUniformMatrix4fv(uShadowMVPMatrixLocation, 1, false, shadowMVPMatrix, 0);
    }

    @Override
    public void unbindData() {
        GLES20.glDisableVertexAttribArray(aPositionLocation);
        GLES20.glDisableVertexAttribArray(aNormalLocation);
        GLES20.glDisableVertexAttribArray(aColorLocation);
    }

    public SceneWithShadowShader setGLMesh(GLMesh glMesh){
        this.glMesh = glMesh;
        return this;
    }

    public SceneWithShadowShader setMMatrix(float[] mMatrix){
        this.mMatrix = mMatrix;
        return this;
    }

    public SceneWithShadowShader setMvpMatrix(float[] mvpMatrix){
        this.mvpMatrix = mvpMatrix;
        return this;
    }

    public SceneWithShadowShader setGLLights(GLLights glLights){
        this.glLights = glLights;
        return this;
    }

    public SceneWithShadowShader setShadowMap(int shadowMap){
        this.shadowMap = shadowMap;
        return this;
    }

    public SceneWithShadowShader setShadowMVPMatrix(float[] shadowMVPMatrix){
        this.shadowMVPMatrix = shadowMVPMatrix;
        return this;
    }
}
