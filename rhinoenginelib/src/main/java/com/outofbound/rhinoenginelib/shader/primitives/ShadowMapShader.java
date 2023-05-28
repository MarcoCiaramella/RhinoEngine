package com.outofbound.rhinoenginelib.shader.primitives;

import android.opengl.GLES20;

import com.outofbound.rhinoenginelib.mesh.Mesh;
import com.outofbound.rhinoenginelib.shader.Shader;

public final class ShadowMapShader extends Shader {

    private float[] mvpMatrix;
    private final int aPosition;
    private final int uMVPMatrix;

    public ShadowMapShader() {
        super("vs_shadow_map.glsl","fs_shadow_map.glsl");
        use();
        aPosition = getAttrib("aPosition");
        uMVPMatrix = getUniform("uMVPMatrix");
    }

    @Override
    public void bindData() {
        use();
        GLES20.glEnableVertexAttribArray(aPosition);
        GLES20.glVertexAttribPointer(aPosition, 3, GLES20.GL_FLOAT, false, 0, data.vertexBuffer);
        GLES20.glUniformMatrix4fv(uMVPMatrix, 1, false, mvpMatrix, 0);
    }

    @Override
    public void unbindData() {
        GLES20.glDisableVertexAttribArray(aPosition);
    }

    public ShadowMapShader setMvpMatrix(float[] mvpMatrix){
        this.mvpMatrix = mvpMatrix;
        return this;
    }

}
