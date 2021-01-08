package com.outofbound.rhinoenginelib.shader.primitives;

import android.opengl.GLES20;

import com.outofbound.rhinoenginelib.mesh.GLMesh;
import com.outofbound.rhinoenginelib.shader.GLShader;

public class ShadowMapShader extends GLShader {

    private int uMVPMatrixLocation;
    private int uMVMatrixLocation;
    private int aPositionLocation;
    private GLMesh glMesh;
    private float[] mMatrix;
    private float[] mvpMatrix;

    public ShadowMapShader() {
        super("vs_shadow_map.glsl","fs_shadow_map.glsl");
    }

    @Override
    public void config(int programShader) {
        GLES20.glUseProgram(programShader);
        aPositionLocation = GLES20.glGetAttribLocation(programShader,"aPosition");
        uMVPMatrixLocation = GLES20.glGetUniformLocation(programShader,"uMVPMatrix");
        uMVMatrixLocation = GLES20.glGetUniformLocation(programShader,"uMVMatrix");
    }

    @Override
    public void bindData() {
        GLES20.glUseProgram(getProgramShader());
        GLES20.glEnableVertexAttribArray(aPositionLocation);
        GLES20.glVertexAttribPointer(aPositionLocation, glMesh.getSizeVertex(), GLES20.GL_FLOAT, false, 0, glMesh.getVertexBuffer());
        GLES20.glUniformMatrix4fv(uMVMatrixLocation, 1, false, mMatrix, 0);
        GLES20.glUniformMatrix4fv(uMVPMatrixLocation, 1, false, mvpMatrix, 0);
    }

    @Override
    public void unbindData() {
        GLES20.glDisableVertexAttribArray(aPositionLocation);
    }

    public ShadowMapShader setGLMesh(GLMesh glMesh){
        this.glMesh = glMesh;
        return this;
    }

    public ShadowMapShader setMMatrix(float[] mMatrix){
        this.mMatrix = mMatrix;
        return this;
    }

    public ShadowMapShader setMvpMatrix(float[] mvpMatrix){
        this.mvpMatrix = mvpMatrix;
        return this;
    }

}
