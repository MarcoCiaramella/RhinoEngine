#version 100

precision highp float;


#define MAX_NUM_SHADOWS 9

uniform mat4 uMVPMatrix;
uniform mat4 uMMatrix;
uniform mat4 uShadowMVPMatrix[MAX_NUM_SHADOWS];
uniform int uNumShadows;
attribute vec4 aPosition;
attribute vec3 aNormal;
attribute vec4 aColor;
varying vec4 vColor;
varying vec3 vPosition;
varying vec3 vNormal;
varying vec4 vPositionFromLight[MAX_NUM_SHADOWS];



void main() {
    vPosition = vec3(uMMatrix * aPosition);
    vNormal = normalize(vec3(uMMatrix * vec4(aNormal, 0.0)));
    vColor = aColor;
    for (int i = 0; i < uNumShadows; i++){
        vPositionFromLight[i] = uShadowMVPMatrix[i] * aPosition;
    }
    gl_Position = uMVPMatrix * aPosition;
}
