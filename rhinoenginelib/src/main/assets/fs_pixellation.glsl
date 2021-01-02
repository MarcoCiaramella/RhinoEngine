#version 100

precision mediump float;

uniform sampler2D u_texId;
// render target width
uniform float u_rt_w;
// render target height
uniform float u_rt_h;
// width of a low resolution pixel
uniform float u_pixel_w;
// height of a low resolution pixel
uniform float u_pixel_h;
uniform float uTime;
varying vec2 vTexCoords;


void main(){
    float dx = clamp(u_pixel_w*uTime,1.0,u_rt_w) / u_rt_w;
    float dy = clamp(u_pixel_h*uTime,1.0,u_rt_h) / u_rt_h;
    vec2 coord = vec2(dx*floor(vTexCoords.x/dx), dy*floor(vTexCoords.y/dy));
    vec3 tc = texture2D(u_texId, coord).rgb;
    gl_FragColor = vec4(tc, 1.0);
}