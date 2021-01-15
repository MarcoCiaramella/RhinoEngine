#version 100

precision highp float;


uniform vec3 uLightsPos[1];
uniform vec3 uLightsColor[1];
uniform sampler2D uShadowMap;
uniform vec3 uViewPos;
varying vec4 vColor;
varying vec3 vPosition;
varying vec3 vNormal;
varying vec4 vPositionFromLight;
const float specularStrength = 0.5;
const float ambientStrength = 0.1;
const vec4 bitShifts = vec4(1.0 / (256.0*256.0*256.0), 1.0 / (256.0*256.0), 1.0 / 256.0, 1.0);


vec3 ambientLighting(vec3 lightColor);
vec3 diffuseLighting(vec3 norm, vec3 lightDir, vec3 lightColor);
vec3 specularLighting(vec3 norm, vec3 lightDir, vec3 lightColor);
float calcShadow();



void main() {
    //gl_FragColor = vColor * (diffuse(uLightsPos[0],uLightsColor[0]) * calcShadow() + ambientLight);
}

vec3 ambientLighting(vec3 lightColor){
    return ambientStrength * lightColor;
}

vec3 diffuseLighting(vec3 norm, vec3 lightDir, vec3 lightColor){
    return max(dot(norm, lightDir), 0.0) * lightColor;
}

vec3 specularLighting(vec3 norm, vec3 lightDir, vec3 lightColor){
    vec3 viewDir = normalize(uViewPos - vPosition);
    vec3 reflectDir = reflect(-lightDir, norm);
    float spec = pow(max(dot(uViewPos, reflectDir), 0.0), 32.0);
    return specularStrength * spec * lightColor;
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

