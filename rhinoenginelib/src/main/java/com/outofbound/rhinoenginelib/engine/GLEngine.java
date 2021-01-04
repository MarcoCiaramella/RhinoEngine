package com.outofbound.rhinoenginelib.engine;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnTouchListener;

import com.outofbound.rhinoenginelib.camera.GLCamera2D;
import com.outofbound.rhinoenginelib.camera.GLCamera3D;
import com.outofbound.rhinoenginelib.gesture.GLGesture;
import com.outofbound.rhinoenginelib.renderer.GLRenderer;
import com.outofbound.rhinoenginelib.renderer.GLRendererOnTexture;
import com.outofbound.rhinoenginelib.renderer.GLSceneRenderer;
import com.outofbound.rhinoenginelib.renderer.fx.GLBlur;
import com.outofbound.rhinoenginelib.task.GLTask;

import java.util.ArrayList;
import java.util.Calendar;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 * The engine abstract class.
 */
public abstract class GLEngine extends GLSurfaceView implements Renderer, OnTouchListener, ScaleGestureDetector.OnScaleGestureListener {

    private final ArrayList<GLRenderer> glRenderers = new ArrayList<>();
    private final ArrayList<GLTask> glTasks = new ArrayList<>();
    private GLGesture glGesture = null;
    private final float[] clearColor = {0,0,0,1};
    private long ms = -1;
    private GLCamera3D camera3D;
    private GLCamera2D camera2D;
    private float[] mvpMatrix3D;
    private float[] mvpMatrix2D;
    private long deltaMs;
    private ScaleGestureDetector scaleDetector;
    private boolean gestureProcessed = false;
    private static GLEngine instance;
    private GLBlur glBlur = null;
    private GLSceneRenderer glSceneRenderer;
    private boolean blurEnabled = false;



    /**
     * The engine constructor.
     * @param context the context for this view.
     * @param camera3D a 3D camera.
     * @param camera2D a 2D camera.
     * @param gesture a GLGesture.
     */
    public GLEngine(Context context, GLCamera3D camera3D, GLCamera2D camera2D, GLGesture gesture){
        super(context);
        config(camera3D, camera2D, gesture);
    }

    /**
     * The engine constructor.
     * @param context the context for this view.
     * @param attrs the object AttributeSet.
     * @param camera3D a 3D camera.
     * @param camera2D a 2D camera.
     * @param gesture a GLGesture.
     */
    public GLEngine(Context context, AttributeSet attrs, GLCamera3D camera3D, GLCamera2D camera2D, GLGesture gesture){
        super(context,attrs);
        config(camera3D, camera2D, gesture);
    }

    /**
     * Configure this engine.
     * @param camera3D a 3D camera.
     * @param camera2D a 2D camera.
     */
    private void config(GLCamera3D camera3D, GLCamera2D camera2D, GLGesture gesture){
        setSystemUiVisibility(SYSTEM_UI_FLAG_IMMERSIVE | SYSTEM_UI_FLAG_FULLSCREEN);
        setEGLContextClientVersion(2);
        setRenderer(this);
        setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        setOnTouchListener(this);
        this.camera3D = camera3D;
        this.camera2D = camera2D;
        this.glGesture = gesture;
        this.glSceneRenderer = this::renderScene;
        this.scaleDetector = new ScaleGestureDetector(getContext(), this);
        instance = this;
    }

    /**
     * Add a GLRenderer.
     * @param glRenderer the GLRenderer to add.
     * @return the GLRenderer id, -1 if the input GLRenderer is a duplicate.
     */
    public int addGLRenderer(GLRenderer glRenderer){
        if (glRenderers.contains(glRenderer)){
            return -1;
        }
        glRenderer.onAdd();
        glRenderers.add(glRenderer);
        return glRenderers.indexOf(glRenderer);
    }

    /**
     * Add a GLTask.
     * @param glTask the GLTask to add.
     * @return the GLTask id, -1 if the input GLTask is a duplicate.
     */
    public int addGLTask(GLTask glTask){
        if (glTasks.contains(glTask)){
            return -1;
        }
        glTask.onAdd();
        glTasks.add(glTask);
        return glTasks.indexOf(glTask);
    }

