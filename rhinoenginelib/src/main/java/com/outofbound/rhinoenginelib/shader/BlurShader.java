package com.outofbound.rhinoenginelib.shader;

import android.opengl.GLES20;

import java.nio.FloatBuffer;

public final class BlurShader extends Shader {

    private FloatBuffer vertices;
    private FloatBuffer textureCoords;
    private int texture;
    private final int aPosition;
    private final int aTexCoords;
    private final int uTextureId;
    private final int uRadius;

    public BlurShader() {
        super("vs_blur.glsl", "fs_blur.glsl");
        use();
        aPosition = getAttrib("aPosition");
        aTexCoords = getAttrib("aTexCoords");
        uTextureId = getUniform("uTextureId");
        uRadius = getUniform("uRadius");
    }

    @Override
    public void bindData() {
        use();
        GLES20.glVertexAttribPointer(aPosition, 2, GLES20.GL_FLOAT, false, 0, vertices);
        GLES20.glEnableVertexAttribArray(aPosition);
        GLES20.glVertexAttribPointer(aTexCoords, 2, GLES20.GL_FLOAT, false, 0, textureCoords);
        GLES20.glEnableVertexAttribArray(aTexCoords);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
        GLES20.glUniform1i(uTextureId, 0);
        GLES20.glUniform1f(uRadius, 1.0f);
    }

    @Override
    public void unbindData() {
        GLES20.glDisableVertexAttribArray(aPosition);
        GLES20.glDisableVertexAttribArray(aTexCoords);
    }

    public BlurShader setVertices(FloatBuffer vertices){
        this.vertices = vertices;
        return this;
    }

    public BlurShader setTextureCoords(FloatBuffer textureCoords){
        this.textureCoords = textureCoords;
        return this;
    }

    public BlurShader setTexture(int texture){
        this.texture = texture;
        return this;
    }
}
