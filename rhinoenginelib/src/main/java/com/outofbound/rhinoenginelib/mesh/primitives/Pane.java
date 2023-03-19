package com.outofbound.rhinoenginelib.mesh.primitives;



import com.outofbound.rhinoenginelib.mesh.Mesh;
import com.outofbound.rhinoenginelib.util.color.Color;

public class Pane extends Mesh {

    private static final float[] vertices = {
            -0.5f,0,0.5f,
            0.5f,0,0.5f,
            0.5f,0,-0.5f,
            -0.5f,0,-0.5f
    };

    private static final float[] normals = {
            0,1,0,
            0,1,0,
            0,1,0,
            0,1,0
    };

    private static final int[] indices = {
            0,1,2,
            2,3,0
    };

    private Pane(String name, float[] vertices, float[] color){
        super(name, vertices, 3, normals, indices, Color.getVertexColor(color,vertices.length));
    }

    public Pane(String name, float[] color) {
        this(name, vertices,color);
    }

    public Pane(String name, float sizeX, float sizeZ, float[] color) {
        this(name, scale(vertices,sizeX,1,sizeZ), color);
    }
}
