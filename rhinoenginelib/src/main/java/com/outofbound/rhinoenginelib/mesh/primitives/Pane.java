package com.outofbound.rhinoenginelib.mesh.primitives;



import com.outofbound.rhinoenginelib.mesh.GLMesh;
import com.outofbound.rhinoenginelib.util.color.Color;

public class Pane extends GLMesh {

    private static final float[] vertices = {
            -1,0,1,
            1,0,1,
            1,0,-1,
            -1,0,-1
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

    public Pane(float[] color) {
        super(vertices, 3, normals, indices, Color.getVertexColor(color,vertices.length));
    }

    @Override
    public void doTransformation(float[] mMatrix, long ms) {
    }
}
