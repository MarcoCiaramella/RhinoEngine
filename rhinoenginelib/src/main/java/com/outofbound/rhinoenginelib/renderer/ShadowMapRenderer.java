package com.outofbound.rhinoenginelib.renderer;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.outofbound.rhinoenginelib.camera.Camera;
import com.outofbound.rhinoenginelib.mesh.Mesh;
import com.outofbound.rhinoenginelib.shader.primitives.ShadowMapShader;
import com.outofbound.rhinoenginelib.util.list.BigList;

public class ShadowMapRenderer extends AbstractRenderer {

    private final ShadowMapShader shadowMapShader;
    private final float[] shadowMVPMatrix = new float[16];
    private final BigList<Mesh> meshes;

    public ShadowMapRenderer(BigList<Mesh> meshes){
        this.meshes = meshes;
        shadowMapShader = new ShadowMapShader();
    }

    @Override
    public void doRendering(int screenWidth, int screenHeight, Camera camera, long ms) {
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glCullFace(GLES20.GL_FRONT);
        camera.loadVpMatrix();
        for (Mesh mesh : meshes) {
            Matrix.multiplyMM(shadowMVPMatrix, 0, camera.getVpMatrix(), 0, mesh.getMMatrix(), 0);
            shadowMapShader.setMesh(mesh);
            shadowMapShader.setMvpMatrix(shadowMVPMatrix);
            shadowMapShader.bindData();
            draw(mesh);
            shadowMapShader.unbindData();
        }
        GLES20.glDisable(GLES20.GL_CULL_FACE);
    }
}
