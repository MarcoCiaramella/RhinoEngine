package com.outofbound.rhinoenginelib.shader.primitives;

import android.opengl.GLES20;

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
    private final ArrayList<Integer> uPointLight;
    private final int uTexture;
    private final int uIsTextured;

    public SceneShader() {
        super("vs_scene.glsl", "fs_scene.glsl");
        GLES20.glUseProgram(getProgram());
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

        uPointLight = new ArrayList<>();
        uPointLight.add(getUniform("uPointLight.on"));
        uPointLight.add(getUniform("uPointLight.position"));
        uPointLight.add(getUniform("uPointLight.constant"));
        uPointLight.add(getUniform("uPointLight.linear"));
        uPointLight.add(getUniform("uPointLight.quadratic"));
        uPointLight.add(getUniform("uPointLight.ambientColor"));
        uPointLight.add(getUniform("uPointLight.diffuseColor"));
        uPointLight.add(getUniform("uPointLight.specularColor"));
        uPointLight.add(getUniform("uPointLight.specularExponent"));
        uPointLight.add(getUniform("uPointLight.shadowMap"));
        uPointLight.add(getUniform("uPointLight.shadowVPMatrix"));
        uPointLight.add(getUniform("uPointLight.shadowEnabled"));

        uTexture = getUniform("uTexture");
        uIsTextured = getUniform("uIsTextured");
    }

    private void bindDirLight(){
        int i = 0;
        DirLight dirLight = lights.getDirLight();
        GLES20.glUniform1i(uDirLight.get(i++), dirLight.isOn() ? 1 : 0);
        GLES20.glUniform3f(uDirLight.get(i++), dirLight.getDirection().x, dirLight.getDirection().y, dirLight.getDirection().z);
        GLES20.glUniform3f(uDirLight.get(i++), mesh.getMaterial().getAmbientColor().x, mesh.getMaterial().getAmbientColor().y, mesh.getMaterial().getAmbientColor().z);
        GLES20.glUniform3f(uDirLight.get(i++), mesh.getMaterial().getDiffuseColor().x, mesh.getMaterial().getDiffuseColor().y, mesh.getMaterial().getDiffuseColor().z);
        GLES20.glUniform3f(uDirLight.get(i++), mesh.getMaterial().getSpecularColor().x, mesh.getMaterial().getSpecularColor().y, mesh.getMaterial().getSpecularColor().z);
        GLES20.glUniform1f(uDirLight.get(i++), mesh.getMaterial().getSpecularExponent());
        if (dirLight.isShadowEnabled()) {
            bindTexture(uDirLight.get(i++), dirLight.getShadowMap().getTexture());
            GLES20.glUniformMatrix4fv(uDirLight.get(i++), 1, false, dirLight.getShadowMap().getCamera().getVpMatrix(), 0);
        }
        GLES20.glUniform1i(uDirLight.get(i), dirLight.isShadowEnabled() ? 1 : 0);
    }

    private void bindPointLight(){
        int i = 0;
        PointLight pointLight = lights.getPointLight();
        GLES20.glUniform1i(uPointLight.get(i++), pointLight.isOn() ? 1 : 0);
        GLES20.glUniform3f(uPointLight.get(i++), pointLight.getPosition().x, pointLight.getPosition().y, pointLight.getPosition().z);
        GLES20.glUniform1f(uPointLight.get(i++), pointLight.getConstant());
        GLES20.glUniform1f(uPointLight.get(i++), pointLight.getLinear());
        GLES20.glUniform1f(uPointLight.get(i++), pointLight.getQuadratic());
        GLES20.glUniform3f(uPointLight.get(i++), mesh.getMaterial().getAmbientColor().x, mesh.getMaterial().getAmbientColor().y, mesh.getMaterial().getAmbientColor().z);
        GLES20.glUniform3f(uPointLight.get(i++), mesh.getMaterial().getDiffuseColor().x, mesh.getMaterial().getDiffuseColor().y, mesh.getMaterial().getDiffuseColor().z);
        GLES20.glUniform3f(uPointLight.get(i++), mesh.getMaterial().getSpecularColor().x, mesh.getMaterial().getSpecularColor().y, mesh.getMaterial().getSpecularColor().z);
        GLES20.glUniform1f(uPointLight.get(i++), mesh.getMaterial().getSpecularExponent());
        if (pointLight.isShadowEnabled()) {
            bindTexture(uPointLight.get(i++), pointLight.getShadowMap().getTexture());
            GLES20.glUniformMatrix4fv(uPointLight.get(i++), 1, false, pointLight.getShadowMap().getCamera().getVpMatrix(), 0);
        }
        GLES20.glUniform1i(uPointLight.get(i), pointLight.isShadowEnabled() ? 1 : 0);
    }

    @Override
    public void bindData() {
        GLES20.glUseProgram(getProgram());
        GLES20.glEnableVertexAttribArray(aPosition);
        GLES20.glEnableVertexAttribArray(aNormal);
        GLES20.glEnableVertexAttribArray(aColor);
        GLES20.glEnableVertexAttribArray(aTexCoords);
        GLES20.glVertexAttribPointer(aPosition, mesh.getSizeVertex(), GLES20.GL_FLOAT, false, 0, mesh.getVertexBuffer());
        GLES20.glVertexAttribPointer(aNormal, 3, GLES20.GL_FLOAT, false, 0, mesh.getNormalBuffer());
        GLES20.glVertexAttribPointer(aColor, 4, GLES20.GL_FLOAT, false, 0, mesh.getColorBuffer());
        GLES20.glVertexAttribPointer(aTexCoords, 2, GLES20.GL_FLOAT, false, 0, mesh.getTexCoordsBuffer());
        GLES20.glUniformMatrix4fv(uMMatrix, 1, false, mMatrix, 0);
        GLES20.glUniformMatrix4fv(uMVPMatrix, 1, false, mvpMatrix, 0);
        GLES20.glUniform3f(uViewPos, viewPos.x, viewPos.y, viewPos.z);
        textureIndex = 0;
        bindTexture(uTexture,mesh.getTexture());
        GLES20.glUniform1i(uIsTextured,mesh.getTexture());
        bindDirLight();
        bindPointLight();
    }

    @Override
    public void unbindData() {
        GLES20.glDisableVertexAttribArray(aPosition);
        GLES20.glDisableVertexAttribArray(aNormal);
        GLES20.glDisableVertexAttribArray(aColor);
    }

    private void bindTexture(int uniform, int texture){
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + textureIndex);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
        GLES20.glUniform1i(uniform, textureIndex++);
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
