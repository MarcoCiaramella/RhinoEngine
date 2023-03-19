package com.outofbound.rhinoenginelib.collision;

import com.lib.joctree.math.Octree;
import com.lib.joctree.math.Vector3;
import com.lib.joctree.math.collision.BoundingBox;
import com.lib.joctree.math.collision.Ray;
import com.lib.joctree.utils.Array;
import com.lib.joctree.utils.ObjectSet;
import com.outofbound.rhinoenginelib.mesh.Mesh;
import com.outofbound.rhinoenginelib.util.map.SyncMap;

import java.util.ArrayList;

public class Collider {

    private static final int MAX_DEPTH = 8;
    private static final int MAX_ITEMS_PER_NODE = 200;
    private static final Vector3 MIN = new Vector3(-1000, -1000, -1000);
    private static final Vector3 MAX = new Vector3(1000, 1000, 1000);
    private static final ObjectSet<Mesh> result = new ObjectSet<>();
    private static final ObjectSet<Mesh> all = new ObjectSet<>();
    private static final Octree<Mesh> octree = new Octree<>(MIN, MAX, MAX_DEPTH, MAX_ITEMS_PER_NODE, new Octree.Collider<Mesh>() {
        @Override
        public boolean intersects(BoundingBox nodeBounds, Mesh mesh) {
            return nodeBounds.intersects(mesh.getAABB().getBoundingBox());
        }

        @Override
        public float intersects(Ray ray, Mesh mesh) {
            return Float.MAX_VALUE;
        }
    });

    public static void add(Mesh mesh) {
        octree.add(mesh);
    }

    public static Mesh[] query(Mesh mesh) {
        octree.query(mesh.getAABB().getBoundingBox(), result);
        Mesh[] meshes = new Mesh[result.size];
        int i = 0;
        for (Mesh m : result) {
            meshes[i++] = m;
        }
        return meshes;
    }

    public static void clear() {
        for (Mesh mesh : octree.getAll(all)) {
            octree.remove(mesh);
        }
    }
}
