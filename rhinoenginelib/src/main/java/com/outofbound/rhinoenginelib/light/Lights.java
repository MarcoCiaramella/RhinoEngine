package com.outofbound.rhinoenginelib.light;



import com.outofbound.rhinoenginelib.util.vector.Vector3f;


public class Lights {

    private DirLight dirLight;
    private PointLight pointLight;

    public Lights(){
        this.dirLight = new DirLight(new Vector3f(1,-1,0));
        this.pointLight = new PointLight(
                new Vector3f(3,2,0),
                1.0f,
                0.22f,
                0.2f
        );
        this.pointLight.off();
    }

    public Lights setDirLight(DirLight dirLight){
        this.dirLight = dirLight;
        return this;
    }

    public DirLight getDirLight(){
        return dirLight;
    }

    public Lights setPointLight(PointLight pointLight){
        this.pointLight = pointLight;
        return this;
    }

    public PointLight getPointLight(){
        return pointLight;
    }
}
