package com.outofbound.rhinoenginelib.renderer;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.outofbound.rhinoenginelib.camera.GLCamera;
import com.outofbound.rhinoenginelib.light.GLDirLight;
import com.outofbound.rhinoenginelib.light.GLLights;
import com.outofbound.rhinoenginelib.mesh.GLMesh;
import com.outofbound.rhinoenginelib.renderer.fx.GLShadowMap;
import com.outofbound.rhinoenginelib.shader.primitives.SceneShader;
import com.outofbound.rhinoenginelib.shader.primitives.ShadowMapShader;
import com.outofbound.rhinoenginelib.util.list.BigList;
import com.outofbound.rhinoenginelib.util.vector.Vector3f;


/**
 * The OpenGL renderer.
 */
public final class GLRenderer {

    private final SceneShader sceneShader;
    private final BigList<GLMesh> glMeshes;
    private final float[] mMatrix = new float[16];
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
        glLights = new GLLights(new GLDirLight(new Vector3f(0.5f,-1,0),new Vector3f(0.2f,0.2f,0.2f),new Vector3f(0.5f,0.5f,0.5f),new Vector3f(1,1,1)));
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
     * Do the rendering.
     * @param screenWidth the screen width.
     * @param screenHeight the screen height.
     * @param glCamera the GLCamera.
     * @param ms engine time in milliseconds.
     */
    public void render(int screenWidth, int screenHeight, GLCamera glCamera, long ms) {
        this.ms = ms;
        renderScene(screenWidth,screenHeight,glCamera);
    }

    private void renderScene(int screenWidth, int screenHeight, GLCamera glCamera){
        sceneShader.setGLLights(glLights);
        sceneShader.setViewPos(glCamera.getEye());
        sceneShader.setShadowEnabled(shadowEnabled);
        if (shadowEnabled){
            glShadowMap.getShadowMapCamera().loadVpMatrix();
            int shadowMap = glShadowMap.render(glSceneRenderer,screenWidth,screenHeight);
            sceneShader.setShadowMap(shadowMap);
            this.ms = 0;
        }
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
                glMesh.doTransformation(mMatrix,ms);
                Matrix.multiplyMM(mvpMatrix, 0, glCamera.getVpMatrix(), 0, mMatrix, 0);
                if (shadowEnabled) {
                    Matrix.multiplyMM(shadowMVPMatrix, 0, glShadowMap.getShadowMapCamera().getVpMatrix(), 0, mMatrix, 0);
                    sceneShader.setShadowMVPMatrix(shadowMVPMatrix);
                }
                if (glMesh.getBoundingBox() != null) {
                    glMesh.getBoundingBox().copyMMatrix(mMatrix);
                }
                sceneShader.setGLMesh(glMesh);
                sceneShader.setMMatrix(mMatrix);
                sceneShader.setMvpMatrix(mvpMatrix);
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
                glMesh.doTransformation(mMatrix,ms);
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
     * Set lights.
     * @param glLights the lights to set.
     * @return this GLRenderer.
     */
    public GLRenderer setGLLights(GLLights glLights){
        this.glLights = glLights;
        return this;
    }

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
     * @param glCamera the GLCamera.
     * @return this GLRenderer.
     */
    public GLRenderer configShadow(int resolution, GLCamera glCamera){
        glShadowMap = new GLShadowMap(resolution,glLights,glCamera);
        shadowMapShader = new ShadowMapShader();
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

    /**
     * Return GLLights.
     * @return GLLights.
     */
    public GLLights getGLLights(){
        return glLights;
    }

}
