#version 100

precision highp float;


struct DirLight {
    vec3 direction;
    vec3 ambientColor;
    vec3 diffuseColor;
    vec3 specularColor;
};

struct PointLight {
    vec3 position;
    float constant;
    float linear;
    float quadratic;
    vec3 ambientColor;
    vec3 diffuseColor;
    vec3 specularColor;
};

#define MAX_NUM_POINT_LIGHTS 16
uniform DirLight uDirLight;
uniform PointLight uPointLights[MAX_NUM_POINT_LIGHTS];
uniform int uNumPointLights;
uniform vec3 uViewPos;
varying vec4 vColor;
varying vec3 vPosition;
varying vec3 vNormal;


vec4 calcDirLight(vec3 normal, vec3 viewDir);
vec4 calcPointLight(PointLight pointLight, vec3 normal, vec3 viewDir);



void main() {
    vec3 normal = normalize(vNormal);
    vec3 viewDir = normalize(uViewPos - vPosition);
    vec4 result = calcDirLight(normal, viewDir);
    for (int i = 0; i < uNumPointLights; i++){
        result += calcPointLight(uPointLights[i], normal, viewDir);
    }
    gl_FragColor = result;
}

float diffuseLighting(vec3 norm, vec3 lightDir){
    return max(dot(norm, lightDir), 0.0);
}

float specularLighting(vec3 norm, vec3 lightDir, vec3 viewDir){
    vec3 reflectDir = reflect(-lightDir, norm);
    return pow(max(dot(viewDir, reflectDir), 0.0), 32.0);
}

vec4 calcDirLight(vec3 normal, vec3 viewDir){
    vec3 lightDir = normalize(-uDirLight.direction);
    float diff = diffuseLighting(normal, lightDir);
    float spec = specularLighting(normal, lightDir, viewDir);
    vec4 ambient = vec4(uDirLight.ambientColor, 1.0) * vColor;
    vec4 diffuse = vec4(uDirLight.diffuseColor * diff, 1.0) * vColor;
    vec4 specular = vec4(uDirLight.specularColor * spec, 1.0) * vec4(0.5,0.5,0.5,1.0);
    return ambient + diffuse + specualr;
}

vec4 calcPointLight(PointLight pointLight, vec3 normal, vec3 viewDir){
    vec3 d = pointLight.position - vPosition;
    vec3 lightDir = normalize(d);
    float diff = diffuseLighting(normal, lightDir);
    float spec = specularLighting(normal, lightDir, viewDir);
    float distance = length(d);
    float attenuation = 1.0 / (pointLight.constant + pointLight.linear * distance + pointLight.quadratic * (distance * distance));
    vec4 ambient = vec4(uDirLight.ambientColor, 1.0) * vColor;
    vec4 diffuse = vec4(uDirLight.diffuseColor * diff, 1.0) * vColor;
    vec4 specular = vec4(uDirLight.specularColor * spec, 1.0) * vec4(0.5,0.5,0.5,1.0);
    ambient *= attenuation;
    diffuse *= attenuation;
    specular *= attenuation;
    return ambient + diffuse + specualr;
}
