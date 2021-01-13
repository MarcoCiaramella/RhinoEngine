#version 100

precision highp float;


uniform vec3 uLightsPos[1];
uniform vec3 uLightsColor[1];
uniform float uLightsIntensity[1];
uniform sampler2D uShadowMap;
varying vec4 vColor;
varying vec3 vPosition;
varying vec3 vNormal;
varying vec4 vPositionFromLight;


const float ambientLight = 0.5;
const vec4 bitShifts = vec4(1.0 / (256.0*256.0*256.0), 1.0 / (256.0*256.0), 1.0 / 256.0, 1.0);


float calcDiffuseLight(vec3 light, float intensity, vec3 vertex, vec3 normal);
float calcShadow();



void main() {
    float diffuse = calcDiffuseLight(uLightsPos[0], uLightsIntensity[0], vPosition, vNormal);
    gl_FragColor = vColor * ((diffuse*vec4(uLightsColor[0],1.0)) * calcShadow() + ambientLight);
}

float calcDiffuseLight(vec3 light, float intensity, vec3 vertex, vec3 normal){
    float distance = length(light - vertex);
    vec3 lightVector = normalize(light - vertex);
    float diffuse = max(dot(normal, lightVector), 0.1);
    diffuse = diffuse * (1.0 / (1.0 + (0.25 * distance * distance))) * intensity;
    return diffuse;
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

