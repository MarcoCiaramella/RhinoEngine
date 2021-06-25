package com.outofbound.rhinoenginelib.shader.primitives;

import android.opengl.GLES30;

import com.outofbound.rhinoenginelib.shader.Shader;

import java.nio.FloatBuffer;

public final class BlurShader extends Shader {

    private float[] vpMatrix;
    private FloatBuffer vertices;
    private FloatBuffer textureCoords;
    private float scale;
    private float amount;
    private float strength;
    private int texture;
    private float screenWidth;
    private float screenHeight;
    private int type;
    private final int aPosition;
    private final int aTexCoords;
    private final int uTextId;
    private final int uMVPMatrix;
    private final int uBlurScale;
    private final int uBlurAmount;
    private final int uBlurStrength;
    private final int uScreenSize;
    private final int uType;
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    public BlurShader() {
        super("vs_blur.glsl", "fs_blur.glsl");
        GLES30.glUseProgram(getProgram());
        aPosition = getAttrib("aPosition");
        aTexCoords = getAttrib("aTexCoords");
        uTextId = getUniform("uTextId");
        uMVPMatrix = getUniform("uMVPMatrix");
        uBlurScale = getUniform("uBlurScale");
        uBlurAmount = getUniform("uBlurAmount");
        uBlurStrength = getUniform("uBlurStrength");
        uScreenSize = getUniform("uScreenSize");
        uType = getUniform("uType");
    }

    @Override
    public void bindData() {
        GLES30.glUseProgram(getProgram());
        GLES30.glVertexAttribPointer(aPosition, 2, GLES30.GL_FLOAT, false, 0, vertices);
        GLES30.glEnableVertexAttribArray(aPosition);
        GLES30.glVertexAttribPointer(aTexCoords, 2, GLES30.GL_FLOAT, false, 0, textureCoords);
        GLES30.glEnableVertexAttribArray(aTexCoords);
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texture);
        GLES30.glUniform1i(uTextId, 0);
        GLES30.glUniformMatrix4fv(uMVPMatrix, 1, false, vpMatrix, 0);
        GLES30.glUniform1f(uBlurScale,scale);
        GLES30.glUniform1f(uBlurAmount,amount);
        GLES30.glUniform1f(uBlurStrength,strength);
        GLES30.glUniform2f(uScreenSize,screenWidth,screenHeight);
        GLES30.glUniform1i(uType,type);
    }

    @Override
    public void unbindData() {
        GLES30.glDisableVertexAttribArray(aPosition);
        GLES30.glDisableVertexAttribArray(aTexCoords);
    }

    public BlurShader setVpMatrix(float[] vpMatrix){
        this.vpMatrix = vpMatrix;
        return this;
    }

    public BlurShader setVertices(FloatBuffer vertices){
        this.vertices = vertices;
        return this;
    }

    public BlurShader setTextureCoords(FloatBuffer textureCoords){
        this.textureCoords = textureCoords;
        return this;
    }

    public BlurShader setScale(float scale){
        this.scale = scale;
        return this;
    }

    public BlurShader setAmount(float amount){
        this.amount = amount;
        return this;
    }

    public BlurShader setStrength(float strength){
        this.strength = strength;
        return this;
    }

    public BlurShader setTexture(int texture){
        this.texture = texture;
        return this;
    }

    public BlurShader setScreenSize(int screenWidth, int screenHeight){
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        return this;
    }

    public BlurShader setType(int type){
        this.type = type;
        return this;
    }
}
