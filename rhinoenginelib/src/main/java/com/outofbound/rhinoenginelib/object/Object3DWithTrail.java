package com.outofbound.rhinoenginelib.object;


public class Object3DWithTrail {

    /*public Object3DWithTrail(int idObject, int idTrail, GLMesh glMeshObject, GLShader glShaderObject, TrailMesh trailMesh, TrailShader trailShader){
        this.glRendererObject = new GLRenderer3D(idObject,glMeshObject.createBoundingBox(),glShaderObject);
        this.glRendererTrail = new GLRenderer3D(idTrail,trailMesh,trailShader).disableFaceCulling().enableBlending();
    }

    @Override
    public boolean load() {
        boolean res1 = GLEngine.getInstance().addGLRenderer(glRendererObject);
        boolean res2 = GLEngine.getInstance().addGLRenderer(glRendererTrail);
        return res1 && res2;
    }

    @Override
    public void unload() {
        glRendererObject.kill();
        glRendererTrail.kill();
    }*/
}