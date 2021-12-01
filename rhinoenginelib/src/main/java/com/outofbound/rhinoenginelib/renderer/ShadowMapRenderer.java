package com.outofbound.rhinoenginelib.renderer;

import android.opengl.Matrix;

import com.outofbound.rhinoenginelib.camera.Camera;
import com.outofbound.rhinoenginelib.mesh.Mesh;
import com.outofbound.rhinoenginelib.shader.primitives.ShadowMapShader;
import com.outofbound.rhinoenginelib.util.list.BigList;

public class ShadowMapRenderer extends AbstractRenderer {

    private final ShadowMapShader shadowMapShader;
    private final float[] shadowMVPMatrix = new float[16];
    private final float[] mMatrix = new float[16];
    private final BigList<Mesh> meshes;

    public ShadowMapRenderer(BigList<Mesh> meshes){
        this.meshes = meshes;
        shadowMapShader = new ShadowMapShader();
    }

    @Override
    public void doRendering(int screenWidth, int screenHeight, Camera camera, long ms) {
        for (Mesh mesh : meshes) {
            if (!mesh.isDead(ms)) {
                Matrix.setIdentityM(mMatrix, 0);
                mesh.doTransformation(mMatrix,ms);
                Matrix.multiplyMM(shadowMVPMatrix, 0, camera.getVpMatrix(), 0, mMatrix, 0);
                if (mesh.getBoundingBox() != null) {
                    mesh.getBoundingBox().copyMMatrix(mMatrix);
                }
                shadowMapShader.setMesh(mesh);
                shadowMapShader.setMvpMatrix(shadowMVPMatrix);
                shadowMapShader.bindData();
                draw(mesh);
                shadowMapShader.unbindData();
            }
            else {
                meshes.remove(mesh);
            }
        }
    }
}
