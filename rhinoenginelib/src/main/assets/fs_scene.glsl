#version 100

precision highp float;


struct DirLight {
    vec3 direction;
    vec3 ambientColor;
    vec3 diffuseColor;
    vec3 specularColor;
};

uniform DirLight dirLight;
uniform vec3 uViewPos;
varying vec4 vColor;
varying vec3 vPosition;
varying vec3 vNormal;


vec4 calcDirLight(vec3 normal);



void main() {
    vec3 norm = normalize(vNormal);
    vec3 lightDir = normalize(uLightsPos[0] - vPosition);
    vec3 ambient = ambientLighting(uLightsColor[0]);
    vec3 diffuse = diffuseLighting(norm,lightDir,uLightsColor[0]);
    vec3 specular = specularLighting(norm,lightDir,uLightsColor[0]);
    gl_FragColor = vColor * vec4(ambient + diffuse + specular, 1.0);
}

float diffuseLighting(vec3 norm, vec3 lightDir){
    return max(dot(norm, lightDir), 0.0);
}

float specularLighting(vec3 norm, vec3 lightDir){
    vec3 viewDir = normalize(uViewPos - vPosition);
    vec3 reflectDir = reflect(-lightDir, norm);
    return pow(max(dot(uViewPos, reflectDir), 0.0), 32.0);
}

vec4 calcDirLight(vec3 normal){
    vec3 lightDir = normalize(-dirLight.direction);
    float diff = diffuseLighting(normal,lightDir);
    float spec = specularLighting(normal,lightDir);
    vec4 ambient = vec4(dirLight.ambientColor,1.0) * vColor;
    vec4 diffuse = vec4(dirLight.diffuseColor * diff, 1.0) * vColor;
    vec4 specular = vec4(dirLight.specularColor * spec, 1.0) * vec4(0.5,0.5,0.5,1.0);
    return ambient + diffuse + specualr;
}


