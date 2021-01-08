package com.outofbound.rhinoenginelib.shader.primitives;

import android.opengl.GLES20;

import com.outofbound.rhinoenginelib.shader.GLShader;

public class ScreenShader extends GLShader {

    private int aPosition;
    private int aTexture;
    private int uTextureId;
    private int uMVPMatrix;

    public ScreenShader() {
        super("vs_texture.glsl", "fs_texture.glsl");
    }

    @Override
    public void config(int programShader) {
        aPosition = GLES20.glGetAttribLocation(programShader, "a_position");
        aTexture = GLES20.glGetAttribLocation(programShader, "a_texCoords");
        uTextureId = GLES20.glGetUniformLocation(programShader, "u_texId");
        uMVPMatrix = GLES20.glGetUniformLocation(programShader, "u_mvpMatrix");
    }

    @Override
    public GLShader bindData() {
        return null;
    }

    @Override
    public GLShader unbindData() {
        return null;
    }
}
