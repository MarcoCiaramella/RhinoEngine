package com.outofbound.rhinoenginelib.collision;


import com.outofbound.rhinoenginelib.util.vector.Vector3f;

public class Collision {

    public static boolean pointVsAABB(Vector3f point, Vector3f[] aabb){
        return (point.x >= aabb[0].x && point.x <= aabb[1].x) &&
                (point.y >= aabb[0].y && point.y <= aabb[1].y) &&
                (point.z >= aabb[0].z && point.z <= aabb[1].z);
    }
}
