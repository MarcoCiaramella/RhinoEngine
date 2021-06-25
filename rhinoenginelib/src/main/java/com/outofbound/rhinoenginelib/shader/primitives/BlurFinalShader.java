package com.outofbound.rhinoenginelib.shader.primitives;

import android.opengl.GLES30;

import com.outofbound.rhinoenginelib.shader.Shader;

import java.nio.FloatBuffer;

public final class BlurFinalShader extends Shader {

    private float[] vpMatrix;
    private FloatBuffer vertices;
    private FloatBuffer textureCoords;
    private int texture1;
    private int texture2;
    private final int aPosition;
    private final int aTexCoords;
    private final int uTextId1;
    private final int uTextId2;
    private final int uMVPMatrix;

    public BlurFinalShader() {
        super("vs_blur.glsl", "fs_blur_final.glsl");
        GLES30.glUseProgram(getProgram());
        aPosition = getAttrib("aPosition");
        aTexCoords = getAttrib("aTexCoords");
        uTextId1 = getUniform("uTextId1");
        uTextId2 = getUniform("uTextId2");
        uMVPMatrix = getUniform("uMVPMatrix");
    }

    @Override
    public void bindData() {
        GLES30.glUseProgram(getProgram());
        GLES30.glVertexAttribPointer(aPosition, 2, GLES30.GL_FLOAT, false, 0, vertices);
        GLES30.glEnableVertexAttribArray(aPosition);
        GLES30.glVertexAttribPointer(aTexCoords, 2, GLES30.GL_FLOAT, false, 0, textureCoords);
        GLES30.glEnableVertexAttribArray(aTexCoords);
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texture1);
        GLES30.glUniform1i(uTextId1, 0);
        GLES30.glActiveTexture(GLES30.GL_TEXTURE1);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texture2);
        GLES30.glUniform1i(uTextId2, 1);
        GLES30.glUniformMatrix4fv(uMVPMatrix, 1, false, vpMatrix, 0);
    }

    @Override
    public void unbindData() {
        GLES30.glDisableVertexAttribArray(aPosition);
        GLES30.glDisableVertexAttribArray(aTexCoords);
    }

    public BlurFinalShader setVpMatrix(float[] vpMatrix){
        this.vpMatrix = vpMatrix;
        return this;
    }

    public BlurFinalShader setVertices(FloatBuffer vertices){
        this.vertices = vertices;
        return this;
    }

    public BlurFinalShader setTextureCoords(FloatBuffer textureCoords){
        this.textureCoords = textureCoords;
        return this;
    }

    public BlurFinalShader setTexture1(int texture1){
        this.texture1 = texture1;
        return this;
    }

    public BlurFinalShader setTexture2(int texture2){
        this.texture2 = texture2;
        return this;
    }
}
