package com.outofbound.rhinoenginelib.renderer;

import android.opengl.GLES20;


public class GLRendererOnTexture {

    // Make sure that dimensions are in POT (Power Of Two), because some devices may not support NPOT textures.
    private final int fboWidth;
    private final int fboHeight;
    private int frameBuffer;
    private int texture;

    public static final int RESOLUTION_256 = 0;
    public static final int RESOLUTION_512 = 1;
    public static final int RESOLUTION_1024 = 2;
    public static final int RESOLUTION_2048 = 3;
    public static final int RESOLUTION_4096 = 4;

    private static final int[] resolutions = {256,512,1024,2048,4096};


    public GLRendererOnTexture(int resolution){
        this.fboWidth = resolutions[resolution];
        this.fboHeight = resolutions[resolution];
        setup();
    }

    private void setup(){
        int[] buffers = new int[1];
        GLES20.glGenFramebuffers(1, buffers, 0);
        frameBuffer = buffers[0];
        GLES20.glGenTextures(1, buffers, 0);
        texture = buffers[0];
        GLES20.glGenRenderbuffers(1, buffers, 0);
        int renderBuffer = buffers[0];
        createFramebuffer(frameBuffer, texture, renderBuffer, fboWidth, fboHeight);
    }

    private void renderOnFBO(GLSceneRenderer glSceneRenderer){
        GLES20.glViewport(0, 0, fboWidth, fboHeight);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        glSceneRenderer.doRendering();
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    public int render(GLSceneRenderer glSceneRenderer) {
        renderOnFBO(glSceneRenderer);
        return texture;
    }

    private void createFramebuffer(int fbo, int tex, int rid, int fboWidth, int fboHeight){
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fbo);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, tex);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, fboWidth, fboHeight, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, rid);
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, fboWidth, fboHeight);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, tex, 0);
        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, rid);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    public int getFboWidth(){
        return fboWidth;
    }

    public int getFboHeight(){
        return fboHeight;
    }

}
