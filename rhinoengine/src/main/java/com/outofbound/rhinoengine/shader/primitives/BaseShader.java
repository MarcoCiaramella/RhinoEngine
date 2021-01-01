package com.outofbound.rhinoengine.shader.primitives;

import com.outofbound.rhinoengine.shader.GLShader;

public class BaseShader extends GLShader {

    public BaseShader() {
        super("vs_base.glsl", "fs_base.glsl");
        config(
                "aPosition",
                "aColor",
                "aNormal",
                "uMVPMatrix",
                "uMVMatrix",
                null,
                "uLightsPos",
                "uLightsColor",
                "uLightsIntensity",
                null,
                null,
                null);
    }
}
