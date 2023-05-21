package com.outofbound.rhinoenginelib.collision;

import com.lib.joctree.math.Octree;
import com.lib.joctree.math.Vector3;
import com.lib.joctree.math.collision.BoundingBox;
import com.lib.joctree.math.collision.Ray;
import com.lib.joctree.utils.ObjectSet;
import com.outofbound.rhinoenginelib.mesh.AABB;
import com.outofbound.rhinoenginelib.mesh.Mesh;


public class Collider {

    private static final int MAX_DEPTH = 5;
    private static final int MAX_ITEMS_PER_NODE = 16;
    private static final Vector3 MIN = new Vector3(-1, -1, -1);
    private static final Vector3 MAX = new Vector3(1, 1, 1);
    private static final ObjectSet<AABB> result = new ObjectSet<>();
    private static Octree<AABB> octree;

    public static void build() {
        octree = new Octree<>(MIN, MAX, MAX_DEPTH, MAX_ITEMS_PER_NODE, new Octree.Collider<AABB>() {
            @Override
            public boolean intersects(BoundingBox nodeBounds, AABB aabb) {
                return nodeBounds.intersects(aabb.getBoundingBox());
            }

            @Override
            public float intersects(Ray ray, AABB aabb) {
                return Float.MAX_VALUE;
            }
        });
    }

    public static void update(Mesh mesh) {
        for (AABB aabb : mesh.getAABBGrid()) {
            octree.update(aabb);
        }
    }

    public static void processCollision(Mesh mesh) {
        for (AABB aabb : mesh.getAABBGrid()) {
            octree.query(aabb.getBoundingBox(), result);
            for (AABB aabb2 : result) {
                if (aabb.getParent() != aabb2.getParent() && aabb.getBoundingBox().intersects(aabb2.getBoundingBox())) {
                    mesh.onCollision(aabb2);
                }
            }
        }
    }
}
