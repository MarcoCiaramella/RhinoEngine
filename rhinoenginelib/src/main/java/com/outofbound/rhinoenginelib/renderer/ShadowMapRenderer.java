package com.outofbound.rhinoenginelib.renderer;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.outofbound.rhinoenginelib.camera.Camera;
import com.outofbound.rhinoenginelib.mesh.Mesh;
import com.outofbound.rhinoenginelib.shader.ShadowMapShader;
import com.outofbound.rhinoenginelib.util.map.SyncMap;

public class ShadowMapRenderer extends AbstractRenderer {

    private final ShadowMapShader shadowMapShader;
    private final float[] mvpMatrix = new float[16];
    private final SyncMap<Mesh> meshMap;

    public ShadowMapRenderer(SyncMap<Mesh> meshMap){
        this.meshMap = meshMap;
        shadowMapShader = new ShadowMapShader();
    }

    @Override
    public void doRendering(Camera camera, long ms) {
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_FRONT);
        camera.loadVpMatrix();
        for (String name : meshMap.keySet()) {
            Mesh mesh = meshMap.get(name);
            if (mesh == null) {
                meshMap.remove(name);
                continue;
            }
            for (Mesh.ShaderData shaderData : mesh.getShaderData()) {
                Matrix.multiplyMM(mvpMatrix, 0, camera.getVpMatrix(), 0, mesh.getMMatrix(), 0);
                shadowMapShader.setData(shaderData);
                shadowMapShader.setMvpMatrix(mvpMatrix);
                shadowMapShader.bindData();
                draw(shaderData);
                shadowMapShader.unbindData();
            }
        }
        GLES20.glDisable(GLES20.GL_CULL_FACE);
    }
}
