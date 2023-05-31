package com.outofbound.rhinoenginelib.renderer;

import android.opengl.GLES20;

import com.outofbound.rhinoenginelib.camera.Camera;
import com.outofbound.rhinoenginelib.light.Lights;
import com.outofbound.rhinoenginelib.mesh.Mesh;
import com.outofbound.rhinoenginelib.shader.BlurShader;
import com.outofbound.rhinoenginelib.shader.FrameShader;
import com.outofbound.rhinoenginelib.util.map.SyncMap;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class FrameRenderer extends AbstractRenderer {

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
    private final FrameShader frameShader;
    private final BlurRenderer blurRenderer;
    private boolean blurEnabled = false;
    private final SceneRenderer sceneRenderer;


    public FrameRenderer(int resolution) {
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
        frameShader = new FrameShader();
        rendererOnTexture = new RendererOnTexture(resolution);
        sceneRenderer = new SceneRenderer();
        blurRenderer = new BlurRenderer(sceneRenderer);
    }

    @Override
    public void doRendering(Camera camera, long ms) {
        frameShader.setVertices(verticesBuffer);
        frameShader.setTextureCoords(textureCoordsBuffer);
        frameShader.setTexture(rendererOnTexture.render(blurEnabled ? blurRenderer : sceneRenderer, camera, ms));
        frameShader.bindData();
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        frameShader.unbindData();
    }

    public SyncMap<Mesh> getMeshes() {
        return sceneRenderer.getMeshes();
    }

    public void enableBlur() {
        blurEnabled = true;
    }

    public void disableBlur() {
        blurEnabled = false;
    }

    public void addMesh(Mesh mesh) {
        sceneRenderer.addMesh(mesh);
    }

    public void removeMesh(String name) {
        sceneRenderer.removeMesh(name);
    }

    public Mesh getMesh(String name) {
        return sceneRenderer.getMesh(name);
    }

    public Lights getLights() {
        return sceneRenderer.getLights();
    }
}
