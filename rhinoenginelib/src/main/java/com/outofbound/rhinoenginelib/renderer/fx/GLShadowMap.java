package com.outofbound.rhinoenginelib.renderer.fx;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.outofbound.rhinoenginelib.camera.GLCamera;
import com.outofbound.rhinoenginelib.engine.GLEngine;
import com.outofbound.rhinoenginelib.renderer.GLOnTextureRenderer;
import com.outofbound.rhinoenginelib.renderer.util.Framebuffer;
import com.outofbound.rhinoenginelib.shader.GLShader;
import com.outofbound.rhinoenginelib.util.file.TextFileReader;
import com.outofbound.rhinoenginelib.util.vector.Vector3f;

import java.nio.FloatBuffer;

public class GLShadowMap extends GLOnTextureRenderer {

    private int frameBuffer;
    private int depthTexture;
    private GLCamera camera;
    private int programShader;
    private int uMVPMatrix;
    private int aPosition;


    public GLShadowMap(Vector3f lightDir){
        this.camera = new Camera(lightDir);
    }

    @Override
    public int render(int textureInput, FloatBuffer vertexBuffer, FloatBuffer textureCoordsBuffer, long ms, int fboWidth, int fboHeight) {
        float[] m = camera.create(fboWidth, fboHeight, ms);
        return 0;
    }

    @Override
    public void setup(int fboWidth, int fboHeight) {
        int[] buffers = new int[1];

        GLES20.glGenFramebuffers(1, buffers, 0);
        frameBuffer = buffers[0];

        GLES20.glGenTextures(1, buffers, 0);
        depthTexture = buffers[0];

        GLES20.glGenRenderbuffers(1, buffers, 0);
        int renderBuffer = buffers[0];

        Framebuffer.create(frameBuffer,depthTexture,renderBuffer,fboWidth,fboHeight);

        programShader = GLES20.glCreateProgram();
        //compile shaders
        GLES20.glAttachShader(programShader, GLShader.loadShader(GLES20.GL_FRAGMENT_SHADER,
                TextFileReader.getString(GLEngine.getInstance().getContext(), "fs_shadow_map.glsl")) );
        GLES20.glAttachShader(programShader, GLShader.loadShader(GLES20.GL_VERTEX_SHADER,
                TextFileReader.getString(GLEngine.getInstance().getContext(), "vs_shadow_map.glsl")) );
        GLES20.glLinkProgram(programShader);
        // Set our shader program
        GLES20.glUseProgram(programShader);
        uMVPMatrix = GLES20.glGetUniformLocation(programShader, "uMVPMatrix");
        aPosition = GLES20.glGetAttribLocation(programShader, "aPosition");
    }

    private static class Camera extends GLCamera {

        private final float near;
        private final float far;

        public Camera(Vector3f lightDir, float near, float far){
            super(lightDir,new Vector3f(0,-1,0),new Vector3f(0,0,0));
            this.near = near;
            this.far = far;
        }

        public Camera(Vector3f lightDir){
            this(lightDir,1,100);
        }

        @Override
        public float[] create(int width, int height, long ms) {
            float ratio = (float)width/(float)height;
            Matrix.orthoM(projectionMatrix, 0, -ratio, ratio, -ratio, ratio, near, far);
            setupM();
            return mvpMatrix;
        }
    }
}
