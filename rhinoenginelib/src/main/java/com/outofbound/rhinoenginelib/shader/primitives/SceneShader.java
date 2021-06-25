package com.outofbound.rhinoenginelib.shader.primitives;

import android.opengl.GLES30;

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
    private int textureIndex;
    private final int aPosition;
    private final int aNormal;
    private final int aColor;
    private final int aTexCoords;
    private final int uMMatrix;
    private final int uMVPMatrix;
    private final int uViewPos;
    private final ArrayList<Integer> uDirLight;
    private final int uNumPointLights;
    private final ArrayList<ArrayList<Integer>> uPointLights;
    private final int uTexture;
    private final int uIsTextured;

    public SceneShader() {
        super("vs_scene.glsl", "fs_scene.glsl");
        GLES30.glUseProgram(getProgram());
        aPosition = getAttrib("aPosition");
        aNormal = getAttrib("aNormal");
        aColor = getAttrib("aColor");
        aTexCoords = getAttrib("aTexCoords");
        uMMatrix = getUniform("uMMatrix");
        uMVPMatrix = getUniform("uMVPMatrix");
        uViewPos = getUniform("uViewPos");
        uDirLight = new ArrayList<>();
        uDirLight.add(getUniform("uDirLight.on"));
        uDirLight.add(getUniform("uDirLight.direction"));
        uDirLight.add(getUniform("uDirLight.ambientColor"));
        uDirLight.add(getUniform("uDirLight.diffuseColor"));
        uDirLight.add(getUniform("uDirLight.specularColor"));
        uDirLight.add(getUniform("uDirLight.specularExponent"));
        uDirLight.add(getUniform("uDirLight.shadowMap"));
        uDirLight.add(getUniform("uDirLight.shadowVPMatrix"));
        uDirLight.add(getUniform("uDirLight.shadowEnabled"));
        uNumPointLights = getUniform("uNumPointLights");
        uPointLights = new ArrayList<>();
        uTexture = getUniform("uTexture");
        uIsTextured = getUniform("uIsTextured");
    }

    private void bindDirLight(){
        int i = 0;
        DirLight dirLight = lights.getDirLight();
        GLES30.glUniform1i(uDirLight.get(i++), dirLight.isOn() ? 1 : 0);
        GLES30.glUniform3f(uDirLight.get(i++), dirLight.getDirection().x, dirLight.getDirection().y, dirLight.getDirection().z);
        GLES30.glUniform3f(uDirLight.get(i++), mesh.getMaterial().getAmbientColor().x, mesh.getMaterial().getAmbientColor().y, mesh.getMaterial().getAmbientColor().z);
        GLES30.glUniform3f(uDirLight.get(i++), mesh.getMaterial().getDiffuseColor().x, mesh.getMaterial().getDiffuseColor().y, mesh.getMaterial().getDiffuseColor().z);
        GLES30.glUniform3f(uDirLight.get(i++), mesh.getMaterial().getSpecularColor().x, mesh.getMaterial().getSpecularColor().y, mesh.getMaterial().getSpecularColor().z);
        GLES30.glUniform1f(uDirLight.get(i++), mesh.getMaterial().getSpecularExponent());
        if (dirLight.isShadowEnabled()) {
            bindTexture(uDirLight.get(i++), dirLight.getShadowMap().getTexture());
            GLES30.glUniformMatrix4fv(uDirLight.get(i++), 1, false, dirLight.getShadowMap().getCamera().getVpMatrix(), 0);
        }
        GLES30.glUniform1i(uDirLight.get(i), dirLight.isShadowEnabled() ? 1 : 0);
    }

    private void bindPointLight(int index){
        PointLight pointLight = lights.getPointLights().get(index);
        ArrayList<Integer> uPointLight;
        if (index < uPointLights.size()){
            uPointLight = uPointLights.get(index);
        }
        else {
            String name = "uPointLights["+index+"]";
            uPointLight = new ArrayList<>();
            uPointLight.add(getUniform(name+".on"));
            uPointLight.add(getUniform(name+".position"));
            uPointLight.add(getUniform(name+".constant"));
            uPointLight.add(getUniform(name+".linear"));
            uPointLight.add(getUniform(name+".quadratic"));
            uPointLight.add(getUniform(name+".ambientColor"));
            uPointLight.add(getUniform(name+".diffuseColor"));
            uPointLight.add(getUniform(name+".specularColor"));
            uPointLight.add(getUniform(name+".specularExponent"));
            uPointLight.add(getUniform(name+".shadowMap"));
            uPointLight.add(getUniform(name+".shadowVPMatrix"));
            uPointLight.add(getUniform(name+".shadowEnabled"));
            uPointLights.add(uPointLight);
        }
        int i = 0;
        GLES30.glUniform1i(uPointLight.get(i++), pointLight.isOn() ? 1 : 0);
        GLES30.glUniform3f(uPointLight.get(i++), pointLight.getPosition().x, pointLight.getPosition().y, pointLight.getPosition().z);
        GLES30.glUniform1f(uPointLight.get(i++), pointLight.getConstant());
        GLES30.glUniform1f(uPointLight.get(i++), pointLight.getLinear());
        GLES30.glUniform1f(uPointLight.get(i++), pointLight.getQuadratic());
        GLES30.glUniform3f(uPointLight.get(i++), mesh.getMaterial().getAmbientColor().x, mesh.getMaterial().getAmbientColor().y, mesh.getMaterial().getAmbientColor().z);
        GLES30.glUniform3f(uPointLight.get(i++), mesh.getMaterial().getDiffuseColor().x, mesh.getMaterial().getDiffuseColor().y, mesh.getMaterial().getDiffuseColor().z);
        GLES30.glUniform3f(uPointLight.get(i++), mesh.getMaterial().getSpecularColor().x, mesh.getMaterial().getSpecularColor().y, mesh.getMaterial().getSpecularColor().z);
        GLES30.glUniform1f(uPointLight.get(i++), mesh.getMaterial().getSpecularExponent());
        if (pointLight.isShadowEnabled()) {
            bindTexture(uPointLight.get(i++), pointLight.getShadowMap().getTexture());
            GLES30.glUniformMatrix4fv(uPointLight.get(i++), 1, false, pointLight.getShadowMap().getCamera().getVpMatrix(), 0);
        }
        GLES30.glUniform1i(uPointLight.get(i), pointLight.isShadowEnabled() ? 1 : 0);
    }

    @Override
    public void bindData() {
        GLES30.glUseProgram(getProgram());
        GLES30.glEnableVertexAttribArray(aPosition);
        GLES30.glEnableVertexAttribArray(aNormal);
        GLES30.glEnableVertexAttribArray(aColor);
        GLES30.glEnableVertexAttribArray(aTexCoords);
        GLES30.glVertexAttribPointer(aPosition, mesh.getSizeVertex(), GLES30.GL_FLOAT, false, 0, mesh.getVertexBuffer());
        GLES30.glVertexAttribPointer(aNormal, 3, GLES30.GL_FLOAT, false, 0, mesh.getNormalBuffer());
        GLES30.glVertexAttribPointer(aColor, 4, GLES30.GL_FLOAT, false, 0, mesh.getColorBuffer());
        GLES30.glVertexAttribPointer(aTexCoords, 2, GLES30.GL_FLOAT, false, 0, mesh.getTexCoordsBuffer());
        GLES30.glUniformMatrix4fv(uMMatrix, 1, false, mMatrix, 0);
        GLES30.glUniformMatrix4fv(uMVPMatrix, 1, false, mvpMatrix, 0);
        GLES30.glUniform3f(uViewPos, viewPos.x, viewPos.y, viewPos.z);
        textureIndex = 0;
        int c = mesh.getTexture();
        bindTexture(uTexture,c);
        GLES30.glUniform1i(uIsTextured,mesh.getTexture());
        bindDirLight();
        GLES30.glUniform1i(uNumPointLights, lights.getPointLights().size());
        for (int i = 0; i < lights.getPointLights().size(); i++){
            bindPointLight(i);
        }
    }

    @Override
    public void unbindData() {
        GLES30.glDisableVertexAttribArray(aPosition);
        GLES30.glDisableVertexAttribArray(aNormal);
        GLES30.glDisableVertexAttribArray(aColor);
    }

    private void bindTexture(int uniform, int texture){
        GLES30.glActiveTexture(GLES30.GL_TEXTURE0 + textureIndex);
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texture);
        GLES30.glUniform1i(uniform, textureIndex++);
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
