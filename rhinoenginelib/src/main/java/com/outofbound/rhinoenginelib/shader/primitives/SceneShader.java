package com.outofbound.rhinoenginelib.shader.primitives;

import android.opengl.GLES20;

import com.outofbound.rhinoenginelib.light.GLDirLight;
import com.outofbound.rhinoenginelib.light.GLLights;
import com.outofbound.rhinoenginelib.light.GLPointLight;
import com.outofbound.rhinoenginelib.mesh.GLMesh;
import com.outofbound.rhinoenginelib.shader.GLShader;
import com.outofbound.rhinoenginelib.util.vector.Vector3f;

public final class SceneShader extends GLShader {

    private GLMesh glMesh;
    private float[] mMatrix;
    private float[] mvpMatrix;
    private GLLights glLights;
    private Vector3f viewPos;

    public SceneShader() {
        super("vs_scene.glsl", "fs_scene.glsl");
    }

    @Override
    public void bindData() {
        GLES20.glUseProgram(getProgramShader());
        GLES20.glEnableVertexAttribArray(getAttrib("aPosition"));
        GLES20.glEnableVertexAttribArray(getAttrib("aNormal"));
        GLES20.glEnableVertexAttribArray(getAttrib("aColor"));
        GLES20.glVertexAttribPointer(getAttrib("aPosition"), glMesh.getSizeVertex(), GLES20.GL_FLOAT, false, 0, glMesh.getVertexBuffer());
        GLES20.glVertexAttribPointer(getAttrib("aNormal"), 3, GLES20.GL_FLOAT, false, 0, glMesh.getNormalBuffer());
        GLES20.glVertexAttribPointer(getAttrib("aColor"), 4, GLES20.GL_FLOAT, false, 0, glMesh.getColorBuffer());
        GLES20.glUniformMatrix4fv(getUniform("uMMatrix"), 1, false, mMatrix, 0);
        GLES20.glUniformMatrix4fv(getUniform("uMVPMatrix"), 1, false, mvpMatrix, 0);
        GLES20.glUniform3f(getUniform("uViewPos"), viewPos.x, viewPos.y, viewPos.z);
        GLDirLight glDirLight = glLights.getGLDirLight();
        GLES20.glUniform3f(getUniform("uDirLight.direction"), glDirLight.getDirection().x, glDirLight.getDirection().y, glDirLight.getDirection().z);
        GLES20.glUniform3f(getUniform("uDirLight.ambientColor"), glDirLight.getAmbientColor().x, glDirLight.getAmbientColor().y, glDirLight.getAmbientColor().z);
        GLES20.glUniform3f(getUniform("uDirLight.diffuseColor"), glDirLight.getDiffuseColor().x, glDirLight.getDiffuseColor().y, glDirLight.getDiffuseColor().z);
        GLES20.glUniform3f(getUniform("uDirLight.specularColor"), glDirLight.getSpecularColor().x, glDirLight.getSpecularColor().y, glDirLight.getSpecularColor().z);
        GLES20.glUniform1i(getUniform("uNumPointLights"), glLights.getGLPointLights().size());
        int i = 0;
        for (GLPointLight glPointLight : glLights.getGLPointLights()){
            String name = "uPointLights["+i+"]";
            i++;
            GLES20.glUniform3f(getUniform(name+".position"), glPointLight.getPosition().x, glPointLight.getPosition().y, glPointLight.getPosition().z);
            GLES20.glUniform1f(getUniform(name+".constant"), glPointLight.getConstant());
            GLES20.glUniform1f(getUniform(name+".linear"), glPointLight.getLinear());
            GLES20.glUniform1f(getUniform(name+".quadratic"), glPointLight.getQuadratic());
            GLES20.glUniform3f(getUniform(name+".ambientColor"), glPointLight.getAmbientColor().x, glPointLight.getAmbientColor().y, glPointLight.getAmbientColor().z);
            GLES20.glUniform3f(getUniform(name+".diffuseColor"), glPointLight.getDiffuseColor().x, glPointLight.getDiffuseColor().y, glPointLight.getDiffuseColor().z);
            GLES20.glUniform3f(getUniform(name+".specularColor"), glPointLight.getSpecularColor().x, glPointLight.getSpecularColor().y, glPointLight.getSpecularColor().z);
        }
    }

    @Override
    public void unbindData() {
        GLES20.glDisableVertexAttribArray(getAttrib("aPosition"));
        GLES20.glDisableVertexAttribArray(getAttrib("aNormal"));
        GLES20.glDisableVertexAttribArray(getAttrib("aColor"));
    }

    public SceneShader setGLMesh(GLMesh glMesh){
        this.glMesh = glMesh;
        return this;
    }

    public SceneShader setMMatrix(float[] mMatrix){
        this.mMatrix = mMatrix;
        return this;
    }

    public SceneShader setMvpMatrix(float[] mvpMatrix){
        this.mvpMatrix = mvpMatrix;
        return this;
    }

    public SceneShader setGLLights(GLLights glLights){
        this.glLights = glLights;
        return this;
    }

    public SceneShader setViewPos(Vector3f viewPos){
        this.viewPos = viewPos;
        return this;
    }

}
