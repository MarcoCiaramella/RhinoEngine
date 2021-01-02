#version 100

precision highp float;


vec4 pack (float depth){
    const vec4 bitSh = vec4(256.0*256.0*256.0, 256.0*256.0, 256.0, 1.0);
    const vec4 bitMsk = vec4(0, 1.0/256.0, 1.0/256.0, 1.0/256.0);
    vec4 comp = fract(depth * bitSh);
    comp -= comp.xxyz * bitMsk;
    return comp;
}

void main() {
    // the depth
    float normalizedDistance  = position.z / position.w;
    // scale it from 0-1
    normalizedDistance = (normalizedDistance + 1.0) / 2.0;
    // bias (to remove artifacts)
    normalizedDistance += 0.0005;
    // pack value into 32-bit RGBA texture
    gl_FragColor = pack(normalizedDistance);
}


