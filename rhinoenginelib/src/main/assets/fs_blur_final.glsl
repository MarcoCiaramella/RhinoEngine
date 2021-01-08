#version 100

precision mediump float;

varying vec2 vTexCoords;
uniform sampler2D uTextId1;
uniform sampler2D uTextId2;


void main(){
    vec4 src = texture2D(uTextId1, vTexCoords);
    vec4 dst = texture2D(uTextId2, vTexCoords);
    vec4 bloomColor = clamp((src + dst) - (src * dst), 0.0, 1.0);
    gl_FragColor = bloomColor;
}

