package com.outofbound.rhinoengine.test;

import android.content.Context;

import com.outofbound.rhinoenginelib.mesh.Mesh;
import com.outofbound.rhinoenginelib.util.bitmap.BitmapUtil;

public class TestMesh extends Mesh {

    public TestMesh(Context context){
        //super("test_mesh_2.ply");
        //super("monkey.ply", BitmapUtil.getBitmapFromAsset(context, "monkey_ply_texture.png"));
        super("monkey.obj");
        rotation.x = -90;
    }

    @Override
    public void doTransformation(float[] mMatrix, long ms) {
        rotateX(mMatrix);
    }
}
