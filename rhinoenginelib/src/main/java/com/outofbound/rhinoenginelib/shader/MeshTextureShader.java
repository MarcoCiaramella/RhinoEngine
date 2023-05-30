package com.outofbound.rhinoenginelib.shader;

import android.opengl.GLES20;

public final class MeshTextureShader extends MeshShader {

    private final int aTexCoords;
    private final int uTexture;

    public MeshTextureShader() {
        super("vs_mesh_texture.glsl", "fs_mesh_texture.glsl");
        aTexCoords = getAttrib("aTexCoords");
        uTexture = getUniform("uTexture");
    }

    @Override
    public void bindData() {
        super.bindData();
        GLES20.glEnableVertexAttribArray(aTexCoords);
        GLES20.glVertexAttribPointer(aTexCoords, 2, GLES20.GL_FLOAT, false, 0, data.texCoordsBuffer);
        bindTexture(uTexture, data.texture, textureIndex);
    }

    @Override
    public void unbindData() {
        super.unbindData();
        GLES20.glDisableVertexAttribArray(aTexCoords);
    }

}
