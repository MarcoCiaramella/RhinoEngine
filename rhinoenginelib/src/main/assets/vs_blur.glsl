#version 100

precision mediump float;

uniform mat4 uMVPMatrix;      // A constant representing the combined model/view/projection matrix.

attribute vec4 aPosition;     // Per-vertex position information we will pass in.
attribute vec2 aTexCoords;

varying vec2 vTexCoords;

void main() {
    vTexCoords = aTexCoords;
    gl_Position = uMVPMatrix * aPosition;
}
