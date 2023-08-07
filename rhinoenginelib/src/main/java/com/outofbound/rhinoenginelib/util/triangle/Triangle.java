package com.outofbound.rhinoenginelib.util.triangle;


import com.outofbound.rhinoenginelib.util.vector.Vector3f;

public class Triangle {

    public final Vector3f v1;
    public final Vector3f v2;
    public final Vector3f v3;
    public final Vector3f normal;

    public Triangle(Vector3f v1, Vector3f v2, Vector3f v3, Vector3f normal) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.normal = normal;
    }


}
