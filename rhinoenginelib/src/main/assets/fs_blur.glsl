#version 100

precision mediump float;

varying vec2 vTexCoords;
uniform sampler2D uSceneTextureId;
// radius [0.0,1.0]
uniform float uRadius;


vec4 calcBlur(sampler2D texture, vec2 uv) {
    float pi = 6.28318530718; // Pi*2

    // GAUSSIAN BLUR SETTINGS {{{
    float directions = 16.0; // BLUR DIRECTIONS (Default 16.0 - More is better but slower)
    float quality = 3.0; // BLUR QUALITY (Default 3.0 - More is better but slower)
    vec2 radius = vec2(uRadius/10.0,uRadius/10.0);
    // GAUSSIAN BLUR SETTINGS }}}


    // Pixel colour
    vec4 color = texture2D(texture, uv);

    // Blur calculations
    for(float d=0.0; d<pi; d+=pi/directions){
        for(float i=1.0/quality; i<=1.0; i+=1.0/quality){
            color += texture2D(texture, uv+vec2(cos(d),sin(d))*radius*i);
        }
    }

    // Output to screen
    color /= quality * directions - 15.0;
    return color;
}

void main(){
    vec4 scene = texture2D(uSceneTextureId, vTexCoords);
    vec4 blur = calcBlur(uSceneTextureId, vTexCoords);
    gl_FragColor = clamp((scene + blur) - (scene * blur), 0.0, 1.0);
}
