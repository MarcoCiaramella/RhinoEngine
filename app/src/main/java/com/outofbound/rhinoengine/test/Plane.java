package com.outofbound.rhinoengine.test;

import androidx.annotation.NonNull;

import com.outofbound.rhinoenginelib.mesh.Mesh;

class Plane extends Mesh {


    public Plane(@NonNull String mesh) {
        super("Plane", mesh);
    }

    @Override
    public void beforeRendering(long ms) {

    }

    @Override
    public void afterRendering(long ms) {

    }
}