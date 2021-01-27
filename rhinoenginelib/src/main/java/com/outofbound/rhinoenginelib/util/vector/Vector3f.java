package com.outofbound.rhinoenginelib.util.vector;

import android.opengl.Matrix;
import android.util.Log;

import java.io.Serializable;


public class Vector3f implements Serializable {

    public float x,y,z;
    private float[] vector4f;
    private float[] m;
    private Vector3f res;

    public Vector3f(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
        vector4f = new float[4];
        m = new float[16];
        res = new Vector3f();
    }

    public Vector3f(float[] v){
        this(v[0],v[1],v[2]);
    }

    private Vector3f(){}

    public Vector3f(Vector3f v){
        this(v.x,v.y,v.z);
    }

    public Vector3f multS(float s, Vector3f res){
        res.x = x*s;
        res.y = y*s;
        res.z = z*s;
        return res;
    }

    public Vector3f multS(float s){
        x *= s;
        y *= s;
        z *= s;
        return this;
    }

    public Vector3f cross(Vector3f v, Vector3f res){
        float crossX = y * v.z - z * v.y;
        float crossY = z * v.x - x * v.z;
        float crossZ = x * v.y - y * v.x;
        res.x = crossX;
        res.y = crossY;
        res.z = crossZ;
        return res;
    }

    public Vector3f cross(Vector3f v){
        float crossX = y * v.z - z * v.y;
        float crossY = z * v.x - x * v.z;
        float crossZ = x * v.y - y * v.x;
        x = crossX;
        y = crossY;
        z = crossZ;
        return this;
    }

    public float dot(Vector3f v){
        return x * v.x + y * v.y + z * v.z;
    }

    public Vector3f normalize(){
        float length = length();
        x /= length;
        y /= length;
        z /= length;
        return this;
    }

    public Vector3f normalize(Vector3f res){
        float length = length();
        res.x = x/length;
        res.y = y/length;
        res.z = z/length;
        return res;
    }

    private void to4f(){
        vector4f[0] = x;
        vector4f[1] = y;
        vector4f[2] = z;
        vector4f[3] = 1;
    }

    private void to3f(){
        x = vector4f[0];
        y = vector4f[1];
        z = vector4f[2];
    }

    public Vector3f multiplyMV(float[] m, Vector3f res){
        to4f();
        Matrix.multiplyMV(res.vector4f,0, m,0,vector4f,0);
        res.to3f();
        return res;
    }

    public float[] multiplyMV4f(float[] m, Vector3f res){
        to4f();
        Matrix.multiplyMV(res.vector4f,0, m,0,vector4f,0);
        res.to3f();
        return res.vector4f;
    }

    public Vector3f rotate(float a, float x, float y, float z){
        Matrix.setIdentityM(m,0);
        Matrix.rotateM(m,0,a,x,y,z);
        multiplyMV(m,this);
        return this;
    }

    public Vector3f rotate(float a, float x, float y, float z, Vector3f res){
        Matrix.setIdentityM(m,0);
        Matrix.rotateM(m,0,a,x,y,z);
        res.copy(this);
        multiplyMV(m,res);
        return res;
    }

    public Vector3f translate(Vector3f shift){
        Matrix.setIdentityM(m,0);
        Matrix.translateM(m,0,shift.x,shift.y,shift.z);
        multiplyMV(m,this);
        return this;
    }

    public Vector3f translate(Vector3f shift, Vector3f res){
        Matrix.setIdentityM(m,0);
        Matrix.translateM(m,0,shift.x,shift.y,shift.z);
        res.copy(this);
        multiplyMV(m,res);
        return res;
    }

    public float length(){
        return (float) Math.sqrt(x*x + y*y + z*z);
    }

    public Vector3f add(Vector3f v, Vector3f res){
        res.x = x + v.x;
        res.y = y + v.y;
        res.z = z + v.z;
        return res;
    }

    public Vector3f add(Vector3f v){
        x += v.x;
        y += v.y;
        z += v.z;
        return this;
    }

    public Vector3f sub(Vector3f v, Vector3f res){
        res.x = x - v.x;
        res.y = y - v.y;
        res.z = z - v.z;
        return res;
    }

    public Vector3f sub(Vector3f v){
        x -= v.x;
        y -= v.y;
        z -= v.z;
        return this;
    }

    public float distance(Vector3f v){
        res.x = x-v.x;
        res.y = y-v.y;
        res.z = z-v.z;
        return res.length();
    }

    public Vector3f copy(Vector3f v){
        x = v.x;
        y = v.y;
        z = v.z;
        return this;
    }

    public void print(String tag){
        Log.i(tag,"x "+x);
        Log.i(tag,"y "+y);
        Log.i(tag,"z "+z);
    }
}
