package com.outofbound.rhinoengine.test;


import com.outofbound.rhinoenginelib.mesh.Mesh;

public class MeshObj extends Mesh {

    public MeshObj(){
        super("monkey.obj");
    }

    @Override
    public void doTransformation(float[] mMatrix, long ms) {
    }
}
