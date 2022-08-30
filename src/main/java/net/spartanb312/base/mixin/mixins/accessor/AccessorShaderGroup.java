package net.spartanb312.base.mixin.mixins.accessor;

import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.Shader;
import net.minecraft.client.shader.ShaderGroup;
import me.thediamondsword5.moloch.mixinotherstuff.AccessorInterfaceShaderGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(ShaderGroup.class)
public abstract class AccessorShaderGroup implements AccessorInterfaceShaderGroup {
    @Accessor(value = "listFramebuffers")
    public abstract List<Framebuffer> getListFramebuffers();

    @Accessor(value = "listShaders")
    public abstract List<Shader> getListShaders();
}
