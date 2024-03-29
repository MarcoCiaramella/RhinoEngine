package com.outofbound.rhinoenginelib.renderer;

import android.opengl.GLES20;

import com.outofbound.rhinoenginelib.camera.Camera;
import com.outofbound.rhinoenginelib.shader.BlurShader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class BlurRenderer extends AbstractRenderer {

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
    private final BlurShader blurShader;
    private final AbstractRenderer abstractRenderer;
    private final RendererOnTexture rendererOnTexture;


    public BlurRenderer(AbstractRenderer abstractRenderer) {
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
        this.abstractRenderer = abstractRenderer;
        rendererOnTexture = new RendererOnTexture(RenderingResolution.RESOLUTION_1024);
    }

    @Override
    public void doRendering(Camera camera, long ms) {
        blurShader.setVertices(verticesBuffer);
        blurShader.setTextureCoords(textureCoordsBuffer);
        blurShader.setTexture(rendererOnTexture.render(abstractRenderer, camera, ms));
        blurShader.bindData();
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        blurShader.unbindData();
    }
}
