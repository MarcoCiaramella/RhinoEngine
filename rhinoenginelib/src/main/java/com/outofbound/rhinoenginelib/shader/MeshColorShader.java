package com.outofbound.rhinoenginelib.shader;

import android.opengl.GLES20;

public final class MeshColorShader extends MeshShader {

    private final int aColor;

    public MeshColorShader() {
        super("vs_mesh_color.glsl", "fs_mesh_color.glsl");
        aColor = getAttrib("aColor");
    }

    @Override
    public void bindData() {
        super.bindData();
        GLES20.glEnableVertexAttribArray(aColor);
        GLES20.glVertexAttribPointer(aColor, 4, GLES20.GL_FLOAT, false, 0, data.colorBuffer);
    }

    @Override
    public void unbindData() {
        super.unbindData();
        GLES20.glDisableVertexAttribArray(aColor);
    }
}