    /**
     * Remove the GLRenderer with the specified id.
     * @param id the id of GLRenderer to remove.
     * @return this GLEngine.
     */
    public GLEngine removeGLRenderer(int id){
        if (id < glRenderers.size() && glRenderers.get(id) != null) {
            glRenderers.get(id).onRemove();
            glRenderers.set(id,null);
        }
        return this;
    }

    /**
     * Remove the GLTask with the specified id.
     * @param id the id of GLTask to remove.
     * @return this GLEngine.
     */
    public GLEngine removeGLTask(int id){
        if (id < glTasks.size() && glTasks.get(id) != null) {
            glTasks.get(id).onRemove();
            glTasks.set(id,null);
        }
        return this;
    }

    /**
     * Return the GLRenderer with input id.
     * @param id the id.
     * @return the GLRenderer if exists, null otherwise.
     */
    public GLRenderer getGLRenderer(int id){
        if (id < glRenderers.size()) {
            return glRenderers.get(id);
        }
        return null;
    }

    /**
     * Return GLTask with the input id.
     * @param id the id.
     * @return the GLTask if exists, null otherwise.
     */
    public GLTask getGLTask(int id){
        if (id < glTasks.size()) {
            return glTasks.get(id);
        }
        return null;
    }

    /**
     * Set a GLGesture object.
     * @param glGesture the GLGesture object.
     * @return this engine.
     */
    public GLEngine setGLGesture(GLGesture glGesture){
        this.glGesture = glGesture;
        return this;
    }

    /**
     * Return the GLGesture.
     * @return the GLGesture.
     */
    public GLGesture getGLGesture(){
        return glGesture;
    }

    /**
     * Call super.onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * Call super.onResume()
     */
    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * Always true.
     * @return true.
     */
    @Override
    public boolean performClick(){
        return true;
    }

    private boolean checkMotionEvent(MotionEvent event){
        int numPointer = event.getPointerCount();
        int pointerIndex = event.getActionIndex();
        if (pointerIndex >= numPointer){
            return false;
        }
        int pointerId = event.getPointerId(pointerIndex);
        return pointerId < numPointer;
    }

