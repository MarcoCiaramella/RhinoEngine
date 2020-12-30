package com.outofbound.rhinoengine.renderer;

import java.nio.FloatBuffer;

public abstract class GLOnTextureRenderer {

    private boolean dead;

    public GLOnTextureRenderer(){
        dead = false;
    }

    public boolean isDead(){
        return dead;
    }

    public void destroy(){
        dead = true;
    }

    public abstract int render(int textureInput, float[] mFBO, FloatBuffer vertexBuffer, FloatBuffer textureCoordsBuffer, long ms, int screenWidth, int screenHeight);
    public abstract void setup(int fboWidth, int fboHeight);
}
