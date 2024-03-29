#version 100

precision highp float;


uniform mat4 uMVPMatrix;
uniform mat4 uMMatrix;
attribute vec4 aPosition;
attribute vec3 aNormal;
attribute vec2 aTexCoords;
varying vec4 vPosition;
varying vec3 vNormal;
varying vec2 vTexCoords;



void main() {
    vPosition = uMMatrix * aPosition;
    vNormal = normalize(vec3(uMMatrix * vec4(aNormal, 0.0)));
    vTexCoords = aTexCoords;
    gl_Position = uMVPMatrix * aPosition;
}
