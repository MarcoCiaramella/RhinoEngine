#version 100

precision highp float;

struct light_dir {
    vec3 direction;
    vec3 color;
    float ambientStrength;
    float specularStrength;
};

uniform light_dir light;
uniform vec3 uViewPos;
varying vec4 vColor;
varying vec3 vPosition;
varying vec3 vNormal;
const float specularStrength = 0.5;
const float ambientStrength = 0.1;



vec3 ambientLighting(vec3 lightColor);
vec3 diffuseLighting(vec3 norm, vec3 lightDir, vec3 lightColor);
vec3 specularLighting(vec3 norm, vec3 lightDir, vec3 lightColor);



void main() {
    vec3 norm = normalize(vNormal);
    vec3 lightDir = normalize(uLightsPos[0] - vPosition);
    vec3 ambient = ambientLighting(uLightsColor[0]);
    vec3 diffuse = diffuseLighting(norm,lightDir,uLightsColor[0]);
    vec3 specular = specularLighting(norm,lightDir,uLightsColor[0]);
    gl_FragColor = vColor * vec4(ambient + diffuse + specular, 1.0);
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


