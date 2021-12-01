#version 100

precision mediump float;

attribute vec4 aPosition;
attribute vec2 aTexCoords;
varying vec2 vTexCoords;

void main(){
    vTexCoords = aTexCoords;
    gl_Position = aPosition;
}
