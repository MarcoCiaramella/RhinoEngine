package com.outofbound.rhinoenginelib.physics.collision;

import com.outofbound.rhinoenginelib.util.vector.Vector3f;

public class Triangle {

    private Vector3f v1;
    private Vector3f v2;
    private Vector3f v3;
    private Vector3f centroid;

    public Triangle(Vector3f v1, Vector3f v2, Vector3f v3){
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        centroid = new Vector3f((v1.x+v2.x+v3.x)/3f,(v1.y+v2.y+v3.y)/3f,(v1.z+v2.z+v3.z)/3f);
    }
}
