package com.outofbound.rhinoengine.test;

import android.content.Context;

import com.outofbound.rhinoenginelib.mesh.Mesh;
import com.outofbound.rhinoenginelib.util.bitmap.BitmapUtil;

public class TestMesh extends Mesh {

    public TestMesh(Context context){
        //super("test_mesh_2.ply");
        super("test_textured_mesh.ply", BitmapUtil.getBitmapFromAsset(context,"Material.001_Base_Color.png"));
        position.y = -1.8f;
    }

    @Override
    public void doTransformation(float[] mMatrix, long ms) {
        translate(mMatrix);
    }
}
