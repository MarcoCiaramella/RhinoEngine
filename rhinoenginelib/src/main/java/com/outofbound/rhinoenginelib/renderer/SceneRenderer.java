package com.outofbound.rhinoenginelib.renderer;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.outofbound.rhinoenginelib.camera.Camera;
import com.outofbound.rhinoenginelib.light.Lights;
import com.outofbound.rhinoenginelib.mesh.Mesh;
import com.outofbound.rhinoenginelib.shader.primitives.SceneShader;
import com.outofbound.rhinoenginelib.util.map.SyncMap;


public final class SceneRenderer extends AbstractRenderer {

    private final SceneShader sceneShader;
    private final float[] mvpMatrix = new float[16];
    private Lights lights;
    private boolean blendingEnabled = false;
    private final ShadowMapRenderer shadowMapRenderer;
    private final SyncMap<Mesh> meshMap;

    public SceneRenderer(){
        sceneShader = new SceneShader();
        meshMap = new SyncMap<>();
        lights = new Lights();
        shadowMapRenderer = new ShadowMapRenderer(meshMap);
    }

    public void addMesh(Mesh mesh){
        meshMap.put(mesh.getName(), mesh);
    }

    public void removeMesh(String name){
        meshMap.remove(name);
    }

    public Mesh getMesh(String name){
        return meshMap.get(name);
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
        for (String name : meshMap.keySet()) {
            Mesh mesh = getMesh(name).loadMMatrix();
            mesh.beforeRendering(ms);
            Matrix.multiplyMM(mvpMatrix, 0, camera.getVpMatrix(), 0, mesh.getMMatrix(), 0);
            sceneShader.setMesh(mesh);
            sceneShader.setMMatrix(mesh.getMMatrix());
            sceneShader.setMvpMatrix(mvpMatrix);
            sceneShader.bindData();
            draw(mesh);
            sceneShader.unbindData();
            mesh.afterRendering(ms);
        }
        if (blendingEnabled) {
            GLES20.glDisable(GLES20.GL_BLEND);
        }
    }
}
