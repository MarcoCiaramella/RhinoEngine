package com.outofbound.rhinoengine.test;

import com.outofbound.rhinoenginelib.mesh.Mesh;

public class TestMesh extends Mesh {

    public TestMesh(){
        super("test_mesh_2.ply");
        position.y = -1.8f;
    }

    @Override
    public void doTransformation(float[] mMatrix, long ms) {
        translate(mMatrix);
    }
}
