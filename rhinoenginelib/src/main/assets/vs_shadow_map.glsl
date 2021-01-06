#version 100

precision highp float;

uniform mat4 uMVPMatrix;
uniform mat4 uMVMatrix;
attribute vec4 aPosition;
varying vec3 vPosition;


void main() {
    vPosition = vec3(uMVMatrix * aPosition);
    gl_Position = uMVPMatrix * aPosition;
}
