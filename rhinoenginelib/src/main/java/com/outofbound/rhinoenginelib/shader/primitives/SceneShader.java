package com.outofbound.rhinoenginelib.shader.primitives;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.outofbound.rhinoenginelib.light.GLDirLight;
import com.outofbound.rhinoenginelib.light.GLLight;
import com.outofbound.rhinoenginelib.light.GLLights;
import com.outofbound.rhinoenginelib.light.GLPointLight;
import com.outofbound.rhinoenginelib.mesh.GLMesh;
import com.outofbound.rhinoenginelib.shader.GLShader;
import com.outofbound.rhinoenginelib.util.vector.Vector3f;

import java.util.ArrayList;

public final class SceneShader extends GLShader {

    private GLMesh glMesh;
    private float[] mMatrix;
    private float[] mvpMatrix;
    private GLLights glLights;
    private Vector3f viewPos;
    private final float[] shadowMVPMatrix = new float[16];
    private final int aPosition;
    private final int aNormal;
    private final int aColor;
    private final int uMMatrix;
    private final int uMVPMatrix;
    private final int uViewPos;
    private final ArrayList<Integer> uDirLight;
    private final int uNumPointLights;
    private final ArrayList<ArrayList<Integer>> uPointLights;

    public SceneShader() {
        super("vs_scene.glsl", "fs_scene.glsl");
        GLES20.glUseProgram(getProgram());
        aPosition = getAttrib("aPosition");
        aNormal = getAttrib("aNormal");
        aColor = getAttrib("aColor");
        uMMatrix = getUniform("uMMatrix");
        uMVPMatrix = getUniform("uMVPMatrix");
        uViewPos = getUniform("uViewPos");
        uDirLight = new ArrayList<>();
        uDirLight.add(getUniform("uDirLight.direction"));
        uDirLight.add(getUniform("uDirLight.ambientColor"));
        uDirLight.add(getUniform("uDirLight.diffuseColor"));
        uDirLight.add(getUniform("uDirLight.specularColor"));
        uDirLight.add(getUniform("uDirLight.shadowMap"));
        uDirLight.add(getUniform("uDirLight.shadowMVPMatrix"));
        uDirLight.add(getUniform("uDirLight.shadowEnabled"));
        uNumPointLights = getUniform("uNumPointLights");
        uPointLights = new ArrayList<>();
    }

