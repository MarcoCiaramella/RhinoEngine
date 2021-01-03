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
import com.outofbound.rhinoenginelib.mesh.GLMesh;
import com.outofbound.rhinoenginelib.renderer.GLRenderer;
import com.outofbound.rhinoenginelib.renderer.GLRendererOnTexture;
import com.outofbound.rhinoenginelib.renderer.GLSceneRenderer;
import com.outofbound.rhinoenginelib.renderer.fx.GLBlur;
import com.outofbound.rhinoenginelib.shader.GLShader;
import com.outofbound.rhinoenginelib.task.GLTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 * The engine abstract class.
 */
public abstract class GLEngine extends GLSurfaceView implements Renderer, OnTouchListener, ScaleGestureDetector.OnScaleGestureListener {

    private final ArrayList<GLRenderer> renderers = new ArrayList<>();
    private final ArrayList<GLRenderer> renderersInRunning = new ArrayList<>();
    private final ArrayList<GLTask> tasks = new ArrayList<>();
    private final ArrayList<GLTask> tasksInRunning = new ArrayList<>();
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
    private boolean running = false;
    private GLBlur glBlur = null;
    private GLSceneRenderer glSceneRenderer;



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
     * Add a GLRenderer object.
     * @param renderer the GLRenderer object to add.
     * @return true if added, false otherwise.
     */
    public boolean addGLRenderer(GLRenderer renderer){
        if (getGLRenderer(renderer.getId()) == null) {
            if (!running) {
                synchronized (renderers) {
                    renderer.onAdd();
                    renderers.add(renderer);
                }
            }
            else {
                synchronized (renderersInRunning) {
                    renderersInRunning.add(renderer);
                }
            }
            return true;
        }
        return false;
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

        // Enable depth test
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        // Accept fragment if it closer to the camera than the former one
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

        running = true;

        GLES20.glClearColor(clearColor[0], clearColor[1], clearColor[2], clearColor[3]);
        // clear Screen and Depth Buffer
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // get ms
        deltaMs = 0;
        long currentMs = Calendar.getInstance().getTimeInMillis();
        if (ms >= 0) {
            deltaMs = currentMs - ms;
        }

        synchronized (renderers) {
            for (GLRenderer glRenderer : renderers){
                glRenderer.move(deltaMs);
            }
        }

        mvpMatrix3D = camera3D.create(getWidth(), getHeight(), deltaMs);
        mvpMatrix2D = camera2D.create(getWidth(), getHeight(), deltaMs);

        synchronized (renderers) {
            if (glBlur != null){
                glBlur.render(getWidth(),getHeight(),deltaMs);
            }
            else {
                glSceneRenderer.doRendering();
            }
            for (GLRenderer glRenderer : renderersInRunning) {
                glRenderer.onAdd();
            }
            renderers.addAll(renderersInRunning);
            renderersInRunning.clear();
        }

        synchronized (tasks) {
            Iterator<GLTask> itr = tasks.iterator();
            while (itr.hasNext()) {
                GLTask task = itr.next();
                if (task.isDead()){
                    task.onRemove();
                    itr.remove();
                }
                else {
                    boolean alive = task.runTask(deltaMs);
                    if (!alive) {
                        itr.remove();
                    }
                }
            }
            for (GLTask glTask : tasksInRunning){
                glTask.onAdd();
            }
            tasks.addAll(tasksInRunning);
            tasksInRunning.clear();
        }

        ms = currentMs;

    }

    private void renderScene(){
        Iterator<GLRenderer> itr = renderers.iterator();
        while (itr.hasNext()) {
            GLRenderer renderer = itr.next();
            if (renderer.isDead(deltaMs)) {
                renderer.onRemove();
                itr.remove();
            } else {
                renderer.render(mvpMatrix3D, mvpMatrix2D, deltaMs);
            }
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
     * Add a GLTask object.
     * @param task the GLTask object.
     * @return true if added, false otherwise.
     */
    public boolean addGLTask(GLTask task){
        if (getGLTask(task.getId()) == null) {
            if (!running) {
                synchronized (tasks) {
                    task.onAdd();
                    tasks.add(task);
                }
            }
            else {
                synchronized (tasksInRunning) {
                    tasksInRunning.add(task);
                }
            }
            return true;
        }
        return false;
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
     * Return the GLMesh of the GLRenderer with the input id.
     * @param id the id of the GLRenderer.
     * @return the GLMesh found, null if not found.
     */
    public GLMesh getGLMesh(int id){
        GLRenderer glRenderer = getGLRenderer(id);
        return glRenderer != null ? glRenderer.getGLMesh() : null;
    }

    /**
     * Return the GLShader of the GLRenderer with input id.
     * @param id the id of the GLRenderer.
     * @return the GLShader found, null if not found.
     */
    public GLShader getGLShader(int id){
        GLRenderer glRenderer = getGLRenderer(id);
        return glRenderer != null ? glRenderer.getGLShader() : null;
    }

    /**
     * Return the GLRenderer with input id.
     * @param id the id.
     * @return the GLRenderer found, null if not found.
     */
    public GLRenderer getGLRenderer(int id){
        synchronized (renderers) {
            for (int i = 0; i < renderers.size(); i++) {
                if (renderers.get(i).getId() == id) {
                    return renderers.get(i);
                }
            }
        }
        synchronized (renderersInRunning) {
            for (int i = 0; i < renderersInRunning.size(); i++) {
                if (renderersInRunning.get(i).getId() == id) {
                    return renderersInRunning.get(i);
                }
            }
        }
        return null;
    }

    /**
     * Return GLTask with the input id.
     * @param id the id.
     * @return the GLTask found, null if not found.
     */
    public GLTask getGLTask(int id){
        synchronized (tasks) {
            for (int i = 0; i < tasks.size(); i++) {
                if (tasks.get(i).getId() == id) {
                    return tasks.get(i);
                }
            }
        }
        synchronized (tasksInRunning) {
            for (int i = 0; i < tasksInRunning.size(); i++) {
                if (tasksInRunning.get(i).getId() == id) {
                    return tasksInRunning.get(i);
                }
            }
        }
        return null;
    }

    /**
     * Return number of GLRenderer objects.
     * @return the number of GLRenderer objects.
     */
    public synchronized int getNumGLRenderer(){
        return renderers.size();
    }

    /**
     * Return the number of GLTask objects.
     * @return the number of GLTask objects.
     */
    public synchronized int getNumGLTask(){
        return tasks.size();
    }

    /**
     * Enable blur effect.
     * @param resolution the resolution. Must be GLRendererOnTexture.RESOLUTION_256, GLRendererOnTexture.RESOLUTION_512 or GLRendererOnTexture.RESOLUTION_1024.
     * @param scale the scale.
     * @param amount the amount.
     * @param strength the strength.
     * @return this GLEngine.
     */
    public GLEngine enableBlur(int resolution, float scale, float amount, float strength){
        glBlur = new GLBlur(new GLRendererOnTexture(glSceneRenderer,resolution),scale,amount,strength).setup();
        return this;
    }

    /**
     * Disable blur effect.
     * @return this GLEngine.
     */
    public GLEngine disableBlur(){
        glBlur = null;
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
