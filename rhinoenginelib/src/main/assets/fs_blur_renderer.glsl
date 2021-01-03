#version 100

precision mediump float;
varying vec2 v_texCoords;
uniform sampler2D u_texId;
uniform sampler2D u_texId1;

void main(){
    vec4 src = texture2D(u_texId, v_texCoords);
    vec4 dst = texture2D(u_texId1, v_texCoords);
    vec4 bloomColor = clamp((src + dst) - (src * dst), 0.0, 1.0);
    gl_FragColor = bloomColor;
    //gl_FragColor.a = 1.0;
}