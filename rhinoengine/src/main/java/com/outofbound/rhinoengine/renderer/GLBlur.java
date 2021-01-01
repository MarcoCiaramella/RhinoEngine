package com.outofbound.rhinoengine.renderer;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.outofbound.rhinoengine.camera.GLCamera;
import com.outofbound.rhinoengine.camera.GLCamera2D;
import com.outofbound.rhinoengine.engine.GLEngine;
import com.outofbound.rhinoengine.shader.GLShader;
import com.outofbound.rhinoengine.util.file.TextFileReader;

import java.nio.FloatBuffer;

public class GLBlur extends GLOnTextureRenderer {

    private int textureInput;

    private int frameBufferStep1;
    private int frameBufferStep2;
    private int frameBufferStep3;

    private int textureStep1;
    private int textureStep2;
    private int textureStep3;

    private int renderBufferStep1;
    private int renderBufferStep2;
    private int renderBufferStep3;

    private int programShaderBlur;
    private int uMVPMatrixBlur;
    private int aPositionBlur;
    private int aTextureBlur;
    private int uDirectionBlur;
    private int uBlurScaleBlur;
    private int uBlurAmountBlur;
    private int uBlurStrengthBlur;
    private int uTextureIdBlur;

    private int programShaderBlurRenderer;
    private int aPositionBlurRenderer;
    private int aTextureBlurRenderer;
    private int uTextureIdBlurRenderer;
    private int uTextureId1BlurRenderer;
    private int uMVPMatrixBlurRenderer;

    private float[] mvMatrix = new float[16];
    private float[] mvpMatrix = new float[16];
    private float scale;
    private float amount;
    private float strength;

    private GLCamera camera;


    public GLBlur(float scale, float amount, float strength) {
        this.scale = scale;
        this.amount = amount;
        this.strength = strength;
        this.camera = new GLCamera2D();
    }

    private void setupBlur(int fboWidth, int fboHeight){

        int[] buffers = new int[3];

        //generate fbo id
        GLES20.glGenFramebuffers(3, buffers, 0);
        frameBufferStep1 = buffers[0];
        frameBufferStep2 = buffers[1];
        frameBufferStep3 = buffers[2];
        GLES20.glGenTextures(3, buffers, 0);
        textureStep1 = buffers[0];
        textureStep2 = buffers[1];
        textureStep3 = buffers[2];
        GLES20.glGenRenderbuffers(3, buffers, 0);
        renderBufferStep1 = buffers[0];
        renderBufferStep2 = buffers[1];
        renderBufferStep3 = buffers[2];
        createFramebuffer(frameBufferStep1,textureStep1,renderBufferStep1,fboWidth,fboHeight);
        createFramebuffer(frameBufferStep2,textureStep2,renderBufferStep2,fboWidth,fboHeight);
        createFramebuffer(frameBufferStep3,textureStep3,renderBufferStep3,fboWidth,fboHeight);

        programShaderBlur = GLES20.glCreateProgram();
        //compile shaders
        GLES20.glAttachShader(programShaderBlur, GLShader.loadShader(GLES20.GL_FRAGMENT_SHADER,
                TextFileReader.getString(GLEngine.getInstance().getContext(), "fs_blur.glsl")) );
        GLES20.glAttachShader(programShaderBlur, GLShader.loadShader(GLES20.GL_VERTEX_SHADER,
                TextFileReader.getString(GLEngine.getInstance().getContext(), "vs_blur.glsl")) );
        GLES20.glLinkProgram(programShaderBlur);
        // Set our shader program
        GLES20.glUseProgram(programShaderBlur);
        uMVPMatrixBlur = GLES20.glGetUniformLocation(programShaderBlur, "uMVPMatrix");
        aPositionBlur = GLES20.glGetAttribLocation(programShaderBlur, "aPosition");
        aTextureBlur = GLES20.glGetAttribLocation(programShaderBlur, "aTexCoords");
        uDirectionBlur = GLES20.glGetUniformLocation(programShaderBlur, "direction");
        uBlurScaleBlur = GLES20.glGetUniformLocation(programShaderBlur, "blurScale");
        uBlurAmountBlur = GLES20.glGetUniformLocation(programShaderBlur, "blurAmount");
        uBlurStrengthBlur = GLES20.glGetUniformLocation(programShaderBlur, "blurStrength");
        uTextureIdBlur = GLES20.glGetUniformLocation(programShaderBlur, "u_texId");
    }

