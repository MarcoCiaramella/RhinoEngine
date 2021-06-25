package com.outofbound.rhinoenginelib.renderer;

import android.opengl.GLES30;

import com.outofbound.rhinoenginelib.camera.Camera;


public class RendererOnTexture {

    private final int fboWidth;
    private final int fboHeight;
    private int frameBuffer;
    private int texture;
    private final Camera camera;

    public static final int RESOLUTION_256 = 0;
    public static final int RESOLUTION_512 = 1;
    public static final int RESOLUTION_1024 = 2;
    public static final int RESOLUTION_2048 = 3;
    public static final int RESOLUTION_4096 = 4;

    private static final int[] resolutions = {256,512,1024,2048,4096};


    public RendererOnTexture(int resolution, Camera camera){
        if (resolution < 0 || resolution > 4){
            throw new IllegalArgumentException("resolution must be RendererOnTexture.RESOLUTION_x");
        }
        this.fboWidth = resolutions[resolution];
        this.fboHeight = resolutions[resolution];
        this.camera = camera;
        setup();
    }

    private void setup(){
        int[] buffers = new int[1];
        GLES30.glGenFramebuffers(1, buffers, 0);
        frameBuffer = buffers[0];
        GLES30.glGenTextures(1, buffers, 0);
        texture = buffers[0];
        GLES30.glGenRenderbuffers(1, buffers, 0);
        int renderBuffer = buffers[0];
        createFramebuffer(frameBuffer, texture, renderBuffer, fboWidth, fboHeight);
    }

    private void renderOnFBO(SceneRenderer sceneRenderer){
        GLES30.glViewport(0, 0, fboWidth, fboHeight);
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, frameBuffer);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
        sceneRenderer.doRendering(camera);
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
    }

    public int render(SceneRenderer sceneRenderer) {
        renderOnFBO(sceneRenderer);
        return texture;
    }

    private void createFramebuffer(int fbo, int tex, int rid, int fboWidth, int fboHeight){
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, fbo);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, tex);
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, fboWidth, fboHeight, 0, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, null);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
        GLES30.glBindRenderbuffer(GLES30.GL_RENDERBUFFER, rid);
        GLES30.glRenderbufferStorage(GLES30.GL_RENDERBUFFER, GLES30.GL_DEPTH_COMPONENT16, fboWidth, fboHeight);
        GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0, GLES30.GL_TEXTURE_2D, tex, 0);
        GLES30.glFramebufferRenderbuffer(GLES30.GL_FRAMEBUFFER, GLES30.GL_DEPTH_ATTACHMENT, GLES30.GL_RENDERBUFFER, rid);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
        GLES30.glBindRenderbuffer(GLES30.GL_RENDERBUFFER, 0);
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
    }

    public int getFboWidth(){
        return fboWidth;
    }

    public int getFboHeight(){
        return fboHeight;
    }

}
