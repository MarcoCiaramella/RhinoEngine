package com.outofbound.rhinoenginelib.shader.primitives;

import android.opengl.GLES20;

import com.outofbound.rhinoenginelib.shader.GLShader;

import java.nio.FloatBuffer;

public class BlurFinalShader extends GLShader {

    private int aPosition;
    private int aTexture;
    private int uTextureId1;
    private int uTextureId2;
    private int uMVPMatrix;
    private float[] vpMatrix;
    private FloatBuffer vertices;
    private FloatBuffer textureCoords;
    private int texture1;
    private int texture2;

    public BlurFinalShader() {
        super("vs_blur.glsl", "fs_blur_final.glsl");
    }

    @Override
    public void config(int programShader) {
        aPosition = GLES20.glGetAttribLocation(programShader, "aPosition");
        aTexture = GLES20.glGetAttribLocation(programShader, "aTexCoords");
        uTextureId1 = GLES20.glGetUniformLocation(programShader, "uTextId1");
        uTextureId2 = GLES20.glGetUniformLocation(programShader, "uTextId2");
        uMVPMatrix = GLES20.glGetUniformLocation(programShader, "uMVPMatrix");
    }

    @Override
    public void bindData() {
        GLES20.glUseProgram(getProgramShader());
        GLES20.glVertexAttribPointer(aPosition, 2, GLES20.GL_FLOAT, false, 0, vertices);
        GLES20.glEnableVertexAttribArray(aPosition);
        GLES20.glVertexAttribPointer(aTexture, 2, GLES20.GL_FLOAT, false, 0, textureCoords);
        GLES20.glEnableVertexAttribArray(aTexture);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture1);
        GLES20.glUniform1i(uTextureId1, 0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture2);
        GLES20.glUniform1i(uTextureId2, 1);
        GLES20.glUniformMatrix4fv(uMVPMatrix, 1, false, vpMatrix, 0);
    }

    @Override
    public void unbindData() {
        GLES20.glDisableVertexAttribArray(aPosition);
        GLES20.glDisableVertexAttribArray(aTexture);
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
