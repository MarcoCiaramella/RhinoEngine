package com.outofbound.rhinoenginelib.renderer;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.outofbound.rhinoenginelib.camera.GLCamera;
import com.outofbound.rhinoenginelib.camera.GLCamera2D;
import com.outofbound.rhinoenginelib.camera.GLCamera3D;
import com.outofbound.rhinoenginelib.light.GLLight;
import com.outofbound.rhinoenginelib.light.GLLights;
import com.outofbound.rhinoenginelib.mesh.GLMesh;
import com.outofbound.rhinoenginelib.renderer.fx.GLShadowMap;
import com.outofbound.rhinoenginelib.shader.primitives.SceneShader;
import com.outofbound.rhinoenginelib.shader.primitives.SceneWithShadowShader;
import com.outofbound.rhinoenginelib.shader.primitives.ShadowMapShader;
import com.outofbound.rhinoenginelib.util.list.BigList;
import com.outofbound.rhinoenginelib.util.vector.Vector3f;


/**
 * The OpenGL renderer.
 */
public abstract class GLRenderer {

    private final SceneShader sceneShader;
    private SceneWithShadowShader sceneWithShadowShader;
    private final BigList<GLMesh> glMeshes;
    private final float[] mMatrix = new float[16];
    private final float[] mvMatrix = new float[16];
    private final float[] mvpMatrix = new float[16];
    private GLLights glLights;
    private boolean faceCullingEnabled = true;
    private boolean blendingEnabled = false;
    private boolean shadowEnabled = false;
    private final GLSceneRenderer glSceneRenderer;
    private long ms;
    private ShadowMapShader shadowMapShader;
    private GLShadowMap glShadowMap;
    private final float[] shadowMVPMatrix = new float[16];


    /**
     * The renderer constructor.
     */
    public GLRenderer(){
        sceneShader = new SceneShader();
        glMeshes = new BigList<>();
        glLights = new GLLights(1);
        GLLight glLight = new GLLight(new Vector3f(20,30,50),new Vector3f(1,1,1),1000);
        glLights.add(0,glLight);
        glSceneRenderer = this::renderSceneShadowMap;
    }

    /**
     * Add a GLMesh.
     * @param glMesh the GLMesh to add.
     * @return the GLMesh id, -1 if GLMesh is a duplicate.
     */
    public int addGLMesh(GLMesh glMesh){
        glMesh.onAdd();
        return glMeshes.add(glMesh);
    }

    /**
     * Remove the GLMesh with the input id.
     * @param id the id.
     * @return this GLRenderer.
     */
    public GLRenderer removeGLMesh(int id){
        glMeshes.remove(id);
        return this;
    }

    /**
     * Return the GLMesh with the input id.
     * @param id the id.
     * @return the GLMesh if exists, null otherwise.
     */
    public GLMesh getGLMesh(int id){
        return glMeshes.get(id);
    }

    /**
     * Render mesh and shader.
     * @param screenWidth the screen width.
     * @param screenHeight the screen height.
     * @param glCamera the GLCamera.
     * @param ms engine time in milliseconds.
     */
    protected void render(int screenWidth, int screenHeight, GLCamera glCamera, long ms) {

        glCamera.loadVpMatrix(screenWidth, screenHeight);
        this.ms = ms;

        if (!shadowEnabled){
            renderScene(glCamera);
        }
        else {
            renderSceneWithShadow(screenWidth,screenHeight,glCamera);
        }
    }

