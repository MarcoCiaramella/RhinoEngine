package com.outofbound.rhinoengine.test;

import com.outofbound.rhinoenginelib.mesh.BoundingBox;
import com.outofbound.rhinoenginelib.mesh.Mesh;
import com.outofbound.rhinoenginelib.object.TrailMesh;

public class Trail extends TrailMesh {

    private static final float[] NEW_NORMAL = {0f, 1f, 0f};

    public Trail(String name, Mesh source, float[] color, int length, long ms) {
        super(name, source, color, length, ms);
    }

    @Override
    protected void newLine(float[][] bbVertices, float[] newLineVertices, float[] newLineNormals) {
        newLineVertices[0] = bbVertices[BoundingBox.VFBL][0];
        newLineVertices[1] = bbVertices[BoundingBox.VFBL][1];
        newLineVertices[2] = bbVertices[BoundingBox.VFBL][2];
        newLineVertices[3] = bbVertices[BoundingBox.VFBR][0];
        newLineVertices[4] = bbVertices[BoundingBox.VFBR][1];
        newLineVertices[5] = bbVertices[BoundingBox.VFBR][2];

        newLineNormals[0] = NEW_NORMAL[0];
        newLineNormals[1] = NEW_NORMAL[1];
        newLineNormals[2] = NEW_NORMAL[2];
        newLineNormals[3] = NEW_NORMAL[0];
        newLineNormals[4] = NEW_NORMAL[1];
        newLineNormals[5] = NEW_NORMAL[2];

    }
}
