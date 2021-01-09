#version 100

precision mediump float;

varying vec2 vTexCoords;
uniform sampler2D uTextId;
uniform float uBlurScale;
uniform float uBlurAmount;
uniform float uBlurStrength;

float gaussianFunction(float x, float dev){
    return ((1.0/sqrt(2.0*3.142857*dev))*exp(-(x*x)/(2.0*dev)));
}

void main(){
    float dev = uBlurAmount*0.5*0.5;
    dev *= dev;
    vec4 color = vec4(0.0,0.0,0.0,0.0);
    float strength = 1.0 - uBlurStrength;
    float half1 = float(uBlurAmount)*0.5;
    float texel = 1.0/128.0;
    int count = int(uBlurAmount);
    for (int i = 0; i < count; i++){
        float offset = float(i) - half1;
        color += texture2D(uTextId, vTexCoords+vec2(0.0,offset*texel*uBlurScale))*gaussianFunction(offset*strength, dev);
    }
    gl_FragColor = clamp(color, 0.0, 1.0);
    gl_FragColor.w = 1.0;
}