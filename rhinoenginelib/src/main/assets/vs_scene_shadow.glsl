#version 100

precision highp float;

uniform mat4 uMVPMatrix;
uniform mat4 uMMatrix;
uniform mat4 uShadowMVPMatrix;
attribute vec4 aPosition;
attribute vec3 aNormal;
attribute vec4 aColor;
varying vec4 vColor;
varying vec3 vPosition;
varying vec3 vNormal;
varying vec4 vShadowCoord;



void main() {
    vPosition = vec3(uMMatrix * aPosition);
    vNormal = normalize(vec3(uMMatrix * vec4(aNormal, 0.0)));
    vColor = aColor;
    vShadowCoord = uShadowMVPMatrix * aPosition;
    gl_Position = uMVPMatrix * aPosition;
}
