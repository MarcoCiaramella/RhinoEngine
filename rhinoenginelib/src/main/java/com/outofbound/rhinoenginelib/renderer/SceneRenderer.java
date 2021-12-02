package com.outofbound.rhinoenginelib.renderer;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.outofbound.rhinoenginelib.camera.Camera;
import com.outofbound.rhinoenginelib.light.Lights;
import com.outofbound.rhinoenginelib.mesh.Mesh;
import com.outofbound.rhinoenginelib.shader.primitives.SceneShader;
import com.outofbound.rhinoenginelib.util.list.BigList;


public final class SceneRenderer extends AbstractRenderer {

    private final SceneShader sceneShader;
    private final BigList<Mesh> meshes;
    private final float[] mMatrix = new float[16];
    private final float[] mvpMatrix = new float[16];
    private Lights lights;
    private boolean blendingEnabled = false;
    private final ShadowMapRenderer shadowMapRenderer;

    public SceneRenderer(){
        sceneShader = new SceneShader();
        meshes = new BigList<>();
        lights = new Lights();
        shadowMapRenderer = new ShadowMapRenderer(meshes);
    }

    public int addMesh(Mesh mesh){
        mesh.onAdd();
        return meshes.add(mesh);
    }

    public SceneRenderer removeMesh(int id){
        meshes.remove(id);
        return this;
    }

    public Mesh getMesh(int id){
        return meshes.get(id);
    }

    public SceneRenderer setLights(Lights lights){
        this.lights = lights;
        return this;
    }

    public SceneRenderer enableBlending(){
        blendingEnabled = true;
        return this;
    }

    public SceneRenderer disableBlending(){
        blendingEnabled = false;
        return this;
    }

    public Lights getLights(){
        return lights;
    }

    @Override
    public void doRendering(int screenWidth, int screenHeight, Camera camera, long ms) {
        camera.loadVpMatrix();
        sceneShader.setLights(lights);
        sceneShader.setViewPos(camera.getEye());
        if (lights.getDirLight().isOn() && lights.getDirLight().isShadowEnabled()){
            lights.getDirLight().getShadowMap().render(shadowMapRenderer, screenWidth, screenHeight, ms);
        }
        if (lights.getPointLight().isOn() && lights.getPointLight().isShadowEnabled()) {
            lights.getPointLight().getShadowMap().render(shadowMapRenderer, screenWidth, screenHeight, ms);
        }
        if (blendingEnabled) {
            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        }
        for (Mesh mesh : meshes) {
            if (!mesh.isDead(ms)) {
                Matrix.setIdentityM(mMatrix, 0);
                mesh.doTransformation(mMatrix,ms);
                Matrix.multiplyMM(mvpMatrix, 0, camera.getVpMatrix(), 0, mMatrix, 0);
                if (mesh.getBoundingBox() != null) {
                    mesh.getBoundingBox().copyMMatrix(mMatrix);
                }
                sceneShader.setMesh(mesh);
                sceneShader.setMMatrix(mMatrix);
                sceneShader.setMvpMatrix(mvpMatrix);
                sceneShader.bindData();
                draw(mesh);
                sceneShader.unbindData();
            }
            else {
                meshes.remove(mesh);
            }
        }
        if (blendingEnabled) {
            GLES20.glDisable(GLES20.GL_BLEND);
        }
    }
}
