#version 100

precision highp float;


struct DirLight {
    int on;
    vec3 direction;
    vec3 ambientColor;
    vec3 diffuseColor;
    vec3 specularColor;
    float specularExponent;
    sampler2D shadowMap;
    mat4 shadowVPMatrix;
    int shadowEnabled;
};

struct PointLight {
    int on;
    vec3 position;
    float constant;
    float linear;
    float quadratic;
    vec3 ambientColor;
    vec3 diffuseColor;
    vec3 specularColor;
    float specularExponent;
    sampler2D shadowMap;
    mat4 shadowVPMatrix;
    int shadowEnabled;
};

uniform DirLight uDirLight;
uniform PointLight uPointLight;
uniform vec3 uViewPos;
uniform sampler2D uTexture;
uniform int uIsTextured;
varying vec4 vColor;
varying vec4 vPosition;
varying vec3 vNormal;
varying vec2 vTexCoords;
const vec4 bitShifts = vec4(1.0 / (256.0*256.0*256.0), 1.0 / (256.0*256.0), 1.0 / 256.0, 1.0);



vec4 getColor(){
    if (uIsTextured != 0){
        return texture2D(uTexture,vTexCoords);
    }
    return vColor;
}

float unpack(vec4 color){
    return dot(color, bitShifts);
}

// return 0.0 if in shadow.
// return 1.0 if not in shadow.
float calcShadow(sampler2D shadowMap, vec4 positionFromLight, int shadowEnabled){
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

float specularLighting(vec3 normal, vec3 lightDir, vec3 viewDir, float specularExponent){
    vec3 reflectDir = reflect(-lightDir, normal);
    return pow(max(dot(viewDir, reflectDir), 0.0), specularExponent);
}

vec4 calcDirLight(vec3 normal, vec3 viewDir){
    vec3 lightDir = normalize(-uDirLight.direction);
    float diff = diffuseLighting(normal, lightDir);
    float spec = specularLighting(normal, lightDir, viewDir, uDirLight.specularExponent);
    vec4 color = getColor();
    vec4 ambient = vec4(uDirLight.ambientColor, 1.0) * color;
    vec4 diffuse = vec4(uDirLight.diffuseColor * diff, 1.0) * color;
    vec4 specular = vec4(uDirLight.specularColor * spec, 1.0) * vec4(0.5,0.5,0.5,1.0);
    return ambient + (diffuse + specular) * calcShadow(uDirLight.shadowMap, uDirLight.shadowVPMatrix * vPosition, uDirLight.shadowEnabled);
}

float calcAttenuation(float distance){
    return 1.0 / (uPointLight.constant + uPointLight.linear * distance + uPointLight.quadratic * (distance * distance));
}

vec4 calcPointLight(vec3 normal, vec3 viewDir){
    vec3 d = uPointLight.position - vec3(vPosition);
    vec3 lightDir = normalize(d);
    float diff = diffuseLighting(normal, lightDir);
    float spec = specularLighting(normal, lightDir, viewDir, uPointLight.specularExponent);
    float distance = length(d);
    float attenuation = calcAttenuation(distance);
    vec4 color = getColor();
    vec4 ambient = vec4(uPointLight.ambientColor, 1.0) * color;
    vec4 diffuse = vec4(uPointLight.diffuseColor * diff, 1.0) * color;
    vec4 specular = vec4(uPointLight.specularColor * spec, 1.0) * vec4(0.5,0.5,0.5,1.0);
    ambient *= attenuation;
    diffuse *= attenuation;
    specular *= attenuation;
    return ambient + (diffuse + specular) * calcShadow(uPointLight.shadowMap, uPointLight.shadowVPMatrix * vPosition, uPointLight.shadowEnabled);
}



void main() {
    vec3 normal = normalize(vNormal);
    vec3 viewDir = normalize(uViewPos - vec3(vPosition));
    vec4 result = vec4(0.0);
    if (uDirLight.on == 1){
        result = calcDirLight(normal, viewDir);
    }
    if (uPointLight.on == 1){
        result += calcPointLight(normal, viewDir);
    }
    gl_FragColor = result;
}

