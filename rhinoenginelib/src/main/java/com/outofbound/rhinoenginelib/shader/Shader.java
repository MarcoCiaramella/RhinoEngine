package com.outofbound.rhinoenginelib.shader;

import android.content.Context;
import android.opengl.GLES20;

import com.outofbound.rhinoenginelib.engine.AbstractEngine;
import com.outofbound.rhinoenginelib.mesh.Mesh;
import com.outofbound.rhinoenginelib.util.file.TextFile;


public abstract class Shader {

    private final int program;
    protected Mesh.ShaderData data;

    public Shader(String vs, String fs){
        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, compile(GLES20.GL_VERTEX_SHADER, AbstractEngine.getInstance().getContext(), vs));
        GLES20.glAttachShader(program, compile(GLES20.GL_FRAGMENT_SHADER, AbstractEngine.getInstance().getContext(), fs));
        GLES20.glLinkProgram(program);
        int[] params = new int[1];
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, params, 0);
        if (params[0] == GLES20.GL_FALSE){
            throw new RuntimeException("Program linking failed:\n"+GLES20.glGetProgramInfoLog(program));
        }
    }

    public abstract void bindData();
    public abstract void unbindData();

    private int compile(int type, Context context, String filename){
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, TextFile.read(context, filename));
        GLES20.glCompileShader(shader);
        int[] params = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, params, 0);
        if (params[0] == GLES20.GL_FALSE){
            throw new RuntimeException("Compilation of shader '" + filename + "' failed:\n"+GLES20.glGetShaderInfoLog(shader));
        }
        return shader;
    }

    public void use(){
        GLES20.glUseProgram(program);
    }

    protected int getAttrib(String name){
        int location = GLES20.glGetAttribLocation(program,name);
        checkLocation(location, name);
        return location;
    }

    protected int getUniform(String name){
        int location = GLES20.glGetUniformLocation(program,name);
        checkLocation(location, name);
        return location;
    }

    private static void checkLocation(int location, String label) {
        if (location < 0) {
            throw new RuntimeException("Unable to locate '" + label + "' in program");
        }
    }

    public Shader setData(Mesh.ShaderData shaderData){
        this.data = shaderData;
        return this;
    }
}
