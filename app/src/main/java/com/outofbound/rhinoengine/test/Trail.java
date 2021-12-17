package com.outofbound.rhinoengine.test;

import com.outofbound.rhinoenginelib.mesh.AABB;
import com.outofbound.rhinoenginelib.mesh.Mesh;
import com.outofbound.rhinoenginelib.object.TrailMesh;

public class Trail extends TrailMesh {

    private static final float[] NEW_NORMAL = {0f, 1f, 0f};

    public Trail(String name, Mesh source, float[] color, int length, long ms) {
        super(name, source, color, length, ms);
    }

    @Override
    protected void newLine(float[][] bbVertices, float[] newLineVertices, float[] newLineNormals) {
        newLineVertices[0] = bbVertices[AABB.VFBL][0];
        newLineVertices[1] = bbVertices[AABB.VFBL][1];
        newLineVertices[2] = bbVertices[AABB.VFBL][2];
        newLineVertices[3] = bbVertices[AABB.VFBR][0];
        newLineVertices[4] = bbVertices[AABB.VFBR][1];
        newLineVertices[5] = bbVertices[AABB.VFBR][2];

        newLineNormals[0] = NEW_NORMAL[0];
        newLineNormals[1] = NEW_NORMAL[1];
        newLineNormals[2] = NEW_NORMAL[2];
        newLineNormals[3] = NEW_NORMAL[0];
        newLineNormals[4] = NEW_NORMAL[1];
        newLineNormals[5] = NEW_NORMAL[2];

    }
}
