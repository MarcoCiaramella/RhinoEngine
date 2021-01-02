package com.outofbound.rhinoenginelib.shader.primitives;

import com.outofbound.rhinoenginelib.shader.GLShader;

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
