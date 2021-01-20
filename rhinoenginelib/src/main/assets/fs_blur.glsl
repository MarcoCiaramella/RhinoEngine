#version 100

precision mediump float;

varying vec2 vTexCoords;
uniform sampler2D uTextId;
uniform float uBlurScale;
uniform float uBlurAmount;
uniform float uBlurStrength;
uniform vec2 uScreenSize;
uniform int uType;
const vec2 screenCenter01 = vec2(0.5,0.5);

float gaussianFunction(float x, float dev){
    return ((1.0/sqrt(2.0*3.142857*dev))*exp(-(x*x)/(2.0*dev)));
}

float calcAttenuation(float distance){
    return 1.0 / (1.0 + 0.2 * distance + 0.2 * (distance * distance));
}

void main(){
    float dev = uBlurAmount*0.5*0.5;
    dev *= dev;
    vec4 color = vec4(0.0,0.0,0.0,0.0);
    vec2 fragCoord01 = gl_FragCoord.xy;
    fragCoord01.x /= uScreenSize.x;
    fragCoord01.y /= uScreenSize.y;
    float strength = 1.0 - uBlurStrength;
    strength *= calcAttenuation(length(fragCoord01 - screenCenter01));
    float half1 = float(uBlurAmount)*0.5;
    float texel = 1.0/128.0;
    int count = int(uBlurAmount);
    //int count = int(uBlurAmount * length(fragCoord01 - screenCenter01));
    float blurScale = uBlurScale * length(fragCoord01 - screenCenter01);
    if (uType == 0) {
        for (int i = 0; i < count; i++){
            float offset = float(i) - half1;
            color += texture2D(uTextId, vTexCoords+vec2(offset*texel*blurScale,0.0))*gaussianFunction(offset*strength, dev);
        }
    }
    else {
        for (int i = 0; i < count; i++){
            float offset = float(i) - half1;
            color += texture2D(uTextId, vTexCoords+vec2(0.0,offset*texel*blurScale))*gaussianFunction(offset*strength, dev);
        }
    }
    gl_FragColor = clamp(color, 0.0, 1.0);
    gl_FragColor.w = 1.0;
}