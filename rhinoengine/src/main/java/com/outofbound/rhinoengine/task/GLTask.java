package com.outofbound.rhinoengine.task;

import androidx.annotation.CallSuper;

import com.outofbound.rhinoengine.engine.GLEngine;
import com.outofbound.rhinoengine.engine.Loadable;

public abstract class GLTask implements Loadable {

    private final int id;
    private boolean dead;

    /**
     * Instantiate a GLTask.
     * @param id the id.
     */
    public GLTask(int id){
        this.id = id;
        dead = false;
    }

    /**
     * Return the id of this GLTask.
     * @return the id.
     */
    public int getId(){
        return id;
    }

    /**
     * Load this GLTask. It is added to GLEngine.
     * @return true if loaded, false otherwise.
     */
    @Override
    public boolean load() {
        return GLEngine.getInstance().addGLTask(this);
    }

    /**
     * Unload this GLTask. It will be removed from GLEngine.
     */
    @Override
    public void unload() {
        kill();
    }

    /**
     * Called when this GLTask is added to GLEngine.
     */
    @CallSuper
    public void onAdd(){
        dead = false;
    }

    /**
     * Called when this GLTask is removed from GLEngine.
     */
    @CallSuper
    public void onRemove(){
        dead = true;
    }

    /**
     * Run sync task.
     * @param ms the engine time in milliseconds.
     * @return true if alive, false otherwise.
     */
    public abstract boolean runTask(long ms);

    /**
     * Kill this GLTask. It will be removed from GLEngine.
     */
    public void kill(){
        dead = true;
    }

    /**
     * Return true if this GLTask is dead.
     * @return true if this GLTask is dead, false otherwise.
     */
    public boolean isDead(){
        return dead;
    }

}
