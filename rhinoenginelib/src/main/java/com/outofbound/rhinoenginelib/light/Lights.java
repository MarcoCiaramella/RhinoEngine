package com.outofbound.rhinoenginelib.light;



import com.outofbound.rhinoenginelib.util.list.BigList;
import com.outofbound.rhinoenginelib.util.vector.Vector3f;


public class Lights {

    private DirLight dirLight;
    private final BigList<PointLight> pointLights;
    public static final int MAX_NUM_POINT_LIGHTS = 8;

    public Lights(){
        this.dirLight = new DirLight(new Vector3f(1,-1,0));
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
