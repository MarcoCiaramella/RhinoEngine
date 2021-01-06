package com.outofbound.rhinoenginelib.shader.primitives;

import com.outofbound.rhinoenginelib.shader.GLShader;

public class BaseWithShadowShader extends GLShader {

    public BaseWithShadowShader() {
        super("vs_base_shadow.glsl", "fs_base_shadow.glsl");
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
                null,
                "uShadowMap",
                "uShadowMVPMatrixLocation");
    }
}
