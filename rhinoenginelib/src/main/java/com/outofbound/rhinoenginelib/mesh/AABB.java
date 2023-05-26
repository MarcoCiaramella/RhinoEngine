package com.outofbound.rhinoenginelib.mesh;

import com.lib.joctree.math.Matrix4;
import com.lib.joctree.math.Vector3;
import com.lib.joctree.math.collision.BoundingBox;

import java.nio.FloatBuffer;
import java.util.ArrayList;


public class AABB {

    private final BoundingBox boundingBox;
    private final Matrix4 m4 = new Matrix4();
    private final Mesh parent;

    private AABB(Mesh parent, float minX, float maxX, float minY, float maxY, float minZ, float maxZ) {
        boundingBox = new BoundingBox(new Vector3(minX, minY, minZ), new Vector3(maxX, maxY, maxZ));
        this.parent = parent;
    }

    public Mesh getParent() {
        return parent;
    }

    private static float minCoord(Mesh parent, int coord){
        float min = parent.getVertexBuffer().get(coord);
        for (int i = 0; i < parent.getVertexBuffer().capacity(); i += parent.getSizeVertex()){
            min = Math.min(parent.getVertexBuffer().get(i+coord), min);
        }
        return min;
    }

    private static float maxCoord(Mesh parent, int coord){
        float max = parent.getVertexBuffer().get(coord);
        for (int i = 0; i < parent.getVertexBuffer().capacity(); i += parent.getSizeVertex()){
            max = Math.max(parent.getVertexBuffer().get(i+coord), max);
        }
        return max;
    }

    public void mul(float[] mMatrix){
        boundingBox.mul(m4.set(mMatrix));
    }

    private static AABB newAABB(Mesh parent, float minX, float maxX, float minY, float maxY, float minZ, float maxZ) {
        // check if at least one vertex is in the bounds
        for (int i = 0; i < parent.getVertexBuffer().capacity(); i += parent.getSizeVertex()) {
            float x = parent.getVertexBuffer().get(i);
            float y = parent.getVertexBuffer().get(i+1);
            float z = parent.getVertexBuffer().get(i+2);
            if (x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ) {
                return new AABB(parent, minX, maxX, minY, maxY, minZ, maxZ);
            }
        }
        return null;
    }

    public static ArrayList<AABB> newAABBGrid(Mesh parent) {
        float minX = minCoord(parent,0);
        float maxX = maxCoord(parent,0);
        float minY = minCoord(parent,1);
        float maxY = maxCoord(parent,1);
        float minZ = minCoord(parent,2);
        float maxZ = maxCoord(parent,2);
        float stepX = (maxX - minX) / parent.getAABBSizeX();
        float stepY = (maxY - minY) / parent.getAABBSizeY();
        float stepZ = (maxZ - minZ) / parent.getAABBSizeZ();
        ArrayList<AABB> grid = new ArrayList<>();
        for (int x = 0; x < parent.getAABBSizeX(); x++) {
            float currentMinX = minX + (stepX * x);
            float currentMaxX = currentMinX + stepX;
            for (int y = 0; y < parent.getAABBSizeY(); y++) {
                float currentMinY = minY + (stepY * y);
                float currentMaxY = currentMinY + stepY;
                for (int z = 0; z < parent.getAABBSizeZ(); z++) {
                    float currentMinZ = minZ + (stepZ * z);
                    float currentMaxZ = currentMinZ + stepZ;
                    AABB aabb = newAABB(parent, currentMinX, currentMaxX, currentMinY, currentMaxY, currentMinZ, currentMaxZ);
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