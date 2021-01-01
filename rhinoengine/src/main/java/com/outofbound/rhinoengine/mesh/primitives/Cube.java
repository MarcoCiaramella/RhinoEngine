package com.outofbound.rhinoengine.mesh.primitives;



import com.outofbound.rhinoengine.mesh.GLMesh;
import com.outofbound.rhinoengine.util.color.Color;

public class Cube extends GLMesh {

    private static float[] vertices = {
            -1,-1,1,
            1,-1,1,
            1,1,1,
            -1,1,1,
            1,-1,1,
            1,-1,-1,
            1,1,-1,
            1,1,1,
            -1,1,1,
            1,1,1,
            1,1,-1,
            -1,1,-1,
            -1,-1,-1,
            -1,-1,1,
            -1,1,1,
            -1,1,-1,
            -1,-1,-1,
            1,-1,-1,
            1,-1,1,
            -1,-1,1,
            1,-1,-1,
            -1,-1,-1,
            -1,1,-1,
            1,1,-1
    };

    private static float[] normals = {
            0,0,1,
            0,0,1,
            0,0,1,
            0,0,1,
            1,0,0,
            1,0,0,
            1,0,0,
            1,0,0,
            0,1,0,
            0,1,0,
            0,1,0,
            0,1,0,
            -1,0,0,
            -1,0,0,
            -1,0,0,
            -1,0,0,
            0,-1,0,
            0,-1,0,
            0,-1,0,
            0,-1,0,
            0,0,-1,
            0,0,-1,
            0,0,-1,
            0,0,-1
    };

    private static int[] indices = {
            0,1,2,
            2,3,0,
            4,5,6,
            6,7,4,
            8,9,10,
            10,11,8,
            12,13,14,
            14,15,12,
            16,17,18,
            18,19,16,
            20,21,22,
            22,23,20
    };

    public Cube(float[] color) {
        super(vertices, 3, normals, indices, Color.getVertexColor(color,vertices.length));
    }

    @Override
    public void doTransformation(float[] mvMatrix) {
    }
}
