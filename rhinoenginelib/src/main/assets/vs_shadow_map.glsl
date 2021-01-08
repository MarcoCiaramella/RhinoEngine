#version 100

precision highp float;

uniform mat4 uMVPMatrix;
uniform mat4 uMMatrix;
attribute vec4 aPosition;
varying vec3 vPosition;


void main() {
    vPosition = vec3(uMMatrix * aPosition);
    gl_Position = uMVPMatrix * aPosition;
}
