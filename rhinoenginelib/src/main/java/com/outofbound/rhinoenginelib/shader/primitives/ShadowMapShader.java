package com.outofbound.rhinoenginelib.shader.primitives;

import android.opengl.GLES20;

import com.outofbound.rhinoenginelib.mesh.GLMesh;
import com.outofbound.rhinoenginelib.shader.GLShader;

public final class ShadowMapShader extends GLShader {

    private GLMesh glMesh;
    private float[] mvpMatrix;
    private final int aPosition;
    private final int uMVPMatrix;

    public ShadowMapShader() {
        super("vs_shadow_map.glsl","fs_shadow_map.glsl");
        GLES20.glUseProgram(getProgram());
        aPosition = getAttrib("aPosition");
        uMVPMatrix = getUniform("uMVPMatrix");
    }

    @Override
    public void bindData() {
        GLES20.glUseProgram(getProgram());
        GLES20.glEnableVertexAttribArray(aPosition);
        GLES20.glVertexAttribPointer(aPosition, glMesh.getSizeVertex(), GLES20.GL_FLOAT, false, 0, glMesh.getVertexBuffer());
        GLES20.glUniformMatrix4fv(uMVPMatrix, 1, false, mvpMatrix, 0);
    }

    @Override
    public void unbindData() {
        GLES20.glDisableVertexAttribArray(aPosition);
    }

    public ShadowMapShader setGLMesh(GLMesh glMesh){
        this.glMesh = glMesh;
        return this;
    }

    public ShadowMapShader setMvpMatrix(float[] mvpMatrix){
        this.mvpMatrix = mvpMatrix;
        return this;
    }

}
