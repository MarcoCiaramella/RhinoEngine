package com.outofbound.rhinoenginelib.renderer.fx;

import android.opengl.GLES30;

import com.outofbound.rhinoenginelib.camera.Camera;
import com.outofbound.rhinoenginelib.camera.CameraOrthographic;
import com.outofbound.rhinoenginelib.renderer.RendererOnTexture;
import com.outofbound.rhinoenginelib.renderer.SceneRenderer;
import com.outofbound.rhinoenginelib.shader.primitives.BlurShader;
import com.outofbound.rhinoenginelib.shader.primitives.BlurFinalShader;
import com.outofbound.rhinoenginelib.util.vector.Vector3f;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Blur {

    private final int frameBufferStep1;
    private final int frameBufferStep2;
    private int textureInput;
    private final int textureStep1;
    private final int textureStep2;
    private final Camera camera;

    private static final float[] vertices = {
            -1f, -1f,
            1f, -1f,
            -1f, 1f,
            1f, 1f
    };

    private static final float[] textureCoords = {
            1, 1,
            0, 1,
            1, 0,
            0, 0
    };

    private final FloatBuffer verticesBuffer;
    private final FloatBuffer textureCoordsBuffer;
    private final RendererOnTexture rendererOnTexture;
    private final BlurShader blurShader;
    private final BlurFinalShader blurFinalShader;
    private final float scale;
    private final float amount;
    private final float strength;


    public Blur(RendererOnTexture rendererOnTexture, float scale, float amount, float strength, float near, float far) {
        this.rendererOnTexture = rendererOnTexture;
        this.scale = scale;
        this.amount = amount;
        this.strength = strength;
        this.camera = new CameraOrthographic(new Vector3f(0,0,1),new Vector3f(0,0,0),new Vector3f(0,-1,0),near,far,1);
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
        GLES30.glGenFramebuffers(3, buffers, 0);
        frameBufferStep1 = buffers[0];
        frameBufferStep2 = buffers[1];
        GLES30.glGenTextures(3, buffers, 0);
        textureStep1 = buffers[0];
        textureStep2 = buffers[1];
        GLES30.glGenRenderbuffers(3, buffers, 0);
        int renderBufferStep1 = buffers[0];
        int renderBufferStep2 = buffers[1];
        createFramebuffer(frameBufferStep1,textureStep1, renderBufferStep1, rendererOnTexture.getFboWidth(), rendererOnTexture.getFboHeight());
        createFramebuffer(frameBufferStep2,textureStep2, renderBufferStep2, rendererOnTexture.getFboWidth(), rendererOnTexture.getFboHeight());

        blurShader = new BlurShader();
        blurFinalShader = new BlurFinalShader();
    }

    public void render(SceneRenderer sceneRenderer, int screenWidth, int screenHeight) {
        camera.setWidth(rendererOnTexture.getFboWidth()).setHeight(rendererOnTexture.getFboHeight());
        camera.loadVpMatrix();
        this.textureInput = rendererOnTexture.render(sceneRenderer);
        blur(screenWidth,screenHeight);
        renderOnScreen(screenWidth,screenHeight);
    }

    private void blur(int screenWidth, int screenHeight){
        blurShader.setVertices(verticesBuffer);
        blurShader.setTextureCoords(textureCoordsBuffer);
        blurShader.setVpMatrix(camera.getVpMatrix());
        blurShader.setAmount(amount);
        blurShader.setScale(scale);
        blurShader.setStrength(strength);
        blurShader.setScreenSize(screenWidth,screenHeight);
        blurHorizontal();
        blurVertical();
    }

    private void blurHorizontal() {
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, frameBufferStep1);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
        blurShader.setTexture(textureInput);
        blurShader.setType(BlurShader.HORIZONTAL);
        blurShader.bindData();
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4);
        blurShader.unbindData();
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
    }

    private void blurVertical() {
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, frameBufferStep2);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
        blurShader.setTexture(textureStep1);
        blurShader.setType(BlurShader.VERTICAL);
        blurShader.bindData();
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4);
        blurShader.unbindData();
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
    }

    public void renderOnScreen(int screenWidth, int screenHeight){
        GLES30.glViewport(0, 0, screenWidth, screenHeight);
        blurFinalShader.setVpMatrix(camera.getVpMatrix());
        blurFinalShader.setVertices(verticesBuffer);
        blurFinalShader.setTextureCoords(textureCoordsBuffer);
        blurFinalShader.setTexture1(textureInput);
        blurFinalShader.setTexture2(textureStep2);
        blurFinalShader.bindData();
        GLES30.glDrawArrays(GLES30.GL_TRIANGLE_STRIP, 0, 4);
        blurFinalShader.unbindData();
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

}
