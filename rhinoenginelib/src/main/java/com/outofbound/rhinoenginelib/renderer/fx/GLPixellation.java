package com.outofbound.rhinoenginelib.renderer.fx;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.outofbound.rhinoenginelib.camera.GLCamera;
import com.outofbound.rhinoenginelib.camera.GLCamera2D;
import com.outofbound.rhinoenginelib.engine.GLEngine;
import com.outofbound.rhinoenginelib.renderer.GLOnTextureRenderer;
import com.outofbound.rhinoenginelib.shader.GLShader;
import com.outofbound.rhinoenginelib.util.file.TextFileReader;

import java.nio.FloatBuffer;

public class GLPixellation extends GLOnTextureRenderer {

    private int textureInput;
    private int frameBuffer;
    private int texture;
    private int programShader;
    private int uMVPMatrix;
    private int aPosition;
    private int aTexture;
    private int uTextureId;
    private float[] mvMatrix = new float[16];
    private float[] mvpMatrix = new float[16];
    private float pixelWidth;
    private float pixelHeight;
    private int uPixelWidth;
    private int uPixelHeight;
    private int uFBOWidth;
    private int uFBOHeight;
    private int fboWidth;
    private int fboHeight;
    private float time;
    private float timeToLiveS;
    private int uTime;
    private GLCamera camera;


    public GLPixellation(float pixelWidth, float pixelHeight, float timeToLiveS) {
        this.pixelWidth = pixelWidth;
        this.pixelHeight = pixelHeight;
        time = 0.0f;
        this.timeToLiveS = timeToLiveS;
        this.camera = new GLCamera2D();
    }

    @Override
    public void setup(int fboWidth, int fboHeight){

        this.fboWidth = fboWidth;
        this.fboHeight = fboHeight;

        int[] buffers = new int[1];

        //generate fbo id
        GLES20.glGenFramebuffers(1, buffers, 0);
        frameBuffer = buffers[0];
        GLES20.glGenTextures(1, buffers, 0);
        texture = buffers[0];
        GLES20.glGenRenderbuffers(1, buffers, 0);
        int renderBuffer = buffers[0];
        createFramebuffer(frameBuffer,texture,renderBuffer,fboWidth,fboHeight);

        programShader = GLES20.glCreateProgram();
        //compile shaders
        GLES20.glAttachShader(programShader, GLShader.loadShader(GLES20.GL_FRAGMENT_SHADER,
                TextFileReader.getString(GLEngine.getInstance().getContext(), "fs_pixellation.glsl")) );
        GLES20.glAttachShader(programShader, GLShader.loadShader(GLES20.GL_VERTEX_SHADER,
                TextFileReader.getString(GLEngine.getInstance().getContext(), "vs_pixellation.glsl")) );
        GLES20.glLinkProgram(programShader);
        // Set our shader program
        GLES20.glUseProgram(programShader);
        uMVPMatrix = GLES20.glGetUniformLocation(programShader, "uMVPMatrix");
        aPosition = GLES20.glGetAttribLocation(programShader, "aPosition");
        aTexture = GLES20.glGetAttribLocation(programShader, "aTexCoords");
        uTextureId = GLES20.glGetUniformLocation(programShader, "u_texId");
        uPixelWidth = GLES20.glGetUniformLocation(programShader, "u_pixel_w");
        uPixelHeight = GLES20.glGetUniformLocation(programShader, "u_pixel_h");
        uFBOWidth = GLES20.glGetUniformLocation(programShader, "u_rt_w");
        uFBOHeight = GLES20.glGetUniformLocation(programShader, "u_rt_h");
        uTime = GLES20.glGetUniformLocation(programShader, "uTime");
    }

    @Override
    public int render(int textureInput, FloatBuffer vertexBuffer, FloatBuffer textureCoordsBuffer, long ms, int fboWidth, int fboHeight) {
        float[] m = camera.create(fboWidth, fboHeight, ms);
        this.textureInput = textureInput;
        // mFBO is used to render on texture.
        pixellation(m,vertexBuffer,textureCoordsBuffer,ms);
        if (time > timeToLiveS){
            destroy();
        }
        return texture;
    }

    private void pixellation(float[] m, FloatBuffer vertexBuffer, FloatBuffer textureCoordsBuffer, long ms) {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT| GLES20.GL_DEPTH_BUFFER_BIT);
        GLES20.glVertexAttribPointer(aPosition, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        GLES20.glEnableVertexAttribArray(aPosition);
        GLES20.glVertexAttribPointer(aTexture, 2, GLES20.GL_FLOAT, false, 0, textureCoordsBuffer);
        GLES20.glEnableVertexAttribArray(aTexture);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureInput);
        GLES20.glUseProgram(programShader);
        GLES20.glUniform1i(uTextureId, 0);
        Matrix.setIdentityM(mvMatrix, 0);
        Matrix.multiplyMM(mvpMatrix, 0, m, 0, mvMatrix, 0);
        GLES20.glUniformMatrix4fv(uMVPMatrix, 1, false, mvpMatrix, 0);
        GLES20.glUniform1f(uPixelWidth,  pixelWidth);
        GLES20.glUniform1f(uPixelHeight, pixelHeight);
        GLES20.glUniform1f(uFBOWidth,  fboWidth);
        GLES20.glUniform1f(uFBOHeight, fboHeight);
        time += ms/1000f;
        GLES20.glUniform1f(uTime, time);
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