    /**
     * Handle on touch events.
     * @param v the view touched.
     * @param event the MotionEvent object.
     * @return true if the event was handled, false otherwise.
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (glGesture != null) {
            if (!checkMotionEvent(event)){
                return true;
            }
            // pause
            v.performClick();
            int pointerIndex = event.getActionIndex();
            int action = event.getActionMasked();
            int pointerId = event.getPointerId(pointerIndex);
            float posX = event.getX(pointerId);
            float posY = getHeight() - event.getY(pointerId); //inverted in GL surface
            scaleDetector.onTouchEvent(event);
            if (gestureProcessed){
                gestureProcessed = false;
                return true;
            }
            switch (action) {
                case MotionEvent.ACTION_MOVE: {
                    VelocityTracker vt = VelocityTracker.obtain();
                    vt.addMovement(event);
                    vt.computeCurrentVelocity(1);
                    float velX = vt.getXVelocity();
                    float velY = vt.getYVelocity();
                    glGesture.onMove(posX, posY, velX, velY);
                    //Return a VelocityTracker object back to be re-used by others. You must not touch the object after calling this function.
                    vt.recycle();
                }
                break;
                case MotionEvent.ACTION_DOWN: {
                    glGesture.onClick(posX, posY);
                }
                break;
                case MotionEvent.ACTION_UP: {
                    glGesture.onRelease(posX, posY);
                }
                break;
                default: {
                    return v.onTouchEvent(event);
                }
            }
        }
        return true;
    }

    /**
     * Scale event handler.
     * @param detector the scale detector object.
     * @return true.
     */
    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        if (glGesture != null) {
            glGesture.onScale(detector.getScaleFactor());
            gestureProcessed = true;
        }
        invalidate();
        return true;
    }

    /**
     * Scale begin handler.
     * @param detector the scale detector object.
     * @return true.
     */
    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        gestureProcessed = true;
        return true;
    }

    /**
     * Scale end handler.
     * @param detector the scale detector object.
     */
    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        gestureProcessed = true;
    }

    /**
     * Prepare engine for rendering.
     * @param gl the OpenGL 1.0 object.
     * @param config the EGLConfig object.
     */
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glDepthFunc(GLES20.GL_LESS);
        GLES20.glClearDepthf(1.0f);
        GLES20.glFrontFace(GLES20.GL_CCW);

        init();
    }

    /**
     * Change viewport when surface is changed.
     * @param unused unused.
     * @param width new surface width.
     * @param height new surface height.
     */
    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    /**
     * Draw the OpenGL frame.
     * @param gl the OpenGL 1.0 object.
     */
    @Override
    public void onDrawFrame(GL10 gl) {

        GLES20.glClearColor(clearColor[0], clearColor[1], clearColor[2], clearColor[3]);
        // clear Screen and Depth Buffer
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // get ms
        deltaMs = 0;
        long currentMs = Calendar.getInstance().getTimeInMillis();
        if (ms >= 0) {
            deltaMs = currentMs - ms;
        }

        for (GLRenderer glRenderer : glRenderers){
            glRenderer.move(deltaMs);
        }

        mvpMatrix3D = camera3D.create(getWidth(), getHeight(), deltaMs);
        mvpMatrix2D = camera2D.create(getWidth(), getHeight(), deltaMs);


        if (glBlur != null && blurEnabled){
            glBlur.render(getWidth(),getHeight(),deltaMs);
        }
        else {
            glSceneRenderer.doRendering();
        }

        for (GLTask glTask : glTasks){
            boolean alive = glTask.runTask(deltaMs);
            if (!alive) {
                removeGLTask(glTasks.indexOf(glTask));
            }
        }

        ms = currentMs;

    }

    private void renderScene(){
        for (GLRenderer glRenderer : glRenderers){
            glRenderer.render(mvpMatrix3D, mvpMatrix2D, deltaMs);
        }
    }

    /**
     * Set the clear color for GLES20.glClearColor method.
     * @param r red.
     * @param g green.
     * @param b blue.
     * @param a alpha.
     */
    public synchronized void setClearColor(float r, float g, float b, float a){
        this.clearColor[0] = r;
        this.clearColor[1] = g;
        this.clearColor[2] = b;
        this.clearColor[3] = a;
    }

    /**
     * Return the 3D camera.
     * @return the 3D camera.
     */
    public GLCamera3D getCamera3D(){
        return camera3D;
    }

    /**
     * Get this GLEngine.
     * @return the instance.
     */
    public static GLEngine getInstance(){
        return instance;
    }

    /**
     * Initialize the engine.
     */
    protected abstract void init();

    /**
     * Set blur effect.
     * @param resolution the resolution. Must be GLRendererOnTexture.RESOLUTION_256, GLRendererOnTexture.RESOLUTION_512 or GLRendererOnTexture.RESOLUTION_1024.
     * @param scale the scale.
     * @param amount the amount.
     * @param strength the strength.
     * @return this GLEngine.
     */
    public GLEngine setBlur(int resolution, float scale, float amount, float strength){
        glBlur = new GLBlur(new GLRendererOnTexture(glSceneRenderer,resolution),scale,amount,strength).setup();
        return this;
    }

    /**
     * Enable blur effect.
     * @return this GLEngine.
     */
    public GLEngine enableBlur(){
        blurEnabled = true;
        return this;
    }

    /**
     * Disable blur effect.
     * @return this GLEngine.
     */
    public GLEngine disableBlur(){
        blurEnabled = false;
        return this;
    }

    /**
     * Return frame rate.
     * @return frame rate.
     */
    public int getFPS(){
        if (deltaMs == 0){
            return 0;
        }
        return (int) (1f/(deltaMs/1000f));
    }

}
