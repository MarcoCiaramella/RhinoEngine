package com.outofbound.rhinoengine.renderer;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.outofbound.rhinoengine.camera.GLCamera;
import com.outofbound.rhinoengine.camera.GLCamera2D;
import com.outofbound.rhinoengine.engine.GLEngine;
import com.outofbound.rhinoengine.engine.Loadable;
import com.outofbound.rhinoengine.renderer.util.FrameBuffer;
import com.outofbound.rhinoengine.shader.GLShader;
import com.outofbound.rhinoengine.util.file.TextFileReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Iterator;


public class GLFBO implements Loadable {

    private static float[] vertices = {
            -1f, -1f, //bottom - left
            1f, -1f, //bottom - right
            -1f, 1f, //top - left
            1f, 1f //top - right
    };

    private static float[] textureCoords = {
            1, 1, //bottom - left
            0, 1, // bottom - right
            1, 0, // top - left
            0, 0 // top - right
    };

    private FloatBuffer vertexBuffer;
    private FloatBuffer textureCoordsBuffer;
    // Make sure that dimensions are in POT (Power Of Two), because some devices may not support NPOT textures.
    private int fboWidth;
    private int fboHeight;
    private GLCamera camera;
    private int frameBufferInput;
    private int textureInput;
    private int textureOutput;
    private int renderBufferInput;
    private int programShader;
    private int aPosition;
    private int aTexture;
    private int uTextureId;
    private int uMVPMatrix;
    private float[] mvMatrix = new float[16];
    private float[] mvpMatrix = new float[16];
    private float[] mFBO;
    private ArrayList<GLOnTextureRenderer> onTextureRenderers = new ArrayList<>();
    private long ms;
    private int screenWidth;
    private int screenHeight;


    public GLFBO(int fboWidth, int fboHeight){
        this.fboWidth = fboWidth;
        this.fboHeight = fboHeight;
        // The vertex buffer.
        ByteBuffer bb_vertex = ByteBuffer.allocateDirect(vertices.length * 4);
        bb_vertex.order(ByteOrder.nativeOrder());
        vertexBuffer = bb_vertex.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);
        // The texture coordinates buffer
        ByteBuffer bb_textCoords = ByteBuffer.allocateDirect(textureCoords.length * 4);
        bb_textCoords.order(ByteOrder.nativeOrder());
        textureCoordsBuffer = bb_textCoords.asFloatBuffer();
        textureCoordsBuffer.put(textureCoords);
        textureCoordsBuffer.position(0);
        this.camera = new GLCamera2D();
        setup();
    }

    private void setup(){
        int[] buffers = new int[1];
        //generate fbo id
        GLES20.glGenFramebuffers(1, buffers, 0);
        frameBufferInput = buffers[0];
        GLES20.glGenTextures(1, buffers, 0);
        textureInput = buffers[0];
        GLES20.glGenRenderbuffers(1, buffers, 0);
        renderBufferInput = buffers[0];
        FrameBuffer.create(frameBufferInput, textureInput, renderBufferInput, fboWidth, fboHeight);

        programShader = GLES20.glCreateProgram();

        //compile shaders
        GLES20.glAttachShader(programShader, GLShader.loadShader(GLES20.GL_FRAGMENT_SHADER,
                TextFileReader.getString(GLEngine.getInstance().getContext(), "fs_fbo.glsl")));
        GLES20.glAttachShader(programShader, GLShader.loadShader(GLES20.GL_VERTEX_SHADER,
                TextFileReader.getString(GLEngine.getInstance().getContext(), "vs_fbo.glsl")));
        GLES20.glLinkProgram(programShader);
        // Set our shader program
        GLES20.glUseProgram(programShader);

        aPosition = GLES20.glGetAttribLocation(programShader, "a_position");
        aTexture = GLES20.glGetAttribLocation(programShader, "a_texCoords");
        uTextureId = GLES20.glGetUniformLocation(programShader, "u_texId");
        uMVPMatrix = GLES20.glGetUniformLocation(programShader, "u_mvpMatrix");
    }

    public void renderOnScreen(int textureOutput, float[] m){

        GLES20.glUseProgram(programShader);

        GLES20.glVertexAttribPointer(aPosition, 2, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        GLES20.glEnableVertexAttribArray(aPosition);

        GLES20.glVertexAttribPointer(aTexture, 2, GLES20.GL_FLOAT, false, 0, textureCoordsBuffer);
        GLES20.glEnableVertexAttribArray(aTexture);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureOutput);
        GLES20.glUniform1i(uTextureId, 0);

        Matrix.setIdentityM(mvMatrix, 0);
        Matrix.multiplyMM(mvpMatrix, 0, m, 0, mvMatrix, 0);
        GLES20.glUniformMatrix4fv(uMVPMatrix, 1, false, mvpMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }

    private void renderOnFBO(ArrayList<GLRenderer> renderers, float[] m3D, float[] m2D, long ms, int screenWidth, int screenHeight){
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBufferInput);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT| GLES20.GL_DEPTH_BUFFER_BIT);
        Iterator<GLRenderer> itr = renderers.iterator();
        while (itr.hasNext()) {
            GLRenderer renderer = itr.next();
            if (renderer.isDead(ms)){
                renderer.onRemove();
                itr.remove();
            }
            else {
                renderer.render(m3D, m2D, ms);
            }
        }
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }

    public void render(ArrayList<GLRenderer> renderers, float[] m3D, float[] m2D, long ms, int screenWidth, int screenHeight) {

        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        GLES20.glViewport(0, 0, fboWidth, fboHeight);

        renderOnFBO(renderers,m3D,m2D,ms,screenWidth,screenHeight);
        // use FBO as texture
        mFBO = camera.create(fboWidth, fboHeight, ms);
        textureOutput = textureInput;
        this.ms = ms;
        Iterator<GLOnTextureRenderer> itr = onTextureRenderers.iterator();
        while (itr.hasNext()) {
            GLOnTextureRenderer otr = itr.next();
            textureOutput = otr.render(textureOutput,mFBO,vertexBuffer,textureCoordsBuffer,ms,screenWidth,screenHeight);
            if (otr.isDead()){
                itr.remove();
            }
        }
        // render on screen
        GLES20.glViewport(0, 0, screenWidth, screenHeight);
        renderOnScreen(textureOutput, mFBO);
    }

    public void addGLOnTextureRenderer(GLOnTextureRenderer otr){
        if (!this.onTextureRenderers.contains(otr)) {
            otr.setup(fboWidth,fboHeight);
            this.onTextureRenderers.add(otr);
        }
    }

    @Override
    public boolean load() {
        GLEngine.getInstance().setFBO(this);
        return true;
    }

    @Override
    public void unload() {
        GLEngine.getInstance().setFBO(null);
    }
}
