package com.outofbound.rhinoenginelib.shader.primitives;

import android.opengl.GLES20;

import com.outofbound.rhinoenginelib.light.GLDirLight;
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
    private int shadowMap;
    private float[] shadowMVPMatrix = new float[16];
    private int shadowEnabled;
    private final int aPosition;
    private final int aNormal;
    private final int aColor;
    private final int uMMatrix;
    private final int uMVPMatrix;
    private final int uViewPos;
    private final ArrayList<Integer> uDirLight;
    private final int uNumPointLights;
    private final ArrayList<ArrayList<Integer>> uPointLights;
    private final int uShadowMap;
    private final int uShadowMVPMatrix;
    private final int uShadowEnabled;

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
        uNumPointLights = getUniform("uNumPointLights");
        uPointLights = new ArrayList<>();
        uShadowMap = getUniform("uShadowMap");
        uShadowMVPMatrix = getUniform("uShadowMVPMatrix");
        uShadowEnabled = getUniform("uShadowEnabled");
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
        GLDirLight glDirLight = glLights.getGLDirLight();
        GLES20.glUniform3f(uDirLight.get(0), glDirLight.getDirection().x, glDirLight.getDirection().y, glDirLight.getDirection().z);
        GLES20.glUniform3f(uDirLight.get(1), glDirLight.getAmbientColor().x, glDirLight.getAmbientColor().y, glDirLight.getAmbientColor().z);
        GLES20.glUniform3f(uDirLight.get(2), glDirLight.getDiffuseColor().x, glDirLight.getDiffuseColor().y, glDirLight.getDiffuseColor().z);
        GLES20.glUniform3f(uDirLight.get(3), glDirLight.getSpecularColor().x, glDirLight.getSpecularColor().y, glDirLight.getSpecularColor().z);
        GLES20.glUniform1i(uNumPointLights, glLights.getGLPointLights().size());
        int i = 0;
        for (GLPointLight glPointLight : glLights.getGLPointLights()){
            ArrayList<Integer> uPointLight;
            if (i+1 > uPointLights.size()){
                String name = "uPointLights["+i+"]";
                uPointLight = new ArrayList<>();
                uPointLight.add(getUniform(name+".position"));
                uPointLight.add(getUniform(name+".constant"));
                uPointLight.add(getUniform(name+".linear"));
                uPointLight.add(getUniform(name+".quadratic"));
                uPointLight.add(getUniform(name+".ambientColor"));
                uPointLight.add(getUniform(name+".diffuseColor"));
                uPointLight.add(getUniform(name+".specularColor"));
                uPointLights.add(uPointLight);
            }
            else {
                uPointLight = uPointLights.get(i);
            }
            GLES20.glUniform3f(uPointLight.get(0), glPointLight.getPosition().x, glPointLight.getPosition().y, glPointLight.getPosition().z);
            GLES20.glUniform1f(uPointLight.get(1), glPointLight.getConstant());
            GLES20.glUniform1f(uPointLight.get(2), glPointLight.getLinear());
            GLES20.glUniform1f(uPointLight.get(3), glPointLight.getQuadratic());
            GLES20.glUniform3f(uPointLight.get(4), glPointLight.getAmbientColor().x, glPointLight.getAmbientColor().y, glPointLight.getAmbientColor().z);
            GLES20.glUniform3f(uPointLight.get(5), glPointLight.getDiffuseColor().x, glPointLight.getDiffuseColor().y, glPointLight.getDiffuseColor().z);
            GLES20.glUniform3f(uPointLight.get(6), glPointLight.getSpecularColor().x, glPointLight.getSpecularColor().y, glPointLight.getSpecularColor().z);
            i++;
        }
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, shadowMap);
        GLES20.glUniform1i(uShadowMap, 0);
        GLES20.glUniformMatrix4fv(uShadowMVPMatrix, 1, false, shadowMVPMatrix, 0);
        GLES20.glUniform1i(uShadowEnabled, shadowEnabled);
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

    public SceneShader setShadowMap(int shadowMap){
        this.shadowMap = shadowMap;
        return this;
    }

    public SceneShader setShadowMVPMatrix(float[] shadowMVPMatrix){
        this.shadowMVPMatrix = shadowMVPMatrix;
        return this;
    }

    public SceneShader setShadowEnabled(boolean shadowEnabled){
        if (shadowEnabled){
            this.shadowEnabled = 1;
        }
        else {
            this.shadowEnabled = 0;
        }
        return this;
    }

}
