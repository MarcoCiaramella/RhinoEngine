#version 100

precision highp float;

uniform mat4 uMVPMatrix;
attribute vec4 aPosition;
varying vec4 vPosition;


void main() {
    vec4 position = uMVPMatrix * aPosition;
    vPosition = position;
    gl_Position = position;
}
