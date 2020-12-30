#version 100

precision mediump float;

uniform sampler2D uTexId;
uniform float uTime;
uniform vec2 uWaveCentre;
uniform vec2 uScreenResolution;
varying vec2 vTexCoords;



float map(float value, float min1, float max1, float min2, float max2) {
    return min2 + (value - min1) * (max2 - min2) / (max1 - min1);
}


void main() {
    vec3 waveParams = vec3(10.0, 0.8, 0.1);

    vec2 waveCentre = uWaveCentre;
    vec2 texCoord = vTexCoords;

    waveCentre.x = map(waveCentre.x,-1.0,1.0,1.0,0.0);
    waveCentre.y = map(waveCentre.y,-1.0,1.0,1.0,0.0);

    vec4 color = texture2D(uTexId, texCoord);

    float ratio = uScreenResolution.y/uScreenResolution.x;
    waveCentre.y *= ratio;
    texCoord.y *= ratio;

    float dist = distance(texCoord, waveCentre);

    //Only distort the pixels within the parameter distance from the centre
    if ( (dist <= (uTime + waveParams.z)) && (dist >= (uTime - waveParams.z)) ){
        //The pixel offset distance based on the input parameters
        float diff = (dist - uTime);
        float scaleDiff = (1.0 - pow(abs(diff * waveParams.x), waveParams.y));
        float diffTime = diff  * scaleDiff;
        //The direction of the distortion
        vec2 diffTexCoord = normalize(texCoord - waveCentre);
        //Perform the distortion and reduce the effect over time
        texCoord += ((diffTexCoord * diffTime) / (uTime * dist * 40.0));
        color = texture2D(uTexId, texCoord);
        //Blow out the color and reduce the effect over time
        color += (color * scaleDiff) / (uTime * dist * 40.0);
    }

    gl_FragColor = color;
}
