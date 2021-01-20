package com.outofbound.rhinoenginelib.light;



import com.outofbound.rhinoenginelib.util.list.BigList;



public class GLLights {

    private GLDirLight glDirLight;
    private final BigList<GLPointLight> glPointLights;
    private static final int MAX_NUM_POINT_LIGHTS = 8;

    public GLLights(GLDirLight glDirLight){
        this.glDirLight = glDirLight;
        glPointLights = new BigList<>();
    }

    public GLLights setGLDirLight(GLDirLight glDirLight){
        this.glDirLight = glDirLight;
        return this;
    }

    public GLDirLight getGLDirLight(){
        return this.glDirLight;
    }

    public int addGLPointLight(GLPointLight glPointLight){
        if (glPointLights.size() < MAX_NUM_POINT_LIGHTS) {
            return glPointLights.add(glPointLight);
        }
        return -1;
    }

    public BigList<GLPointLight> getGLPointLights(){
        return glPointLights;
    }
}
