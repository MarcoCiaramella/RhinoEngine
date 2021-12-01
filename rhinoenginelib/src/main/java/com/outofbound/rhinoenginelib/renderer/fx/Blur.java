package com.outofbound.rhinoenginelib.renderer.fx;

import android.opengl.GLES20;

import com.outofbound.rhinoenginelib.camera.Camera;
import com.outofbound.rhinoenginelib.renderer.RendererOnTexture;
import com.outofbound.rhinoenginelib.renderer.AbstractRenderer;
import com.outofbound.rhinoenginelib.shader.primitives.BlurShader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Blur {

    private static final float[] vertices = {
            -1f, -1f,
            1f, -1f,
            -1f, 1f,
            1f, 1f
    };

    private static final float[] textureCoords = {
            0, 0,
            1, 0,
            0, 1,
            1, 1
    };

    private final FloatBuffer verticesBuffer;
    private final FloatBuffer textureCoordsBuffer;
    private final RendererOnTexture rendererOnTexture;
    private final BlurShader blurShader;


    public Blur(RendererOnTexture rendererOnTexture) {
        this.rendererOnTexture = rendererOnTexture;
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
        blurShader = new BlurShader();
    }

    public void render(AbstractRenderer abstractRenderer, int screenWidth, int screenHeight, Camera camera, long ms) {
        blurShader.setVertices(verticesBuffer);
        blurShader.setTextureCoords(textureCoordsBuffer);
        blurShader.setSceneTexture(rendererOnTexture.render(abstractRenderer, screenWidth, screenHeight, camera, ms));
        // TODO renderizzare il blur su una texture piccola per velocizzare
        blurShader.bindData();
        GLES20.glViewport(0, 0, screenWidth, screenHeight);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        blurShader.unbindData();
    }

}
