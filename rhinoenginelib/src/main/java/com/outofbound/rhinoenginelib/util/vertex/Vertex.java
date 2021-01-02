package com.outofbound.rhinoenginelib.util.vertex;

public class Vertex {

    public static float getMaxZInVertices(float[] vertices){
        float maxZ = vertices[2];
        for (int i = 5; i < vertices.length; i += 3){
            if (vertices[i] > maxZ){
                maxZ = vertices[i];
            }
        }
        return maxZ;
    }

    public static float getVertexZ(float[] vertices, int index){
        return vertices[index*3+2];
    }

    public static float getMaxZInTriangle(float[] vertices, int index1, int index2, int index3){
        float[] z123 = new float[]{
                getVertexZ(vertices,index1),
                getVertexZ(vertices,index2),
                getVertexZ(vertices,index3)
        };
        float maxZ = z123[0];
        for (int i = 1; i < z123.length; i++){
            if (z123[i] > maxZ){
                maxZ = z123[i];
            }
        }
        return maxZ;
    }

    public static float getMinZInTriangle(float[] vertices, int index1, int index2, int index3){
        float[] z123 = new float[]{
                getVertexZ(vertices,index1),
                getVertexZ(vertices,index2),
                getVertexZ(vertices,index3)
        };
        float minZ = z123[0];
        for (int i = 1; i < z123.length; i++){
            if (z123[i] < minZ){
                minZ = z123[i];
            }
        }
        return minZ;
    }

}
