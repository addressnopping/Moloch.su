package net.spartanb312.base.mixin;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.Name("Moloch")
@IFMLLoadingPlugin.SortingIndex(value = 1001)
public class MixinLoader implements IFMLLoadingPlugin {

    public static final Logger log = LogManager.getLogger("MIXIN");

    public MixinLoader() {
        log.info("Mixins initialized");
        MixinBootstrap.init();
        Mixins.addConfigurations("mixins.mixer.json");
        MixinEnvironment.getDefaultEnvironment().setObfuscationContext("searge");
        log.info(MixinEnvironment.getDefaultEnvironment().getObfuscationContext());
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) { }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

}
