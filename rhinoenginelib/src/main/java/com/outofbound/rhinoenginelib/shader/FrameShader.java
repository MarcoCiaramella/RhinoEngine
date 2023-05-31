package com.outofbound.rhinoenginelib.shader;

import android.opengl.GLES20;

import java.nio.FloatBuffer;

public final class FrameShader extends Shader {

    private FloatBuffer vertices;
    private FloatBuffer textureCoords;
    private int texture;
    private final int aPosition;
    private final int aTexCoords;
    private final int uTextureId;

    public FrameShader() {
        super("vs_frame.glsl", "fs_frame.glsl");
        use();
        aPosition = getAttrib("aPosition");
        aTexCoords = getAttrib("aTexCoords");
        uTextureId = getUniform("uTextureId");
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
    }

    @Override
    public void unbindData() {
        GLES20.glDisableVertexAttribArray(aPosition);
        GLES20.glDisableVertexAttribArray(aTexCoords);
    }

    public FrameShader setVertices(FloatBuffer vertices){
        this.vertices = vertices;
        return this;
    }

    public FrameShader setTextureCoords(FloatBuffer textureCoords){
        this.textureCoords = textureCoords;
        return this;
    }

    public FrameShader setTexture(int texture){
        this.texture = texture;
        return this;
    }
}
