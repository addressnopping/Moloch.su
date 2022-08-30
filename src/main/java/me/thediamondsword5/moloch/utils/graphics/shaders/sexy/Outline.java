package me.thediamondsword5.moloch.utils.graphics.shaders.sexy;

import me.thediamondsword5.moloch.utils.graphics.shaders.FramebufferShader;
import org.lwjgl.opengl.GL20;

public class Outline extends FramebufferShader {
    public static final Outline SHADER_OUTLINE = new Outline();

    public Outline() {
        super("outline.fsh", "vertex.vsh");
    }

    @Override
    public void setupUniforms() {
        setupUniform("texture");
        setupUniform("texelSize");
        setupUniform("color");
        setupUniform("alpha");
        setupUniform("radius");
    }

    @Override
    public void updateUniforms() {
        GL20.glUniform1i(getUniform("texture"), 0);
        GL20.glUniform2f(getUniform("texelSize"), 1.0f / mc.displayWidth * (radius * quality), 1.0f / mc.displayHeight * (radius * quality));
        GL20.glUniform4f(getUniform("color"), red, green, blue, alpha);
        GL20.glUniform1f(getUniform("alpha"), alpha);
        GL20.glUniform1f(getUniform("radius"), radius);
    }
}
