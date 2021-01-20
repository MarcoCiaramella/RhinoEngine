#version 100

precision highp float;


struct DirLight {
    vec3 direction;
    vec3 ambientColor;
    vec3 diffuseColor;
    vec3 specularColor;
    sampler2D shadowMap;
    mat4 shadowMVPMatrix;
    int shadowEnabled;
};

struct PointLight {
    vec3 position;
    float constant;
    float linear;
    float quadratic;
    vec3 ambientColor;
    vec3 diffuseColor;
    vec3 specularColor;
    sampler2D shadowMap;
    mat4 shadowMVPMatrix;
    int shadowEnabled;
};

#define MAX_NUM_POINT_LIGHTS 8

uniform DirLight uDirLight;
uniform PointLight uPointLights[MAX_NUM_POINT_LIGHTS];
uniform int uNumPointLights;
uniform vec3 uViewPos;
varying vec4 vColor;
varying vec3 vPosition;
varying vec3 vNormal;
const vec4 bitShifts = vec4(1.0 / (256.0*256.0*256.0), 1.0 / (256.0*256.0), 1.0 / 256.0, 1.0);




float unpack(vec4 color){
    return dot(color, bitShifts);
}

// return 0.0 if in shadow.
// return 1.0 if not in shadow.
float calcShadow(vec4 positionFromLight, sampler2D shadowMap, int shadowEnabled){
    if (shadowEnabled == 0){
        return 1.0;
    }
    vec3 positionFromLight3 = positionFromLight.xyz / positionFromLight.w;
    positionFromLight3 = (positionFromLight3 + 1.0) / 2.0;
    float closestFragmentZ = unpack(texture2D(shadowMap, positionFromLight3.xy));
    float currentFragmentZ = positionFromLight3.z;
    return float(closestFragmentZ > currentFragmentZ);
}

float diffuseLighting(vec3 normal, vec3 lightDir){
    return max(dot(normal, lightDir), 0.0);
}

float specularLighting(vec3 normal, vec3 lightDir, vec3 viewDir){
    vec3 reflectDir = reflect(-lightDir, normal);
    return pow(max(dot(viewDir, reflectDir), 0.0), 32.0);
}

vec4 calcDirLight(vec3 normal, vec3 viewDir){
    vec3 lightDir = normalize(-uDirLight.direction);
    float diff = diffuseLighting(normal, lightDir);
    float spec = specularLighting(normal, lightDir, viewDir);
    vec4 ambient = vec4(uDirLight.ambientColor, 1.0) * vColor;
    vec4 diffuse = vec4(uDirLight.diffuseColor * diff, 1.0) * vColor;
    vec4 specular = vec4(uDirLight.specularColor * spec, 1.0) * vec4(0.5,0.5,0.5,1.0);
    return ambient + (diffuse + specular) * calcShadow(vec4(vPosition, 1.0) * uDirLight.shadowMVPMatrix, uDirLight.shadowMap, uDirLight.shadowEnabled);
}

float calcAttenuation(PointLight pointLight, float distance){
    return 1.0 / (pointLight.constant + pointLight.linear * distance + pointLight.quadratic * (distance * distance));
}

vec4 calcPointLight(PointLight pointLight, vec3 normal, vec3 viewDir){
    vec3 d = pointLight.position - vPosition;
    vec3 lightDir = normalize(d);
    float diff = diffuseLighting(normal, lightDir);
    float spec = specularLighting(normal, lightDir, viewDir);
    float distance = length(d);
    float attenuation = calcAttenuation(pointLight,distance);
    vec4 ambient = vec4(pointLight.ambientColor, 1.0) * vColor;
    vec4 diffuse = vec4(pointLight.diffuseColor * diff, 1.0) * vColor;
    vec4 specular = vec4(pointLight.specularColor * spec, 1.0) * vec4(0.5,0.5,0.5,1.0);
    ambient *= attenuation;
    diffuse *= attenuation;
    specular *= attenuation;
    return ambient + (diffuse + specular) * calcShadow(vec4(vPosition, 1.0) * pointLight.shadowMVPMatrix, pointLight.shadowMap, pointLight.shadowEnabled);
}



void main() {
    vec3 normal = normalize(vNormal);
    vec3 viewDir = normalize(uViewPos - vPosition);
    vec4 result = calcDirLight(normal, viewDir);
    for (int i = 0; i < uNumPointLights; i++){
        result += calcPointLight(uPointLights[i], normal, viewDir);
    }
    gl_FragColor = result;
}

