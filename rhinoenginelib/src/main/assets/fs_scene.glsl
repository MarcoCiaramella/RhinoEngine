#version 100

precision highp float;


uniform vec3 uLightsPos[1];
uniform vec3 uLightsColor[1];
uniform float uLightsIntensity[1];
varying vec4 vColor;
varying vec3 vPosition;
varying vec3 vNormal;



float calcDiffuseLight(vec3 light, float intensity, vec3 vertex, vec3 normal);



void main() {
    float diffuse = calcDiffuseLight(uLightsPos[0], uLightsIntensity[0], vPosition, vNormal);
    vec4 color = vColor * (diffuse*vec4(uLightsColor[0],1.0));
    gl_FragColor = color;
}

float calcDiffuseLight(vec3 light, float intensity, vec3 vertex, vec3 normal){
    float distance = length(light - vertex);
    vec3 lightVector = normalize(light - vertex);
    float diffuse = max(dot(normal, lightVector), 0.1);
    diffuse = diffuse * (1.0 / (1.0 + (0.25 * distance * distance))) * intensity;
    return diffuse;
}


