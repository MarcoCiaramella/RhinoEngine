package com.outofbound.rhinoenginelib.shader.primitives;

import android.opengl.GLES30;

import com.outofbound.rhinoenginelib.mesh.Mesh;
import com.outofbound.rhinoenginelib.shader.Shader;

public final class ShadowMapShader extends Shader {

    private Mesh mesh;
    private float[] mvpMatrix;
    private final int aPosition;
    private final int uMVPMatrix;

    public ShadowMapShader() {
        super("vs_shadow_map.glsl","fs_shadow_map.glsl");
        GLES30.glUseProgram(getProgram());
        aPosition = getAttrib("aPosition");
        uMVPMatrix = getUniform("uMVPMatrix");
    }

    @Override
    public void bindData() {
        GLES30.glUseProgram(getProgram());
        GLES30.glEnableVertexAttribArray(aPosition);
        GLES30.glVertexAttribPointer(aPosition, mesh.getSizeVertex(), GLES30.GL_FLOAT, false, 0, mesh.getVertexBuffer());
        GLES30.glUniformMatrix4fv(uMVPMatrix, 1, false, mvpMatrix, 0);
    }

    @Override
    public void unbindData() {
        GLES30.glDisableVertexAttribArray(aPosition);
    }

    public ShadowMapShader setMesh(Mesh mesh){
        this.mesh = mesh;
        return this;
    }

    public ShadowMapShader setMvpMatrix(float[] mvpMatrix){
        this.mvpMatrix = mvpMatrix;
        return this;
    }

}
