package com.outofbound.rhinoenginelib.mesh;

import com.lib.joctree.math.Matrix4;
import com.lib.joctree.math.Vector3;
import com.lib.joctree.math.collision.BoundingBox;

import java.util.ArrayList;


public class AABB {

    private final BoundingBox boundingBox;
    private final Matrix4 m4 = new Matrix4();

    private AABB(float minX, float maxX, float minY, float maxY, float minZ, float maxZ) {
        boundingBox = new BoundingBox(new Vector3(minX, minY, minZ), new Vector3(maxX, maxY, maxZ));
    }

    private static float minCoord(float[] vertices, int sizeVertex, int coord){
        float min = vertices[coord];
        for (int i = 0; i < vertices.length; i += sizeVertex){
            min = Math.min(vertices[i+coord], min);
        }
        return min;
    }

    private static float maxCoord(float[] vertices, int sizeVertex, int coord){
        float max = vertices[coord];
        for (int i = 0; i < vertices.length; i += sizeVertex){
            max = Math.max(vertices[i+coord], max);
        }
        return max;
    }

    public void mul(float[] mMatrix){
        boundingBox.mul(m4.set(mMatrix));
    }

    private static AABB newAABB(float[] vertices, int sizeVertex, float minX, float maxX, float minY, float maxY, float minZ, float maxZ) {
        // check if at least one vertex is in the bounds
        for (int i = 0; i < vertices.length; i += sizeVertex) {
            float x = vertices[i];
            float y = vertices[i+1];
            float z = vertices[i+2];
            if (x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ) {
                return new AABB(minX, maxX, minY, maxY, minZ, maxZ);
            }
        }
        return null;
    }

    public static ArrayList<AABB> newAABBGrid(float[] vertices, int sizeVertex, int sizeX, int sizeY, int sizeZ) {
        float minX = minCoord(vertices,sizeVertex,0);
        float maxX = maxCoord(vertices,sizeVertex,0);
        float minY = minCoord(vertices,sizeVertex,1);
        float maxY = maxCoord(vertices,sizeVertex,1);
        float minZ = minCoord(vertices,sizeVertex,2);
        float maxZ = maxCoord(vertices,sizeVertex,2);
        float stepX = (maxX - minX) / sizeX;
        float stepY = (maxY - minY) / sizeY;
        float stepZ = (maxZ - minZ) / sizeZ;
        ArrayList<AABB> grid = new ArrayList<>();
        for (int x = 0; x < sizeX; x++) {
            float currentMinX = minX + (stepX * x);
            float currentMaxX = currentMinX + stepX;
            for (int y = 0; y < sizeY; y++) {
                float currentMinY = minY + (stepY * y);
                float currentMaxY = currentMinY + stepY;
                for (int z = 0; z < sizeZ; z++) {
                    float currentMinZ = minZ + (stepZ * z);
                    float currentMaxZ = currentMinZ + stepZ;
                    AABB aabb = newAABB(vertices, sizeVertex, currentMinX, currentMaxX, currentMinY, currentMaxY, currentMinZ, currentMaxZ);
                    if (aabb != null) {
                        grid.add(aabb);
                    }
                }
            }
        }
        return grid;
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }
}