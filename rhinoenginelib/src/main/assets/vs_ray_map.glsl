#version 100

precision highp float;

uniform mat4 uMVPMatrix;
attribute vec4 aPosition;
attribute vec3 aNormal;
varying vec3 vPosition;
varying vec3 vNormal;


void main() {
    vec4 position = uMVPMatrix * aPosition;
    vPosition = vec3(position);
    vNormal = normalize(vec3(uMMatrix * vec4(aNormal, 0.0)));
    gl_Position = position;
}
