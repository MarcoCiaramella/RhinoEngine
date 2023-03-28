package com.outofbound.rhinoenginelib.collision;

import com.lib.joctree.math.Octree;
import com.lib.joctree.math.Vector3;
import com.lib.joctree.math.collision.BoundingBox;
import com.lib.joctree.math.collision.Ray;
import com.lib.joctree.utils.Array;
import com.lib.joctree.utils.ObjectSet;
import com.outofbound.rhinoenginelib.mesh.AABB;

import java.util.ArrayList;

public class Collider {

    private static final int MAX_DEPTH = 8;
    private static final int MAX_ITEMS_PER_NODE = 8;
    private static final Vector3 MIN = new Vector3(-1000, -1000, -1000);
    private static final Vector3 MAX = new Vector3(1000, 1000, 1000);
    private static final ObjectSet<AABB> result = new ObjectSet<>();
    private static final ObjectSet<AABB> all = new ObjectSet<>();
    private static final Octree<AABB> octree = new Octree<>(MIN, MAX, MAX_DEPTH, MAX_ITEMS_PER_NODE, new Octree.Collider<AABB>() {
        @Override
        public boolean intersects(BoundingBox nodeBounds, AABB aabb) {
            return nodeBounds.intersects(aabb.getBoundingBox());
        }

        @Override
        public float intersects(Ray ray, AABB aabb) {
            return Float.MAX_VALUE;
        }
    });

    public static void add(ArrayList<AABB> aabbs) {
        for (AABB aabb : aabbs) {
            octree.add(aabb);
        }
    }

    public static ArrayList<AABB> query(AABB aabb) {
        octree.query(aabb.getBoundingBox(), result);
        if (result.size > 0) {
            ArrayList<AABB> aabbs = new ArrayList<>();
            for (AABB a : result) {
                aabbs.add(a);
            }
            return aabbs;
        }
        return null;
    }

    public static void clear() {
        for (AABB aabb : octree.getAll(all)) {
            octree.remove(aabb);
        }
    }
}
