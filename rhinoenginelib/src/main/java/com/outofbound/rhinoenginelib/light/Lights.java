package com.outofbound.rhinoenginelib.light;



import com.outofbound.rhinoenginelib.util.vector.Vector3f;


/**
 * Wrapper class for directional and point lights.
 */
public class Lights {

    private DirLight dirLight;
    private PointLight pointLight;

    /**
     * The constructor.
     */
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

    /**
     * Sets the directional light.
     * @param dirLight the directional light
     * @return this Lights object
     */
    public Lights setDirLight(DirLight dirLight){
        this.dirLight = dirLight;
        return this;
    }

    /**
     * Returns the directional light.
     * @return the DirLight object
     */
    public DirLight getDirLight(){
        return dirLight;
    }

    /**
     * Sets the point light.
     * @param pointLight the point light
     * @return this Lights object
     */
    public Lights setPointLight(PointLight pointLight){
        this.pointLight = pointLight;
        return this;
    }

    /**
     * Returns the point light.
     * @return the PointLight object
     */
    public PointLight getPointLight(){
        return pointLight;
    }
}
