package com.outofbound.rhinoenginelib.shader.primitives;

import android.opengl.GLES20;

import com.outofbound.rhinoenginelib.light.GLLights;
import com.outofbound.rhinoenginelib.mesh.GLMesh;
import com.outofbound.rhinoenginelib.shader.GLShader;

public class SceneShader extends GLShader {

    private int uMVPMatrixLocation;
    private int uMMatrixLocation;
    private int aPositionLocation;
    private int aColorLocation;
    private int aNormalLocation;
    private int uLightsPositionLocation;
    private int uLightsColorLocation;
    private int uLightsIntensityLocation;
    private GLMesh glMesh;
    private float[] mMatrix;
    private float[] mvpMatrix;
    private GLLights glLights;

    public SceneShader() {
        super("vs_scene.glsl", "fs_scene.glsl");
    }

    @Override
    public void config(int programShader) {
        GLES20.glUseProgram(programShader);
        aPositionLocation = GLES20.glGetAttribLocation(programShader,"aPosition");
        aColorLocation = GLES20.glGetAttribLocation(programShader,"aColor");
        aNormalLocation = GLES20.glGetAttribLocation(programShader,"aNormal");
        uMVPMatrixLocation = GLES20.glGetUniformLocation(programShader,"uMVPMatrix");
        uMMatrixLocation = GLES20.glGetUniformLocation(programShader,"uMMatrix");
        uLightsPositionLocation = GLES20.glGetUniformLocation(programShader,"uLightsPos");
        uLightsColorLocation = GLES20.glGetUniformLocation(programShader,"uLightsColor");
        uLightsIntensityLocation = GLES20.glGetUniformLocation(programShader,"uLightsIntensity");
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
        GLES20.glUniformMatrix4fv(uMMatrixLocation, 1, false, mMatrix, 0);
        GLES20.glUniformMatrix4fv(uMVPMatrixLocation, 1, false, mvpMatrix, 0);
        GLES20.glUniform3fv(uLightsPositionLocation, glLights.size(), glLights.getPositions(), 0);
        GLES20.glUniform3fv(uLightsColorLocation, glLights.size(), glLights.getColors(), 0);
        GLES20.glUniform1fv(uLightsIntensityLocation, glLights.size(), glLights.getIntensities(), 0);
    }

    @Override
    public void unbindData() {
        GLES20.glDisableVertexAttribArray(aPositionLocation);
        GLES20.glDisableVertexAttribArray(aNormalLocation);
        GLES20.glDisableVertexAttribArray(aColorLocation);
    }

    public SceneShader setGLMesh(GLMesh glMesh){
        this.glMesh = glMesh;
        return this;
    }

    public SceneShader setMMatrix(float[] mMatrix){
        this.mMatrix = mMatrix;
        return this;
    }

    public SceneShader setMvpMatrix(float[] mvpMatrix){
        this.mvpMatrix = mvpMatrix;
        return this;
    }

    public SceneShader setGLLights(GLLights glLights){
        this.glLights = glLights;
        return this;
    }

}
