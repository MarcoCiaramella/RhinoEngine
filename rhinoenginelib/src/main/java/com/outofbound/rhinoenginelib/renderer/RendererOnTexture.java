package com.outofbound.rhinoenginelib.renderer;

import android.opengl.GLES20;

import com.outofbound.rhinoenginelib.camera.Camera;
import com.outofbound.rhinoenginelib.engine.AbstractEngine;


public class RendererOnTexture {

    private final int fboWidth;
    private final int fboHeight;
    private int frameBuffer;
    private int texture;



    public RendererOnTexture(RenderingResolution resolution){
        int res = 0;
        switch (resolution) {
            case RESOLUTION_256: res = 256; break;
            case RESOLUTION_512: res = 512; break;
            case RESOLUTION_1024: res = 1024; break;
            case RESOLUTION_2048: res = 2048; break;
            case RESOLUTION_4096: res = 4096; break;
        }
        this.fboWidth = res;
        this.fboHeight = res;
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

    private void renderOnFBO(AbstractRenderer abstractRenderer, Camera camera, long ms){
        GLES20.glViewport(0, 0, fboWidth, fboHeight);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        abstractRenderer.doRendering(camera, ms);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        GLES20.glViewport(0, 0, AbstractEngine.getInstance().getWidth(), AbstractEngine.getInstance().getHeight());
    }

    private void createFramebuffer(int frameBuffer, int texture, int renderBuffer, int fboWidth, int fboHeight){
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffer);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, fboWidth, fboHeight, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, renderBuffer);
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, fboWidth, fboHeight);
        GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, texture, 0);
        GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, renderBuffer);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    public int render(AbstractRenderer abstractRenderer, Camera camera, long ms) {
        renderOnFBO(abstractRenderer, camera, ms);
        return texture;
    }

}