    private void setupBlurRenderer(){

        programShaderBlurRenderer = GLES20.glCreateProgram();

        //compile shaders
        GLES20.glAttachShader(programShaderBlurRenderer, GLShader.loadShader(GLES20.GL_FRAGMENT_SHADER,
                TextFileReader.getString(GLEngine.getInstance().getContext(), "fs_blur_renderer.glsl")) );
        GLES20.glAttachShader(programShaderBlurRenderer, GLShader.loadShader(GLES20.GL_VERTEX_SHADER,
                TextFileReader.getString(GLEngine.getInstance().getContext(), "vs_blur_renderer.glsl")) );
        GLES20.glLinkProgram(programShaderBlurRenderer);
        // Set our shader program
        GLES20.glUseProgram(programShaderBlurRenderer);

        aPositionBlurRenderer = GLES20.glGetAttribLocation(programShaderBlurRenderer, "a_position");
        aTextureBlurRenderer = GLES20.glGetAttribLocation(programShaderBlurRenderer, "a_texCoords");
        uTextureIdBlurRenderer = GLES20.glGetUniformLocation(programShaderBlurRenderer, "u_texId");
        uTextureId1BlurRenderer = GLES20.glGetUniformLocation(programShaderBlurRenderer, "u_texId1");
        uMVPMatrixBlurRenderer = GLES20.glGetUniformLocation(programShaderBlurRenderer, "u_mvpMatrix");
    }

    @Override
    public void setup(int fboWidth, int fboHeight){
        setupBlur(fboWidth, fboHeight);
        setupBlurRenderer();
    }

    @Override
    public int render(int textureInput, FloatBuffer vertexBuffer, FloatBuffer textureCoordsBuffer, long ms, int fboWidth, int fboHeight) {
        float[] m = camera.create(fboWidth, fboHeight, ms);
        this.textureInput = textureInput;
        // mFBO is used to render on texture.
        blur(1, m, vertexBuffer, textureCoordsBuffer);
        blur(2, m, vertexBuffer, textureCoordsBuffer);
        draw(m, vertexBuffer, textureCoordsBuffer);
        return textureStep3;
    }

    private void blur(int step, float[] m, FloatBuffer vertexBuffer, FloatBuffer textureCoordsBuffer) {
        //apply horizontal blur
        if (step == 1)
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBufferStep1);
        else if (step == 2)//apply vertical blur
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBufferStep2);

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT| GLES20.GL_DEPTH_BUFFER_BIT);

        GLES20.glVertexAttribPointer(aPositionBlur, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        GLES20.glEnableVertexAttribArray(aPositionBlur);

        GLES20.glVertexAttribPointer(aTextureBlur, 2, GLES20.GL_FLOAT, false, 0, textureCoordsBuffer);
        GLES20.glEnableVertexAttribArray(aTextureBlur);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        if (step == 1)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureInput);
        else if (step == 2)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureStep1);

        GLES20.glUseProgram(programShaderBlur);
        GLES20.glUniform1i(uTextureIdBlur, 0);
        Matrix.setIdentityM(mvMatrix, 0);
        Matrix.multiplyMM(mvpMatrix, 0, m, 0, mvMatrix, 0);
        GLES20.glUniformMatrix4fv(uMVPMatrixBlur, 1, false, mvpMatrix, 0);
        if (step == 1)
            GLES20.glUniform1i(uDirectionBlur, 0);
        else if (step == 2)
            GLES20.glUniform1i(uDirectionBlur, 1);
        GLES20.glUniform1f(uBlurScaleBlur,  scale);
        GLES20.glUniform1f(uBlurAmountBlur, amount);
        GLES20.glUniform1f(uBlurStrengthBlur, strength);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    private void draw(float[] m, FloatBuffer vertexBuffer, FloatBuffer textureCoordsBuffer){

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBufferStep3);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT| GLES20.GL_DEPTH_BUFFER_BIT);

        GLES20.glUseProgram(programShaderBlurRenderer);

        GLES20.glVertexAttribPointer(aPositionBlurRenderer, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        GLES20.glEnableVertexAttribArray(aPositionBlurRenderer);

        GLES20.glVertexAttribPointer(aTextureBlurRenderer, 2, GLES20.GL_FLOAT, false, 0, textureCoordsBuffer);
        GLES20.glEnableVertexAttribArray(aTextureBlurRenderer);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureInput);
        GLES20.glUniform1i(uTextureIdBlurRenderer, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureStep2);
        GLES20.glUniform1i(uTextureId1BlurRenderer, 1);

        Matrix.setIdentityM(mvMatrix, 0);
        Matrix.multiplyMM(mvpMatrix, 0, m, 0, mvMatrix, 0);
        GLES20.glUniformMatrix4fv(uMVPMatrixBlurRenderer, 1, false, mvpMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

    }

    private void createFramebuffer(int fbo, int tex, int rid, int fboWidth, int fboHeight){
        //Bind Frame buffer
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fbo);
        //Bind texture
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tex);
        //Define texture parameters
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, fboWidth, fboHeight, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);

        //Bind render buffer and define buffer dimension
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, rid);
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, fboWidth, fboHeight);
        //Attach texture FBO color attachment
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, tex, 0);
        //Attach render buffer to depth attachment
        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, rid);
        //we are done, reset
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

}
