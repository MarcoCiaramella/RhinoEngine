package com.outofbound.rhinoenginelib.shader.primitives;

import android.opengl.GLES20;

import com.outofbound.rhinoenginelib.shader.GLShader;

import java.nio.FloatBuffer;

public final class BlurHorizontalShader extends GLShader {

    private float[] vpMatrix;
    private FloatBuffer vertices;
    private FloatBuffer textureCoords;
    private float scale;
    private float amount;
    private float strength;
    private int texture;

    public BlurHorizontalShader() {
        super("vs_blur.glsl", "fs_blur_horizontal.glsl");
    }

    @Override
    public void bindData() {
        GLES20.glUseProgram(getProgram());
        GLES20.glVertexAttribPointer(getAttrib("aPosition"), 2, GLES20.GL_FLOAT, false, 0, vertices);
        GLES20.glEnableVertexAttribArray(getAttrib("aPosition"));
        GLES20.glVertexAttribPointer(getAttrib("aTexCoords"), 2, GLES20.GL_FLOAT, false, 0, textureCoords);
        GLES20.glEnableVertexAttribArray(getAttrib("aTexCoords"));
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
        GLES20.glUniform1i(getUniform("uTextId"), 0);
        GLES20.glUniformMatrix4fv(getUniform("uMVPMatrix"), 1, false, vpMatrix, 0);
        GLES20.glUniform1f(getUniform("uBlurScale"),scale);
        GLES20.glUniform1f(getUniform("uBlurAmount"),amount);
        GLES20.glUniform1f(getUniform("uBlurStrength"),strength);
    }

    @Override
    public void unbindData() {
        GLES20.glDisableVertexAttribArray(getAttrib("aPosition"));
        GLES20.glDisableVertexAttribArray(getAttrib("aTexCoords"));
    }

    public BlurHorizontalShader setVpMatrix(float[] vpMatrix){
        this.vpMatrix = vpMatrix;
        return this;
    }

    public BlurHorizontalShader setVertices(FloatBuffer vertices){
        this.vertices = vertices;
        return this;
    }

    public BlurHorizontalShader setTextureCoords(FloatBuffer textureCoords){
        this.textureCoords = textureCoords;
        return this;
    }

    public BlurHorizontalShader setScale(float scale){
        this.scale = scale;
        return this;
    }

    public BlurHorizontalShader setAmount(float amount){
        this.amount = amount;
        return this;
    }

    public BlurHorizontalShader setStrength(float strength){
        this.strength = strength;
        return this;
    }

    public BlurHorizontalShader setTexture(int texture){
        this.texture = texture;
        return this;
    }
}
