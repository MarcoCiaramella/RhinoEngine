#version 100

precision highp float;

varying vec4 vPosition;

const vec4 bitSh = vec4(256.0 * 256.0 * 256.0, 256.0 * 256.0, 256.0, 1.0);
const vec4 bitMsk = vec4(0, 1.0 / 256.0, 1.0 / 256.0, 1.0 / 256.0);



vec4 packToRGBA32(float f){
    vec4 comp = fract(f * bitSh);
    comp -= comp.xxyz * bitMsk;
    return comp;
}

float getDepth(){
    return vPosition.z / vPosition.w;
}

float to01(float f){
    return (f + 1.0) / 2.0;
}

void main() {
    gl_FragColor = packToRGBA32(to01(getDepth()));
}


