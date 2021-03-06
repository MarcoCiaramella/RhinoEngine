package com.outofbound.rhinoenginelib.renderer;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.outofbound.rhinoenginelib.camera.Camera;
import com.outofbound.rhinoenginelib.light.Lights;
import com.outofbound.rhinoenginelib.light.PointLight;
import com.outofbound.rhinoenginelib.mesh.Mesh;
import com.outofbound.rhinoenginelib.shader.primitives.SceneShader;
import com.outofbound.rhinoenginelib.shader.primitives.ShadowMapShader;
import com.outofbound.rhinoenginelib.util.list.BigList;


/**
 * The OpenGL renderer.
 */
public final class Renderer {

    private final SceneShader sceneShader;
    private final BigList<Mesh> meshes;
    private final float[] mMatrix = new float[16];
    private final float[] mvpMatrix = new float[16];
    private Lights lights;
    private boolean blendingEnabled = false;
    private final SceneRenderer sceneRenderer;
    private long ms;
    private final ShadowMapShader shadowMapShader;
    private final float[] shadowMVPMatrix = new float[16];


    /**
     * The renderer constructor.
     */
    public Renderer(){
        sceneShader = new SceneShader();
        shadowMapShader = new ShadowMapShader();
        meshes = new BigList<>();
        lights = new Lights();
        sceneRenderer = this::renderShadowMap;
    }

    /**
     * Add a Mesh.
     * @param mesh the Mesh to add.
     * @return the Mesh id, -1 if Mesh is a duplicate.
     */
    public int addMesh(Mesh mesh){
        mesh.onAdd();
        return meshes.add(mesh);
    }

    /**
     * Remove the Mesh with the input id.
     * @param id the id.
     * @return this Renderer.
     */
    public Renderer removeMesh(int id){
        meshes.remove(id);
        return this;
    }

    /**
     * Return the Mesh with the input id.
     * @param id the id.
     * @return the Mesh if exists, null otherwise.
     */
    public Mesh getMesh(int id){
        return meshes.get(id);
    }

    /**
     * Do the rendering.
     * @param screenWidth the screen width.
     * @param screenHeight the screen height.
     * @param camera the Camera.
     * @param ms engine time in milliseconds.
     */
    public void render(int screenWidth, int screenHeight, Camera camera, long ms) {
        this.ms = ms;
        renderScene(screenWidth,screenHeight, camera);
    }

    private void renderScene(int screenWidth, int screenHeight, Camera camera){
        sceneShader.setLights(lights);
        sceneShader.setViewPos(camera.getEye());
        if (lights.getDirLight().isOn() && lights.getDirLight().isShadowEnabled()){
            lights.getDirLight().getShadowMap().render(sceneRenderer,screenWidth,screenHeight);
            this.ms = 0;
        }
        if (lights.getPointLight().isOn() && lights.getPointLight().isShadowEnabled()) {
            lights.getPointLight().getShadowMap().render(sceneRenderer, screenWidth, screenHeight);
            this.ms = 0;
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

    private void renderShadowMap(Camera camera){
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

    private void draw(Mesh mesh){
        if (mesh.getIndices() != null) {
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, mesh.getIndicesBuffer().capacity(), GLES20.GL_UNSIGNED_INT, mesh.getIndicesBuffer());
        } else {
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, mesh.getNumVertices());
        }
    }

    /**
     * Set lights.
     * @param lights the lights to set.
     * @return this Renderer.
     */
    public Renderer setLights(Lights lights){
        this.lights = lights;
        return this;
    }

    /**
     * Enable blending.
     * @return this Renderer.
     */
    public Renderer enableBlending(){
        blendingEnabled = true;
        return this;
    }

    /**
     * Disable blending.
     * @return this Renderer.
     */
    public Renderer disableBlending(){
        blendingEnabled = false;
        return this;
    }

    /**
     * Return Lights.
     * @return Lights.
     */
    public Lights getLights(){
        return lights;
    }

}
