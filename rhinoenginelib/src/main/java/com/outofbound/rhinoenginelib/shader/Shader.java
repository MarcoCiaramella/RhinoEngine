package com.outofbound.rhinoenginelib.shader;

import android.opengl.GLES30;

import com.outofbound.rhinoenginelib.engine.AbstractEngine;
import com.outofbound.rhinoenginelib.util.file.TextFile;


public abstract class Shader {

    private final int program;

    public Shader(String vs, String fs){
        program = GLES30.glCreateProgram();
        GLES30.glAttachShader(program, compile(GLES30.GL_VERTEX_SHADER,
                TextFile.read(AbstractEngine.getInstance().getContext(), vs)));
        GLES30.glAttachShader(program, compile(GLES30.GL_FRAGMENT_SHADER,
                TextFile.read(AbstractEngine.getInstance().getContext(), fs)));
        GLES30.glLinkProgram(program);
    }

    public abstract void bindData();
    public abstract void unbindData();

    private int compile(int type, String source){
        int shader = GLES30.glCreateShader(type);
        GLES30.glShaderSource(shader, source);
        GLES30.glCompileShader(shader);
        return shader;
    }

    public int getProgram(){
        return program;
    }

    protected int getAttrib(String name){
        return GLES30.glGetAttribLocation(program,name);
    }

    protected int getUniform(String name){
        return GLES30.glGetUniformLocation(program,name);
    }
}