    private void renderScene(GLCamera glCamera){
        if (blendingEnabled) {
            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        }
        if (faceCullingEnabled){
            GLES20.glEnable(GLES20.GL_CULL_FACE);
            GLES20.glCullFace(GLES20.GL_BACK);
        }
        for (GLMesh glMesh : glMeshes) {
            if (!glMesh.isDead(ms)) {
                Matrix.setIdentityM(mMatrix, 0);
                glMesh.doTransformation(mMatrix);
                Matrix.multiplyMM(mvMatrix, 0, glCamera.getViewMatrix(), 0, mMatrix, 0);
                Matrix.multiplyMM(mvpMatrix, 0, glCamera.getVpMatrix(), 0, mMatrix, 0);
                if (glMesh.getBoundingBox() != null) {
                    glMesh.getBoundingBox().copyMMatrix(mMatrix);
                }
                sceneShader.setGLMesh(glMesh);
                sceneShader.setMvMatrix(mvMatrix);
                sceneShader.setMvpMatrix(mvpMatrix);
                sceneShader.setGLLights(glLights);
                sceneShader.bindData();
                if (glMesh.getIndices() != null) {
                    GLES20.glDrawElements(GLES20.GL_TRIANGLES, glMesh.getIndicesBuffer().capacity(), GLES20.GL_UNSIGNED_INT, glMesh.getIndicesBuffer());
                } else {
                    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, glMesh.getNumVertices());
                }
                sceneShader.unbindData();
            }
            else {
                glMeshes.remove(glMesh);
            }
        }
        if (faceCullingEnabled){
            GLES20.glDisable(GLES20.GL_CULL_FACE);
        }
        if (blendingEnabled) {
            GLES20.glDisable(GLES20.GL_BLEND);
        }
    }

    private void renderSceneShadowMap(){
        for (GLMesh glMesh : glMeshes) {
            if (!glMesh.isDead(ms)) {
                Matrix.setIdentityM(mMatrix, 0);
                glMesh.doTransformation(mMatrix);
                Matrix.multiplyMM(shadowMVPMatrix, 0, glShadowMap.getShadowMapCamera().getVpMatrix(), 0, mMatrix, 0);
                if (glMesh.getBoundingBox() != null) {
                    glMesh.getBoundingBox().copyMMatrix(mMatrix);
                }
                shadowMapShader.setGLMesh(glMesh);
                shadowMapShader.setMvpMatrix(shadowMVPMatrix);
                shadowMapShader.bindData();
                if (glMesh.getIndices() != null) {
                    GLES20.glDrawElements(GLES20.GL_TRIANGLES, glMesh.getIndicesBuffer().capacity(), GLES20.GL_UNSIGNED_INT, glMesh.getIndicesBuffer());
                } else {
                    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, glMesh.getNumVertices());
                }
                shadowMapShader.unbindData();
            }
            else {
                glMeshes.remove(glMesh);
            }
        }
    }

    /**
     * Move the mesh.
     * @param ms engine time in milliseconds.
     */
    public void move(long ms){
        for (GLMesh glMesh : glMeshes) {
            glMesh.move(ms);
        }
    }

    /**
     * Set lights.
     * @param glLights the lights to set.
     * @return this GLRenderer.
     */
    public GLRenderer setGLLights(GLLights glLights){
        this.glLights = glLights;
        return this;
    }

    /**
     * Do the rendering.
     * @param screenWidth the screen width.
     * @param screenHeight the screen height.
     * @param glCamera3D the GLCamera3D.
     * @param glCamera2D the GLCamera2D.
     * @param ms engine time in milliseconds.
     */
    public abstract void render(int screenWidth, int screenHeight, GLCamera3D glCamera3D, GLCamera2D glCamera2D, long ms);

    /**
     * Enable face culling.
     * @return this GLRenderer.
     */
    public GLRenderer enableFaceCulling(){
        faceCullingEnabled = true;
        return this;
    }

    /**
     * Disable face culling.
     * @return this GLRenderer.
     */
    public GLRenderer disableFaceCulling(){
        faceCullingEnabled = false;
        return this;
    }

    /**
     * Enable blending.
     * @return this GLRenderer.
     */
    public GLRenderer enableBlending(){
        blendingEnabled = true;
        return this;
    }

    /**
     * Disable blending.
     * @return this GLRenderer.
     */
    public GLRenderer disableBlending(){
        blendingEnabled = false;
        return this;
    }

    /**
     * Configure shadow.
     * @param resolution the quality level. Must be GLRendererOnTexture.RESOLUTION_256, GLRendererOnTexture.RESOLUTION_512 or GLRendererOnTexture.RESOLUTION_1024.
     * @param near camera near.
     * @param far camera far.
     * @return this GLRenderer.
     */
    public GLRenderer configShadow(int resolution, float near, float far){
        if (glLights.size() > 0) {
            glShadowMap = new GLShadowMap(resolution,glLights.getFirstLight(),near,far);
            shadowMapShader = new ShadowMapShader();
            sceneWithShadowShader = new SceneWithShadowShader();
        }
        return this;
    }

    /**
     * Enable shadow.
     * @return this GLRenderer.
     */
    public GLRenderer enableShadow(){
        shadowEnabled = true;
        return this;
    }

    /**
     * Disable shadow.
     * @return this GLRenderer.
     */
    public GLRenderer disableShadow(){
        shadowEnabled = false;
        return this;
    }

    private void renderSceneWithShadow(int screenWidth, int screenHeight, GLCamera glCamera){
        glShadowMap.getShadowMapCamera().loadVpMatrix(screenWidth,screenHeight);
        int shadowMap = glShadowMap.render(glSceneRenderer,screenWidth,screenHeight);
        this.ms = 0;

        if (blendingEnabled) {
            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        }
        if (faceCullingEnabled){
            GLES20.glEnable(GLES20.GL_CULL_FACE);
            GLES20.glCullFace(GLES20.GL_BACK);
        }
        for (GLMesh glMesh : glMeshes) {
            if (!glMesh.isDead(ms)) {
                Matrix.setIdentityM(mMatrix, 0);
                glMesh.doTransformation(mMatrix);
                Matrix.multiplyMM(mvMatrix, 0, glCamera.getViewMatrix(), 0, mMatrix, 0);
                Matrix.multiplyMM(mvpMatrix, 0, glCamera.getVpMatrix(), 0, mMatrix, 0);
                Matrix.multiplyMM(shadowMVPMatrix, 0, glShadowMap.getShadowMapCamera().getVpMatrix(), 0, mMatrix, 0);
                if (glMesh.getBoundingBox() != null) {
                    glMesh.getBoundingBox().copyMMatrix(mMatrix);
                }
                sceneWithShadowShader.setGLMesh(glMesh);
                sceneWithShadowShader.setMvMatrix(mvMatrix);
                sceneWithShadowShader.setMvpMatrix(mvpMatrix);
                sceneWithShadowShader.setGLLights(glLights);
                sceneWithShadowShader.setShadowMap(shadowMap);
                sceneWithShadowShader.setShadowMVPMatrix(shadowMVPMatrix);
                sceneWithShadowShader.bindData();
                if (glMesh.getIndices() != null) {
                    GLES20.glDrawElements(GLES20.GL_TRIANGLES, glMesh.getIndicesBuffer().capacity(), GLES20.GL_UNSIGNED_INT, glMesh.getIndicesBuffer());
                } else {
                    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, glMesh.getNumVertices());
                }
                sceneWithShadowShader.unbindData();
            }
            else {
                glMeshes.remove(glMesh);
            }
        }
        if (faceCullingEnabled){
            GLES20.glDisable(GLES20.GL_CULL_FACE);
        }
        if (blendingEnabled) {
            GLES20.glDisable(GLES20.GL_BLEND);
        }
    }

}
