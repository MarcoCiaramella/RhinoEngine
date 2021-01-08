package com.outofbound.rhinoenginelib.shader;

import android.opengl.GLES20;

import com.outofbound.rhinoenginelib.engine.GLEngine;
import com.outofbound.rhinoenginelib.util.file.TextFileReader;


public abstract class GLShader {

    private final int programShader;

    public GLShader(String vs, String fs){
        programShader = GLES20.glCreateProgram();

        GLES20.glAttachShader(programShader, compile(GLES20.GL_FRAGMENT_SHADER,
                TextFileReader.getString(GLEngine.getInstance().getContext(), fs)));
        GLES20.glAttachShader(programShader, compile(GLES20.GL_VERTEX_SHADER,
                TextFileReader.getString(GLEngine.getInstance().getContext(), vs)));
        GLES20.glLinkProgram(programShader);

        GLES20.glUseProgram(programShader);
        config(programShader);
    }

    public abstract void config(int programShader);
    public abstract void bindData();
    public abstract void unbindData();

    private int compile(int type, String shaderCode){
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    public int getProgramShader(){
        return programShader;
    }
}
