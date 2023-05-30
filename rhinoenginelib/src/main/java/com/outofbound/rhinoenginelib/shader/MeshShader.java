package com.outofbound.rhinoenginelib.shader;

import android.opengl.GLES20;

import com.outofbound.rhinoenginelib.light.DirLight;
import com.outofbound.rhinoenginelib.light.Lights;
import com.outofbound.rhinoenginelib.light.PointLight;
import com.outofbound.rhinoenginelib.util.vector.Vector3f;

import java.util.ArrayList;

public class MeshShader extends Shader {

    private float[] mMatrix;
    private float[] mvpMatrix;
    private Lights lights;
    private Vector3f viewPos;
    private final int aPosition;
    private final int aNormal;
    private final int uMMatrix;
    private final int uMVPMatrix;
    private final int uViewPos;
    private final ArrayList<Integer> uDirLight;
    private final ArrayList<Integer> uPointLight;
    protected int textureIndex;

    public MeshShader(String vs, String fs) {
        super(vs, fs);
        use();
        aPosition = getAttrib("aPosition");
        aNormal = getAttrib("aNormal");
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

    }

    @Override
    public void bindData() {
        use();
        GLES20.glEnableVertexAttribArray(aPosition);
        GLES20.glEnableVertexAttribArray(aNormal);
        GLES20.glVertexAttribPointer(aPosition, 3, GLES20.GL_FLOAT, false, 0, data.vertexBuffer);
        GLES20.glVertexAttribPointer(aNormal, 3, GLES20.GL_FLOAT, false, 0, data.normalBuffer);
        GLES20.glUniformMatrix4fv(uMMatrix, 1, false, mMatrix, 0);
        GLES20.glUniformMatrix4fv(uMVPMatrix, 1, false, mvpMatrix, 0);
        GLES20.glUniform3f(uViewPos, viewPos.x, viewPos.y, viewPos.z);
        textureIndex = 0;
        bindDirLight(textureIndex);
        textureIndex++;
        bindPointLight(textureIndex);
        textureIndex++;
    }

    @Override
    public void unbindData() {
        GLES20.glDisableVertexAttribArray(aPosition);
        GLES20.glDisableVertexAttribArray(aNormal);
    }

    private void bindDirLight(int textureIndex){
        int i = 0;
        DirLight dirLight = lights.getDirLight();
        GLES20.glUniform1i(uDirLight.get(i++), dirLight.isOn() ? 1 : 0);
        GLES20.glUniform3f(uDirLight.get(i++), dirLight.getDirection().x, dirLight.getDirection().y, dirLight.getDirection().z);
        GLES20.glUniform3f(uDirLight.get(i++), data.ambientColor.x, data.ambientColor.y, data.ambientColor.z);
        GLES20.glUniform3f(uDirLight.get(i++), data.diffuseColor.x, data.diffuseColor.y, data.diffuseColor.z);
        GLES20.glUniform3f(uDirLight.get(i++), data.specularColor.x, data.specularColor.y, data.specularColor.z);
        GLES20.glUniform1f(uDirLight.get(i++), data.specularExponent);
        if (dirLight.isShadowEnabled()) {
            bindTexture(uDirLight.get(i++), dirLight.getShadowMap().getTexture(), textureIndex);
            GLES20.glUniformMatrix4fv(uDirLight.get(i++), 1, false, dirLight.getShadowMap().getCamera().getVpMatrix(), 0);
        }
        GLES20.glUniform1i(uDirLight.get(i), dirLight.isShadowEnabled() ? 1 : 0);
    }

    private void bindPointLight(int textureIndex){
        int i = 0;
        PointLight pointLight = lights.getPointLight();
        GLES20.glUniform1i(uPointLight.get(i++), pointLight.isOn() ? 1 : 0);
        GLES20.glUniform3f(uPointLight.get(i++), pointLight.getPosition().x, pointLight.getPosition().y, pointLight.getPosition().z);
        GLES20.glUniform1f(uPointLight.get(i++), pointLight.getConstant());
        GLES20.glUniform1f(uPointLight.get(i++), pointLight.getLinear());
        GLES20.glUniform1f(uPointLight.get(i++), pointLight.getQuadratic());
        GLES20.glUniform3f(uPointLight.get(i++), data.ambientColor.x, data.ambientColor.y, data.ambientColor.z);
        GLES20.glUniform3f(uPointLight.get(i++), data.diffuseColor.x, data.diffuseColor.y, data.diffuseColor.z);
        GLES20.glUniform3f(uPointLight.get(i++), data.specularColor.x, data.specularColor.y, data.specularColor.z);
        GLES20.glUniform1f(uPointLight.get(i++), data.specularExponent);
        if (pointLight.isShadowEnabled()) {
            bindTexture(uPointLight.get(i++), pointLight.getShadowMap().getTexture(), textureIndex);
            GLES20.glUniformMatrix4fv(uPointLight.get(i++), 1, false, pointLight.getShadowMap().getCamera().getVpMatrix(), 0);
        }
        GLES20.glUniform1i(uPointLight.get(i), pointLight.isShadowEnabled() ? 1 : 0);
    }

    protected void bindTexture(int uniform, int texture, int index){
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + index);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
        GLES20.glUniform1i(uniform, index);
    }


    public MeshShader setMMatrix(float[] mMatrix){
        this.mMatrix = mMatrix;
        return this;
    }

    public MeshShader setMvpMatrix(float[] mvpMatrix){
        this.mvpMatrix = mvpMatrix;
        return this;
    }

    public MeshShader setLights(Lights lights){
        this.lights = lights;
        return this;
    }

    public MeshShader setViewPos(Vector3f viewPos){
        this.viewPos = viewPos;
        return this;
    }

}
