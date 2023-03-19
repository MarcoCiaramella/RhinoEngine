package com.outofbound.rhinoenginelib.mesh;

import com.lib.joctree.math.Matrix4;
import com.lib.joctree.math.Vector3;
import com.lib.joctree.math.collision.BoundingBox;
import com.outofbound.rhinoenginelib.util.vector.Vector3f;


public class AABB extends BoundingBox {

    private final Matrix4 m4 = new Matrix4();

    public AABB(float[] vertices, int sizeVertex) {
        float minX = minCoord(vertices,sizeVertex,0);
        float maxX = maxCoord(vertices,sizeVertex,0);
        float minY = minCoord(vertices,sizeVertex,1);
        float maxY = maxCoord(vertices,sizeVertex,1);
        float minZ = minCoord(vertices,sizeVertex,2);
        float maxZ = maxCoord(vertices,sizeVertex,2);
        set(new Vector3(minX, minY, minZ), new Vector3(maxX, maxY, maxZ));
    }

    private float minCoord(float[] vertices, int sizeVertex, int coord){
        float min = vertices[coord];
        for (int i = 0; i < vertices.length; i += sizeVertex){
            min = Math.min(vertices[i+coord], min);
        }
        return min;
    }

    private float maxCoord(float[] vertices, int sizeVertex, int coord){
        float max = vertices[coord];
        for (int i = 0; i < vertices.length; i += sizeVertex){
            max = Math.max(vertices[i+coord], max);
        }
        return max;
    }

    public void calc(float[] mMatrix){
        mul(m4.set(mMatrix));
    }
}