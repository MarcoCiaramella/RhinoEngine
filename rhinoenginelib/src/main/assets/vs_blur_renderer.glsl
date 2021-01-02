#version 100

precision mediump float;

attribute vec4 a_position;
attribute vec2 a_texCoords;
uniform mat4 u_mvpMatrix;
varying vec2 v_texCoords;

void main(){
    v_texCoords = a_texCoords;
    gl_Position = u_mvpMatrix * a_position;
}