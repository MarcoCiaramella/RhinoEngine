package com.outofbound.rhinoenginelib.light;



import com.outofbound.rhinoenginelib.util.list.BigList;



public class Lights {

    private DirLight dirLight;
    private final BigList<PointLight> pointLights;
    public static final int MAX_NUM_POINT_LIGHTS = 8;

    public Lights(DirLight dirLight){
        this.dirLight = dirLight;
        pointLights = new BigList<>();
    }

    public Lights setDirLight(DirLight dirLight){
        this.dirLight = dirLight;
        return this;
    }

    public DirLight getDirLight(){
        return this.dirLight;
    }

    public int addPointLight(PointLight pointLight){
        if (pointLights.size() < MAX_NUM_POINT_LIGHTS) {
            return pointLights.add(pointLight);
        }
        return -1;
    }

    public BigList<PointLight> getPointLights(){
        return pointLights;
    }
}