    private void bindGLDirLight(){
        GLDirLight glDirLight = glLights.getGLDirLight();
        GLES20.glUniform3f(uDirLight.get(0), glDirLight.getDirection().x, glDirLight.getDirection().y, glDirLight.getDirection().z);
        GLES20.glUniform3f(uDirLight.get(1), glDirLight.getAmbientColor().x, glDirLight.getAmbientColor().y, glDirLight.getAmbientColor().z);
        GLES20.glUniform3f(uDirLight.get(2), glDirLight.getDiffuseColor().x, glDirLight.getDiffuseColor().y, glDirLight.getDiffuseColor().z);
        GLES20.glUniform3f(uDirLight.get(3), glDirLight.getSpecularColor().x, glDirLight.getSpecularColor().y, glDirLight.getSpecularColor().z);
        if (glDirLight.isShadowEnabled()) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, glDirLight.getGLShadowMap().getTexture());
            GLES20.glUniform1i(uDirLight.get(4), 0);
            Matrix.multiplyMM(shadowMVPMatrix, 0, glDirLight.getGLShadowMap().getShadowMapCamera().getVpMatrix(), 0, mMatrix, 0);
            GLES20.glUniformMatrix4fv(uDirLight.get(5), 1, false, shadowMVPMatrix, 0);
        }
        GLES20.glUniform1i(uDirLight.get(6), glDirLight.isShadowEnabled() ? 1 : 0);
    }

    private void bindGLPointLight(GLPointLight glPointLight, int index){
        ArrayList<Integer> uPointLight;
        if (index+1 > uPointLights.size()){
            String name = "uPointLights["+index+"]";
            uPointLight = new ArrayList<>();
            uPointLight.add(getUniform(name+".position"));
            uPointLight.add(getUniform(name+".constant"));
            uPointLight.add(getUniform(name+".linear"));
            uPointLight.add(getUniform(name+".quadratic"));
            uPointLight.add(getUniform(name+".ambientColor"));
            uPointLight.add(getUniform(name+".diffuseColor"));
            uPointLight.add(getUniform(name+".specularColor"));
            uPointLight.add(getUniform(name+".shadowMap"));
            uPointLight.add(getUniform(name+".shadowMVPMatrix"));
            uPointLight.add(getUniform(name+".shadowEnabled"));
            uPointLights.add(uPointLight);
        }
        else {
            uPointLight = uPointLights.get(index);
        }
        GLES20.glUniform3f(uPointLight.get(0), glPointLight.getPosition().x, glPointLight.getPosition().y, glPointLight.getPosition().z);
        GLES20.glUniform1f(uPointLight.get(1), glPointLight.getConstant());
        GLES20.glUniform1f(uPointLight.get(2), glPointLight.getLinear());
        GLES20.glUniform1f(uPointLight.get(3), glPointLight.getQuadratic());
        GLES20.glUniform3f(uPointLight.get(4), glPointLight.getAmbientColor().x, glPointLight.getAmbientColor().y, glPointLight.getAmbientColor().z);
        GLES20.glUniform3f(uPointLight.get(5), glPointLight.getDiffuseColor().x, glPointLight.getDiffuseColor().y, glPointLight.getDiffuseColor().z);
        GLES20.glUniform3f(uPointLight.get(6), glPointLight.getSpecularColor().x, glPointLight.getSpecularColor().y, glPointLight.getSpecularColor().z);
        if (glPointLight.isShadowEnabled()) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE1 + index);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, glPointLight.getGLShadowMap().getTexture());
            GLES20.glUniform1i(uPointLight.get(7), 0);
            Matrix.multiplyMM(shadowMVPMatrix, 0, glPointLight.getGLShadowMap().getShadowMapCamera().getVpMatrix(), 0, mMatrix, 0);
            GLES20.glUniformMatrix4fv(uPointLight.get(8), 1, false, shadowMVPMatrix, 0);
        }
        GLES20.glUniform1i(uPointLight.get(9), glPointLight.isShadowEnabled() ? 1 : 0);
    }

    @Override
    public void bindData() {
        GLES20.glUseProgram(getProgram());
        GLES20.glEnableVertexAttribArray(aPosition);
        GLES20.glEnableVertexAttribArray(aNormal);
        GLES20.glEnableVertexAttribArray(aColor);
        GLES20.glVertexAttribPointer(aPosition, glMesh.getSizeVertex(), GLES20.GL_FLOAT, false, 0, glMesh.getVertexBuffer());
        GLES20.glVertexAttribPointer(aNormal, 3, GLES20.GL_FLOAT, false, 0, glMesh.getNormalBuffer());
        GLES20.glVertexAttribPointer(aColor, 4, GLES20.GL_FLOAT, false, 0, glMesh.getColorBuffer());
        GLES20.glUniformMatrix4fv(uMMatrix, 1, false, mMatrix, 0);
        GLES20.glUniformMatrix4fv(uMVPMatrix, 1, false, mvpMatrix, 0);
        GLES20.glUniform3f(uViewPos, viewPos.x, viewPos.y, viewPos.z);
        bindGLDirLight();
        GLES20.glUniform1i(uNumPointLights, glLights.getGLPointLights().size());
        int i = 0;
        for (GLPointLight glPointLight : glLights.getGLPointLights()){
            bindGLPointLight(glPointLight,i++);
        }
    }

    @Override
    public void unbindData() {
        GLES20.glDisableVertexAttribArray(aPosition);
        GLES20.glDisableVertexAttribArray(aNormal);
        GLES20.glDisableVertexAttribArray(aColor);
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
