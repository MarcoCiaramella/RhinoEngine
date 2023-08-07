package com.outofbound.rhinoenginelib.mesh;

import com.lib.joctree.math.Matrix4;
import com.lib.joctree.math.Vector3;
import com.lib.joctree.math.collision.BoundingBox;
import com.outofbound.rhinoenginelib.util.triangle.Triangle;
import com.outofbound.rhinoenginelib.util.vector.Vector3f;

import java.nio.FloatBuffer;
import java.util.ArrayList;


public class AABB {

    private final BoundingBox boundingBox;
    private final Matrix4 m4 = new Matrix4();
    private final Mesh parent;
    private final ArrayList<Triangle> triangles = new ArrayList<>();

    private AABB(Mesh parent, float minX, float maxX, float minY, float maxY, float minZ, float maxZ) {
        boundingBox = new BoundingBox(new Vector3(minX, minY, minZ), new Vector3(maxX, maxY, maxZ));
        this.parent = parent;
        loadTriangles();
    }

    private void loadTriangles() {
        for (Triangle triangle : parent.getTriangles()) {
            if (boundingBox.contains(cast(triangle.v1)) || boundingBox.contains(cast(triangle.v2)) || boundingBox.contains(cast(triangle.v3)) ) {
                triangles.add(triangle);
            }
        }
    }

    private Vector3 cast(Vector3f v) {
        return new Vector3(v.x, v.y, v.z);
    }

    public Mesh getParent() {
        return parent;
    }

    private static float minCoord(Mesh parent, int coord){
        float min = parent.getShaderData().get(0).vertexBuffer.get(coord);
        for (Mesh.ShaderData shaderData : parent.getShaderData()) {
            for (int i = 0; i < shaderData.vertexBuffer.capacity(); i += 3) {
                min = Math.min(shaderData.vertexBuffer.get(i + coord), min);
            }
        }
        return min;
    }

    private static float maxCoord(Mesh parent, int coord){
        float max = parent.getShaderData().get(0).vertexBuffer.get(coord);
        for (Mesh.ShaderData shaderData : parent.getShaderData()) {
            for (int i = 0; i < shaderData.vertexBuffer.capacity(); i += 3) {
                max = Math.max(shaderData.vertexBuffer.get(i + coord), max);
            }
        }
        return max;
    }

    public void mul(float[] mMatrix){
        boundingBox.mul(m4.set(mMatrix));
    }

    private static AABB newAABB(Mesh parent, float minX, float maxX, float minY, float maxY, float minZ, float maxZ) {
        // check if at least one vertex is in the bounds
        for (Mesh.ShaderData shaderData : parent.getShaderData()) {
            for (int i = 0; i < shaderData.vertexBuffer.capacity(); i += 3) {
                float x = shaderData.vertexBuffer.get(i);
                float y = shaderData.vertexBuffer.get(i + 1);
                float z = shaderData.vertexBuffer.get(i + 2);
                if (x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ) {
                    return new AABB(parent, minX, maxX, minY, maxY, minZ, maxZ);
                }
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