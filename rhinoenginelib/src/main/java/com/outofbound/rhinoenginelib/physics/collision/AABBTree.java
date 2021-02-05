package com.outofbound.rhinoenginelib.physics.collision;

import com.outofbound.rhinoenginelib.util.vector.Vector3f;

public class AABBTree {

    public AABBTree(float[] vertices, int[] indices){
        for (int i = 0; i < indices.length; i += 3){
            Triangle triangle = new Triangle(getVertex(vertices,i),getVertex(vertices,i+1),getVertex(vertices,i+2));
        }
    }

    private Vector3f getVertex(float[] vertices, int i){
        int j = i*3;
        return new Vector3f(vertices[j],vertices[j+1],vertices[j+2]);
    }
}
