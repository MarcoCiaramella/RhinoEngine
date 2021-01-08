package com.outofbound.rhinoenginelib.shader.primitives;

import android.opengl.GLES20;

import com.outofbound.rhinoenginelib.shader.GLShader;

public class BlurStep1Shader extends GLShader {

    private int uMVPMatrix;
    private int aPosition;
    private int aTexture;
    private int uDirection;
    private int uBlurScale;
    private int uBlurAmount;
    private int uBlurStrength;
    private int uTextureId;

    public BlurStep1Shader() {
        super("vs_blur.glsl", "fs_blur.glsl");
    }

    @Override
    public void config(int programShader) {
        uMVPMatrix = GLES20.glGetUniformLocation(programShader, "uMVPMatrix");
        aPosition = GLES20.glGetAttribLocation(programShader, "aPosition");
        aTexture = GLES20.glGetAttribLocation(programShader, "aTexCoords");
        uDirection = GLES20.glGetUniformLocation(programShader, "direction");
        uBlurScale = GLES20.glGetUniformLocation(programShader, "blurScale");
        uBlurAmount = GLES20.glGetUniformLocation(programShader, "blurAmount");
        uBlurStrength = GLES20.glGetUniformLocation(programShader, "blurStrength");
        uTextureId = GLES20.glGetUniformLocation(programShader, "u_texId");
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
