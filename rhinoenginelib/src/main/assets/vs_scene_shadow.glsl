#version 100

precision highp float;

uniform mat4 uMVPMatrix;
uniform mat4 uMVMatrix;
uniform mat4 uShadowMVPMatrix;
attribute vec4 aPosition;
attribute vec3 aNormal;
attribute vec4 aColor;
varying vec4 vColor;
varying vec3 vPosition;
varying vec3 vNormal;
varying vec4 vPositionFromLight;



void main() {
    vPosition = vec3(uMVMatrix * aPosition);
    vNormal = normalize(vec3(uMVMatrix * vec4(aNormal, 0.0)));
    vColor = aColor;
    vPositionFromLight = uShadowMVPMatrix * aPosition;
    gl_Position = uMVPMatrix * aPosition;
}
