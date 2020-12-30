package com.outofbound.rhinoengine.renderer;

import android.opengl.GLES20;
import android.opengl.Matrix;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;

import com.outofbound.rhinoengine.engine.GLEngine;
import com.outofbound.rhinoengine.engine.Loadable;
import com.outofbound.rhinoengine.light.GLLights;
import com.outofbound.rhinoengine.mesh.GLMesh;
import com.outofbound.rhinoengine.shader.GLShader;


/**
 * The OpenGL renderer.
 */
public abstract class GLRenderer implements Loadable {

    private GLMesh glMesh;
    private GLShader glShader;
    private float[] mvMatrix = new float[16];
    private float[] mvpMatrix = new float[16];
    private float time = 0;
    private GLLights glLights;
    private float[] floatArray = new float[255];
    private int numFloat = 0;
    private final int id;
    private boolean faceCullingEnabled = true;
    private boolean blendingEnabled = false;

    /**
     * The renderer constructor.
     * @param id the id for this GLRenderer.
     * @param glMesh the mesh to render.
     * @param glShader the shader to render.
     */
    public GLRenderer(int id, @NonNull GLMesh glMesh, @NonNull GLShader glShader){
        this.id = id;
        this.glMesh = glMesh;
        this.glShader = glShader;
        this.glLights = new GLLights(0);
    }

    /**
     * Set mesh.
     * @param glMesh the mesh to set.
     * @return this GLRenderer.
     */
    public GLRenderer setGLMesh(GLMesh glMesh){
        this.glMesh = glMesh;
        return this;
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
     * Render mesh and shader.
     * @param m projection matrix.
     * @param ms engine time in milliseconds.
     */
    protected void render(float[] m, long ms) {

        Matrix.setIdentityM(mvMatrix,0);
        if (glMesh.getMotion() != null) {
            glMesh.doTransformation(mvMatrix);
        }
        Matrix.multiplyMM(mvpMatrix, 0, m, 0, mvMatrix, 0);

        if (glMesh.getBoundingBox() != null) {
            glMesh.getBoundingBox().copyMvMatrix(mvMatrix);
        }

        GLES20.glUseProgram(glShader.programShader);
        if (glShader.aPositionLocation >= 0) {
            GLES20.glEnableVertexAttribArray(glShader.aPositionLocation);
            GLES20.glVertexAttribPointer(glShader.aPositionLocation, glMesh.getSizeVertex(), GLES20.GL_FLOAT, false, 0, glMesh.getVertexBuffer());
        }
        if (glShader.aNormalLocation >= 0) {
            GLES20.glEnableVertexAttribArray(glShader.aNormalLocation);
            GLES20.glVertexAttribPointer(glShader.aNormalLocation, 3, GLES20.GL_FLOAT, false, 0, glMesh.getNormalBuffer());
        }
        if (glShader.aColorLocation >= 0) {
            GLES20.glEnableVertexAttribArray(glShader.aColorLocation);
            GLES20.glVertexAttribPointer(glShader.aColorLocation, 4, GLES20.GL_FLOAT, false, 0, glMesh.getColorBuffer());
        }
        time += ms/1000f;
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
        if (glShader.uMVMatrixLocation >= 0) {
            GLES20.glUniformMatrix4fv(glShader.uMVMatrixLocation, 1, false, mvMatrix, 0);
        }
        if (glShader.uMVPMatrixLocation >= 0) {
            GLES20.glUniformMatrix4fv(glShader.uMVPMatrixLocation, 1, false, mvpMatrix, 0);
        }

        if (blendingEnabled) {
            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        }

        if (faceCullingEnabled){
            GLES20.glEnable(GLES20.GL_CULL_FACE);
            GLES20.glCullFace(GLES20.GL_BACK);
        }

        if (glMesh.getIndices() != null) {
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, glMesh.getIndicesBuffer().capacity(), GLES20.GL_UNSIGNED_INT, glMesh.getIndicesBuffer());
        }
        else {
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, glMesh.getNumVertices());
        }

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
     * Move the mesh.
     * @param ms engine time in milliseconds.
     */
    public void move(long ms){
        glMesh.move(ms);
    }

    /**
     * Get mesh.
     * @return the mesh.
     */
    public GLMesh getGLMesh(){
        return glMesh;
    }

    /**
     * Get the shader.
     * @return the shader.
     */
    public GLShader getGLShader(){
        return glShader;
    }

    /**
     * Called when this object is added to GLEngine.
     */
    @CallSuper
    public void onAdd(){
        time = 0;
        glMesh.onAdd();
    }

    /**
     * Called when this object is removed from GLEngine.
     */
    @CallSuper
    public void onRemove(){
        glMesh.onRemove();
    }

    /**
     * Check if mesh is dead after ms.
     * @param ms the time in milliseconds.
     * @return true if dead, false otherwise.
     */
    public boolean isDead(long ms){
        return glMesh.isDead(ms);
    }

    /**
     * Kill the mesh.
     */
    public void kill(){
        glMesh.kill();
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
     * Return the id.
     * @return the id.
     */
    public int getId(){
        return id;
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

    @Override
    public boolean load() {
        return GLEngine.getInstance().addGLRenderer(this);
    }

    @Override
    public void unload() {
        kill();
    }

}
