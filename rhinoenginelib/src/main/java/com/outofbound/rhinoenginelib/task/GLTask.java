package com.outofbound.rhinoenginelib.task;

import androidx.annotation.CallSuper;


public abstract class GLTask {

    private boolean dead;

    /**
     * Instantiate a GLTask.
     */
    public GLTask(){
        dead = false;
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
