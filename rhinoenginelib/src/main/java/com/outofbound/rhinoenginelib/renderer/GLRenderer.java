package com.outofbound.rhinoenginelib.renderer;

import android.opengl.GLES20;
import android.opengl.Matrix;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;

import com.outofbound.rhinoenginelib.light.GLLight;
import com.outofbound.rhinoenginelib.light.GLLights;
import com.outofbound.rhinoenginelib.mesh.GLMesh;
import com.outofbound.rhinoenginelib.shader.GLShader;
import com.outofbound.rhinoenginelib.util.list.BigList;
import com.outofbound.rhinoenginelib.util.vector.Vector3f;


/**
 * The OpenGL renderer.
 */
public abstract class GLRenderer {

    private final BigList<GLMesh> glMeshes;
    private GLShader glShader;
    private float[] mvMatrix = new float[16];
    private float[] mvpMatrix = new float[16];
    private float time = 0;
    private GLLights glLights;
    private float[] floatArray = new float[255];
    private int numFloat = 0;
    private boolean faceCullingEnabled = true;
    private boolean blendingEnabled = false;
    private boolean shadowEnabled = false;
    private final GLSceneRenderer glSceneRenderer;
    private float[] m;
    private long ms;
    private final GLShader glShaderShadowMap;
    private GLRendererOnTexture shadowMapRenderer;

    /**
     * The renderer constructor.
     * @param glShader the shader for this renderer.
     */
    public GLRenderer(@NonNull GLShader glShader){
        this.glShader = glShader;
        this.glMeshes = new BigList<>();
        this.glLights = new GLLights(1);
        GLLight glLight = new GLLight(new Vector3f(20,30,50),new Vector3f(1,1,1),1000);
        glLights.add(0,glLight);
        glSceneRenderer = this::renderScene;
        glShaderShadowMap = new GLShader("vs_shadow_map.glsl","fs_shadow_map.glsl");
        glShaderShadowMap.config(
                "aPosition",
                null,
                null,
                "uMVPMatrix",
                "uMVMatrix",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null);
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
     * Set shader.
     * @param glShader the shader to set.
     * @return this GLRenderer.
     */
    public GLRenderer setGLShader(GLShader glShader){
        this.glShader = glShader;
        return this;
    }

    /**
     * Get the shader.
     * @return the shader.
     */
    public GLShader getGLShader(){
        return glShader;
    }

    /**
     * Render mesh and shader.
     * @param m projection matrix.
     * @param ms engine time in milliseconds.
     */
    protected void render(float[] m, long ms) {

        this.m = m;
        this.ms = ms;

        GLES20.glUseProgram(glShader.programShader);

        time += ms/1000f;
        if (blendingEnabled) {
            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        }
        if (faceCullingEnabled){
            GLES20.glEnable(GLES20.GL_CULL_FACE);
            GLES20.glCullFace(GLES20.GL_BACK);
        }
        if (glShader.uTimeLocation >= 0) {
            GLES20.glUniform1f(glShader.uTimeLocation, time);
        }
        if (glShader.uLightsPositionLocation >= 0) {
            GLES20.glUniform3fv(glShader.uLightsPositionLocation, glLights.size(), glLights.getPositions(), 0);
        }
        if (glShader.uLightsColorLocation >= 0) {
            GLES20.glUniform3fv(glShader.uLightsColorLocation, glLights.size(), glLights.getColors(), 0);
        }
        if (glShader.uLightsIntensityLocation >= 0) {
            GLES20.glUniform1fv(glShader.uLightsIntensityLocation, glLights.size(), glLights.getIntensities(), 0);
        }
        if (glShader.uNumLightsLocation >= 0) {
            GLES20.glUniform1i(glShader.uNumLightsLocation, glLights.size());
        }
        if (glShader.uFloatArrayLocation >= 0) {
            GLES20.glUniform1fv(glShader.uFloatArrayLocation, numFloat, floatArray, 0);
        }
        if (glShader.uNumFloatLocation >= 0) {
            GLES20.glUniform1i(glShader.uNumFloatLocation, numFloat);
        }
        if (glShader.aPositionLocation >= 0) {
            GLES20.glEnableVertexAttribArray(glShader.aPositionLocation);
        }
        if (glShader.aNormalLocation >= 0) {
            GLES20.glEnableVertexAttribArray(glShader.aNormalLocation);
        }
        if (glShader.aColorLocation >= 0) {
            GLES20.glEnableVertexAttribArray(glShader.aColorLocation);
        }
        if (shadowEnabled && glShader.uShadowMapLocation >= 0) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, shadowMapRenderer.render(glSceneRenderer));
            GLES20.glUniform1i(glShader.uShadowMapLocation, 0);
            this.ms = 0;
        }

