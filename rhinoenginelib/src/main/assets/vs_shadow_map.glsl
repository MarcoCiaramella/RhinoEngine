#version 100

precision highp float;

uniform mat4 uMVPMatrix;
uniform mat4 uMMatrix;
attribute vec4 aPosition;
varying vec4 vPosition;


void main() {
    vec4 position = uMMatrix * aPosition;
    vPosition = position;
    gl_Position = position;
}
