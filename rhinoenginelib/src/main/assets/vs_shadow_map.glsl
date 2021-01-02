#version 100

precision highp float;

uniform mat4 uMVPMatrix;
attribute vec4 aPosition;


void main() {
    gl_Position = uMVPMatrix * aPosition;
}
