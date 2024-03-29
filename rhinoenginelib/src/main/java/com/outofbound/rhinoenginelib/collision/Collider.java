package com.outofbound.rhinoenginelib.collision;

import com.lib.joctree.math.Octree;
import com.lib.joctree.math.Vector3;
import com.lib.joctree.math.collision.BoundingBox;
import com.lib.joctree.math.collision.Ray;
import com.lib.joctree.utils.ObjectSet;
import com.outofbound.rhinoenginelib.mesh.AABB;
import com.outofbound.rhinoenginelib.mesh.Mesh;
import com.outofbound.rhinoenginelib.util.vector.Vector3f;


public class Collider {

    private static final ObjectSet<AABB> result = new ObjectSet<>();
    private static Octree<AABB> octree;

    public static void build(Vector3f min, Vector3f max, int maxDepth, int maxItemsPerNode) {
        octree = new Octree<>(new Vector3(min.x, min.y, min.z), new Vector3(max.x, max.y, max.z), maxDepth, maxItemsPerNode, new Octree.Collider<AABB>() {
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
        if (octree != null) {
            for (AABB aabb : mesh.getAABBGrid()) {
                octree.update(aabb);
            }
        }
    }

    public static void processCollision(Mesh mesh) {
        if (octree != null) {
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
}
