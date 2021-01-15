#version 100

precision highp float;


uniform vec3 uLightsPos[1];
uniform vec3 uLightsColor[1];
varying vec4 vColor;
varying vec3 vPosition;
varying vec3 vNormal;



vec4 diffuse(vec3 lightPos, vec3 lightColor);


void main() {
    gl_FragColor = vColor * diffuse(uLightsPos[0],uLightsColor[0]);
}

vec4 diffuse(vec3 lightPos, vec3 lightColor){
    return vec4(max(dot(normalize(vNormal), normalize(lightPos-vPosition)), 0.0) * lightColor, 1.0);
}


