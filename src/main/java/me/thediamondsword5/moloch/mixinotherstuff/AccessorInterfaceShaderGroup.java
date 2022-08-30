package me.thediamondsword5.moloch.mixinotherstuff;

import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.Shader;

import java.util.List;

public interface AccessorInterfaceShaderGroup {

    List<Framebuffer> getListFramebuffers();

    List<Shader> getListShaders();
}
