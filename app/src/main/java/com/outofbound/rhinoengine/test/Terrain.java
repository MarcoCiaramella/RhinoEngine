package com.outofbound.rhinoengine.test;

import androidx.annotation.NonNull;

import com.outofbound.rhinoenginelib.mesh.Mesh;

class Terrain extends Mesh {


    public Terrain(@NonNull String mesh) {
        super("Terrain", mesh);
        enableCollision(16, 16, 16);
    }
}
