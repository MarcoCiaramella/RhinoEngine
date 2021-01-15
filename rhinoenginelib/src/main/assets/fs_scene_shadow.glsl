#version 100

precision highp float;


uniform vec3 uLightsPos[1];
uniform vec3 uLightsColor[1];
uniform sampler2D uShadowMap;
varying vec4 vColor;
varying vec3 vPosition;
varying vec3 vNormal;
varying vec4 vPositionFromLight;


const float ambientLight = 0.5;
const vec4 bitShifts = vec4(1.0 / (256.0*256.0*256.0), 1.0 / (256.0*256.0), 1.0 / 256.0, 1.0);


vec4 diffuse(vec3 lightPos, vec3 lightColor);
float calcShadow();



void main() {
    gl_FragColor = vColor * (diffuse(uLightsPos[0],uLightsColor[0]) * calcShadow() + ambientLight);
}

vec4 diffuse(vec3 lightPos, vec3 lightColor){
    return vec4(max(dot(normalize(vNormal), normalize(lightPos-vPosition)), 0.0) * lightColor, 1.0);
}

float unpack(vec4 color){
    return dot(color, bitShifts);
}

// return 0.0 if in shadow.
// return 1.0 if not in shadow.
float calcShadow(){
    vec4 positionFromLight = vPositionFromLight / vPositionFromLight.w;
    positionFromLight = (positionFromLight + 1.0) / 2.0;
    float closestFragmentZ = unpack(texture2D(uShadowMap, positionFromLight.xy));
    float currentFragmentZ = positionFromLight.z;
    return float(closestFragmentZ > currentFragmentZ);
}

