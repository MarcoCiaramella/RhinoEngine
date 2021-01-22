#version 100

precision highp float;


varying vec3 vPosition;
varying vec3 vNormal;


void main() {
    // looking from light. vPosition is the light direction.
    vec3 reflectDir = reflect(normalize(vPosition), normalize(vNormal));
    gl_FragColor = vec4(reflectDir,1.0);
}
