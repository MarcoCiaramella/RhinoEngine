package com.outofbound.rhinoenginelib.renderer.fx;

import android.opengl.GLES20;

import com.outofbound.rhinoenginelib.camera.GLCamera;
import com.outofbound.rhinoenginelib.camera.GLCamera2D;
import com.outofbound.rhinoenginelib.renderer.GLRendererOnTexture;
import com.outofbound.rhinoenginelib.renderer.GLSceneRenderer;
import com.outofbound.rhinoenginelib.shader.primitives.BlurHorizontalShader;
import com.outofbound.rhinoenginelib.shader.primitives.BlurFinalShader;
import com.outofbound.rhinoenginelib.shader.primitives.BlurVerticalShader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class GLBlur {

    private final int frameBufferStep1;
    private final int frameBufferStep2;
    private int textureInput;
    private final int textureStep1;
    private final int textureStep2;
    private final GLCamera camera;

    private static final float[] vertices = {
            -1f, -1f, //bottom - left
            1f, -1f, //bottom - right
            -1f, 1f, //top - left
            1f, 1f //top - right
    };

    private static final float[] textureCoords = {
            1, 1, //bottom - left
            0, 1, // bottom - right
            1, 0, // top - left
            0, 0 // top - right
    };

    private final FloatBuffer verticesBuffer;
    private final FloatBuffer textureCoordsBuffer;
    private final GLRendererOnTexture glRendererOnTexture;
    private final BlurHorizontalShader blurHorizontalShader;
    private final BlurVerticalShader blurVerticalShader;
    private final BlurFinalShader blurFinalShader;
    private final float scale;
    private final float amount;
    private final float strength;


    public GLBlur(GLRendererOnTexture glRendererOnTexture, float scale, float amount, float strength, float near, float far) {
        this.glRendererOnTexture = glRendererOnTexture;
        this.scale = scale;
        this.amount = amount;
        this.strength = strength;
        this.camera = new GLCamera2D(near,far);
        ByteBuffer bb_vertex = ByteBuffer.allocateDirect(vertices.length * 4);
        bb_vertex.order(ByteOrder.nativeOrder());
        verticesBuffer = bb_vertex.asFloatBuffer();
        verticesBuffer.put(vertices);
        verticesBuffer.position(0);
        ByteBuffer bb_textCoords = ByteBuffer.allocateDirect(textureCoords.length * 4);
        bb_textCoords.order(ByteOrder.nativeOrder());
        textureCoordsBuffer = bb_textCoords.asFloatBuffer();
        textureCoordsBuffer.put(textureCoords);
        textureCoordsBuffer.position(0);

        int[] buffers = new int[3];
        GLES20.glGenFramebuffers(3, buffers, 0);
        frameBufferStep1 = buffers[0];
        frameBufferStep2 = buffers[1];
        GLES20.glGenTextures(3, buffers, 0);
        textureStep1 = buffers[0];
        textureStep2 = buffers[1];
        GLES20.glGenRenderbuffers(3, buffers, 0);
        int renderBufferStep1 = buffers[0];
        int renderBufferStep2 = buffers[1];
        createFramebuffer(frameBufferStep1,textureStep1, renderBufferStep1,glRendererOnTexture.getFboWidth(),glRendererOnTexture.getFboHeight());
        createFramebuffer(frameBufferStep2,textureStep2, renderBufferStep2,glRendererOnTexture.getFboWidth(),glRendererOnTexture.getFboHeight());

        blurHorizontalShader = new BlurHorizontalShader();
        blurVerticalShader = new BlurVerticalShader();
        blurFinalShader = new BlurFinalShader();
    }

    public void render(GLSceneRenderer glSceneRenderer, int screenWidth, int screenHeight, long ms) {
        camera.loadVpMatrix(glRendererOnTexture.getFboWidth(), glRendererOnTexture.getFboHeight(), ms);
        float[] vpMatrix = camera.getVpMatrix();
        this.textureInput = glRendererOnTexture.render(glSceneRenderer);
        blurHorizontal(vpMatrix, verticesBuffer, textureCoordsBuffer);
        blurVertical(vpMatrix, verticesBuffer, textureCoordsBuffer);
        renderOnScreen(glRendererOnTexture.getFboWidth(),glRendererOnTexture.getFboHeight(),screenWidth,screenHeight,ms);
    }

    private void blurHorizontal(float[] vpMatrix, FloatBuffer verticesBuffer, FloatBuffer textureCoordsBuffer) {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBufferStep1);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        blurHorizontalShader.setVertices(verticesBuffer);
        blurHorizontalShader.setTextureCoords(textureCoordsBuffer);
        blurHorizontalShader.setVpMatrix(vpMatrix);
        blurHorizontalShader.setTexture(textureInput);
        blurHorizontalShader.setAmount(amount);
        blurHorizontalShader.setScale(scale);
        blurHorizontalShader.setStrength(strength);
        blurHorizontalShader.bindData();
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        blurHorizontalShader.unbindData();
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    private void blurVertical(float[] vpMatrix, FloatBuffer verticesBuffer, FloatBuffer textureCoordsBuffer) {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBufferStep2);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        blurVerticalShader.setVertices(verticesBuffer);
        blurVerticalShader.setTextureCoords(textureCoordsBuffer);
        blurVerticalShader.setVpMatrix(vpMatrix);
        blurVerticalShader.setTexture(textureStep1);
        blurVerticalShader.setAmount(amount);
        blurVerticalShader.setScale(scale);
        blurVerticalShader.setStrength(strength);
        blurVerticalShader.bindData();
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        blurVerticalShader.unbindData();
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    public void renderOnScreen(int fboWidth, int fboHeight, int screenWidth, int screenHeight, long ms){
        GLES20.glViewport(0, 0, screenWidth, screenHeight);
        camera.loadVpMatrix(fboWidth, fboHeight, ms);
        blurFinalShader.setVpMatrix(camera.getVpMatrix());
        blurFinalShader.setVertices(verticesBuffer);
        blurFinalShader.setTextureCoords(textureCoordsBuffer);
        blurFinalShader.setTexture1(textureInput);
        blurFinalShader.setTexture2(textureStep2);
        blurFinalShader.bindData();
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        blurFinalShader.unbindData();
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

}