        glSceneRenderer.doRendering();

        if (blendingEnabled) {
            GLES20.glDisable(GLES20.GL_BLEND);
        }
        if (faceCullingEnabled){
            GLES20.glDisable(GLES20.GL_CULL_FACE);
        }
        if (glShader.aPositionLocation >= 0) {
            GLES20.glDisableVertexAttribArray(glShader.aPositionLocation);
        }
        if (glShader.aNormalLocation >= 0) {
            GLES20.glDisableVertexAttribArray(glShader.aNormalLocation);
        }
        if (glShader.aColorLocation >= 0) {
            GLES20.glDisableVertexAttribArray(glShader.aColorLocation);
        }
    }

    /**
     * Render the scene.
     */
    private void renderScene(){
        for (GLMesh glMesh : glMeshes) {
            if (!glMesh.isDead(ms)) {
                Matrix.setIdentityM(mvMatrix, 0);
                if (glMesh.getMotion() != null) {
                    glMesh.doTransformation(mvMatrix);
                }
                Matrix.multiplyMM(mvpMatrix, 0, m, 0, mvMatrix, 0);
                if (glMesh.getBoundingBox() != null) {
                    glMesh.getBoundingBox().copyMvMatrix(mvMatrix);
                }
                if (glShader.aPositionLocation >= 0) {
                    GLES20.glVertexAttribPointer(glShader.aPositionLocation, glMesh.getSizeVertex(), GLES20.GL_FLOAT, false, 0, glMesh.getVertexBuffer());
                }
                if (glShader.aNormalLocation >= 0) {
                    GLES20.glVertexAttribPointer(glShader.aNormalLocation, 3, GLES20.GL_FLOAT, false, 0, glMesh.getNormalBuffer());
                }
                if (glShader.aColorLocation >= 0) {
                    GLES20.glVertexAttribPointer(glShader.aColorLocation, 4, GLES20.GL_FLOAT, false, 0, glMesh.getColorBuffer());
                }
                if (glShader.uMVMatrixLocation >= 0) {
                    GLES20.glUniformMatrix4fv(glShader.uMVMatrixLocation, 1, false, mvMatrix, 0);
                }
                if (glShader.uMVPMatrixLocation >= 0) {
                    GLES20.glUniformMatrix4fv(glShader.uMVPMatrixLocation, 1, false, mvpMatrix, 0);
                }
                if (glMesh.getIndices() != null) {
                    GLES20.glDrawElements(GLES20.GL_TRIANGLES, glMesh.getIndicesBuffer().capacity(), GLES20.GL_UNSIGNED_INT, glMesh.getIndicesBuffer());
                } else {
                    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, glMesh.getNumVertices());
                }
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
     * Called when this object is added to GLEngine.
     */
    @CallSuper
    public void onAdd(){
        time = 0;
    }

    /**
     * Called when this object is removed from GLEngine.
     */
    @CallSuper
    public void onRemove(){
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
     * Add a float.
     * @param f the float to add.
     * @return true if float is added, false otherwise.
     */
    public boolean addFloat(float f){
        if (numFloat == floatArray.length){
            return false;
        }
        floatArray[numFloat++] = f;
        return true;
    }

    /**
     * Add floats.
     * @param fa the float array.
     */
    public void addFloats(float[] fa){
        for (float f : fa){
            addFloat(f);
        }
    }

    /**
     * Reset the float array.
     */
    public void resetFloatArray(){
        numFloat = 0;
    }

    /**
     * Do the rendering.
     * @param m3D projection matrix 3D.
     * @param m2D projection matrix 2D.
     * @param ms engine time in milliseconds.
     */
    public abstract void render(float[] m3D, float[] m2D, long ms);

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
     * @return this GLRenderer.
     */
    public GLRenderer configShadow(int resolution){
        shadowMapRenderer = new GLRendererOnTexture(resolution);
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

    private void attachGLShaderShadowMap(GLRenderer glRenderer){
        glShader = glRenderer.getGLShader();
        glRenderer.setGLShader(glShaderShadowMap);
    }

    private void detachGLShaderShadowMap(GLRenderer glRenderer){
        glRenderer.setGLShader(glShader);
    }

}
