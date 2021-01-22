package com.outofbound.rhinoenginelib.shader.primitives;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.outofbound.rhinoenginelib.light.DirLight;
import com.outofbound.rhinoenginelib.light.Lights;
import com.outofbound.rhinoenginelib.light.PointLight;
import com.outofbound.rhinoenginelib.mesh.Mesh;
import com.outofbound.rhinoenginelib.shader.Shader;
import com.outofbound.rhinoenginelib.util.vector.Vector3f;

import java.util.ArrayList;

public final class SceneShader extends Shader {

    private Mesh mesh;
    private float[] mMatrix;
    private float[] mvpMatrix;
    private Lights lights;
    private Vector3f viewPos;
    private final float[] shadowMVPMatrix = new float[16];
    private int shadowIndex = 0;
    private final int aPosition;
    private final int aNormal;
    private final int aColor;
    private final int uMMatrix;
    private final int uMVPMatrix;
    private final int uViewPos;
    private final ArrayList<Integer> uDirLight;
    private final int uNumPointLights;
    private final ArrayList<ArrayList<Integer>> uPointLights;
    private final ArrayList<Integer> uShadowMVPMatrices;
    private final int uNumShadows;

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
        uDirLight.add(getUniform("uDirLight.shadowEnabled"));
        uNumPointLights = getUniform("uNumPointLights");
        uPointLights = new ArrayList<>();
        uShadowMVPMatrices = new ArrayList<>();
        for (int i = 0; i < Lights.MAX_NUM_POINT_LIGHTS + 1; i++) {
            uShadowMVPMatrices.add(getUniform("uShadowMVPMatrices[" + i + "]"));
        }
        uNumShadows = getUniform("uNumShadows");
    }

    private void bindDirLight(){
        DirLight dirLight = lights.getDirLight();
        GLES20.glUniform3f(uDirLight.get(0), dirLight.getDirection().x, dirLight.getDirection().y, dirLight.getDirection().z);
        GLES20.glUniform3f(uDirLight.get(1), dirLight.getAmbientColor().x, dirLight.getAmbientColor().y, dirLight.getAmbientColor().z);
        GLES20.glUniform3f(uDirLight.get(2), dirLight.getDiffuseColor().x, dirLight.getDiffuseColor().y, dirLight.getDiffuseColor().z);
        GLES20.glUniform3f(uDirLight.get(3), dirLight.getSpecularColor().x, dirLight.getSpecularColor().y, dirLight.getSpecularColor().z);
        if (dirLight.isShadowEnabled()) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, dirLight.getShadowMap().getTexture());
            GLES20.glUniform1i(uDirLight.get(4), 0);
            Matrix.multiplyMM(shadowMVPMatrix, 0, dirLight.getShadowMap().getCamera().getVpMatrix(), 0, mMatrix, 0);
            GLES20.glUniformMatrix4fv(uShadowMVPMatrices.get(shadowIndex++), 1, false, shadowMVPMatrix, 0);
        }
        GLES20.glUniform1i(uDirLight.get(5), dirLight.isShadowEnabled() ? 1 : 0);
    }

    private void bindPointLight(PointLight pointLight, int index){
        ArrayList<Integer> uPointLight;
        if (index + 1 > uPointLights.size()){
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
            uPointLight.add(getUniform(name+".shadowEnabled"));
            uPointLights.add(uPointLight);
        }
        else {
            uPointLight = uPointLights.get(index);
        }
        GLES20.glUniform3f(uPointLight.get(0), pointLight.getPosition().x, pointLight.getPosition().y, pointLight.getPosition().z);
        GLES20.glUniform1f(uPointLight.get(1), pointLight.getConstant());
        GLES20.glUniform1f(uPointLight.get(2), pointLight.getLinear());
        GLES20.glUniform1f(uPointLight.get(3), pointLight.getQuadratic());
        GLES20.glUniform3f(uPointLight.get(4), pointLight.getAmbientColor().x, pointLight.getAmbientColor().y, pointLight.getAmbientColor().z);
        GLES20.glUniform3f(uPointLight.get(5), pointLight.getDiffuseColor().x, pointLight.getDiffuseColor().y, pointLight.getDiffuseColor().z);
        GLES20.glUniform3f(uPointLight.get(6), pointLight.getSpecularColor().x, pointLight.getSpecularColor().y, pointLight.getSpecularColor().z);
        if (pointLight.isShadowEnabled()) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE1 + index);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, pointLight.getShadowMap().getTexture());
            GLES20.glUniform1i(uPointLight.get(7), 0);
            Matrix.multiplyMM(shadowMVPMatrix, 0, pointLight.getShadowMap().getCamera().getVpMatrix(), 0, mMatrix, 0);
            GLES20.glUniformMatrix4fv(uShadowMVPMatrices.get(shadowIndex++), 1, false, shadowMVPMatrix, 0);
        }
        GLES20.glUniform1i(uPointLight.get(8), pointLight.isShadowEnabled() ? 1 : 0);
    }

    @Override
    public void bindData() {
        GLES20.glUseProgram(getProgram());
        GLES20.glEnableVertexAttribArray(aPosition);
        GLES20.glEnableVertexAttribArray(aNormal);
        GLES20.glEnableVertexAttribArray(aColor);
        GLES20.glVertexAttribPointer(aPosition, mesh.getSizeVertex(), GLES20.GL_FLOAT, false, 0, mesh.getVertexBuffer());
        GLES20.glVertexAttribPointer(aNormal, 3, GLES20.GL_FLOAT, false, 0, mesh.getNormalBuffer());
        GLES20.glVertexAttribPointer(aColor, 4, GLES20.GL_FLOAT, false, 0, mesh.getColorBuffer());
        GLES20.glUniformMatrix4fv(uMMatrix, 1, false, mMatrix, 0);
        GLES20.glUniformMatrix4fv(uMVPMatrix, 1, false, mvpMatrix, 0);
        GLES20.glUniform3f(uViewPos, viewPos.x, viewPos.y, viewPos.z);
        shadowIndex = 0;
        bindDirLight();
        GLES20.glUniform1i(uNumPointLights, lights.getPointLights().size());
        int i = 0;
        for (PointLight pointLight : lights.getPointLights()){
            bindPointLight(pointLight,i++);
        }
        GLES20.glUniform1i(uNumShadows, shadowIndex);
    }

    @Override
    public void unbindData() {
        GLES20.glDisableVertexAttribArray(aPosition);
        GLES20.glDisableVertexAttribArray(aNormal);
        GLES20.glDisableVertexAttribArray(aColor);
    }

    public SceneShader setMesh(Mesh mesh){
        this.mesh = mesh;
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

    public SceneShader setLights(Lights lights){
        this.lights = lights;
        return this;
    }

    public SceneShader setViewPos(Vector3f viewPos){
        this.viewPos = viewPos;
        return this;
    }

}
