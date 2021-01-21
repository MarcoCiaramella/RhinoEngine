package com.outofbound.rhinoenginelib.shader;

import android.opengl.GLES20;

import com.outofbound.rhinoenginelib.engine.AbstractEngine;
import com.outofbound.rhinoenginelib.util.file.TextFileReader;


public abstract class Shader {

    private final int program;

    public Shader(String vs, String fs){
        program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, compile(GLES20.GL_VERTEX_SHADER,
                TextFileReader.getString(AbstractEngine.getInstance().getContext(), vs)));
        GLES20.glAttachShader(program, compile(GLES20.GL_FRAGMENT_SHADER,
                TextFileReader.getString(AbstractEngine.getInstance().getContext(), fs)));
        GLES20.glLinkProgram(program);
    }

    public abstract void bindData();
    public abstract void unbindData();

    private int compile(int type, String source){
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, source);
        GLES20.glCompileShader(shader);
        return shader;
    }

    public int getProgram(){
        return program;
    }

    protected int getAttrib(String name){
        return GLES20.glGetAttribLocation(program,name);
    }

    protected int getUniform(String name){
        return GLES20.glGetUniformLocation(program,name);
    }
}
