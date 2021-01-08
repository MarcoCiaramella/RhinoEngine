package com.outofbound.rhinoenginelib.shader.primitives;

import android.opengl.GLES20;

import com.outofbound.rhinoenginelib.shader.GLShader;

public class BlurStep2Shader extends GLShader {

    private int aPosition;
    private int aTexture;
    private int uTextureId1;
    private int uTextureId2;
    private int uMVPMatrix;

    public BlurStep2Shader() {
        super("vs_blur_renderer.glsl", "fs_blur_renderer.glsl");
    }

    @Override
    public void config(int programShader) {
        aPosition = GLES20.glGetAttribLocation(programShader, "a_position");
        aTexture = GLES20.glGetAttribLocation(programShader, "a_texCoords");
        uTextureId1 = GLES20.glGetUniformLocation(programShader, "u_texId");
        uTextureId2 = GLES20.glGetUniformLocation(programShader, "u_texId1");
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
