#version 100

precision mediump float;

varying vec2 v_texCoords;
uniform sampler2D u_texId;

void main(){
    gl_FragColor = texture2D(u_texId, v_texCoords);
}