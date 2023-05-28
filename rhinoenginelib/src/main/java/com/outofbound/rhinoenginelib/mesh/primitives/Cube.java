package com.outofbound.rhinoenginelib.mesh.primitives;



import com.outofbound.rhinoenginelib.mesh.Mesh;
import com.outofbound.rhinoenginelib.util.color.Color;

public class Cube extends Mesh {

    private static final float[] vertices = {
            -0.5f,-0.5f,0.5f,
            0.5f,-0.5f,0.5f,
            0.5f,0.5f,0.5f,
            -0.5f,0.5f,0.5f,
            0.5f,-0.5f,0.5f,
            0.5f,-0.5f,-0.5f,
            0.5f,0.5f,-0.5f,
            0.5f,0.5f,0.5f,
            -0.5f,0.5f,0.5f,
            0.5f,0.5f,0.5f,
            0.5f,0.5f,-0.5f,
            -0.5f,0.5f,-0.5f,
            -0.5f,-0.5f,-0.5f,
            -0.5f,-0.5f,0.5f,
            -0.5f,0.5f,0.5f,
            -0.5f,0.5f,-0.5f,
            -0.5f,-0.5f,-0.5f,
            0.5f,-0.5f,-0.5f,
            0.5f,-0.5f,0.5f,
            -0.5f,-0.5f,0.5f,
            0.5f,-0.5f,-0.5f,
            -0.5f,-0.5f,-0.5f,
            -0.5f,0.5f,-0.5f,
            0.5f,0.5f,-0.5f
    };

    private static final float[] normals = {
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

    private static final int[] indices = {
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

    private Cube(String name, float[] vertices, float[] color){
        super(name, vertices, normals, indices, Color.getVertexColor(color,vertices.length));
    }

    public Cube(String name, float[] color) {
        this(name, vertices, color);
    }

    public Cube(String name, float sizeX, float sizeY, float sizeZ, float[] color) {
        this(name, scale(vertices,sizeX,sizeY,sizeZ), color);
    }
}
