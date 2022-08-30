package net.spartanb312.base.mixin.mixins.render;

import net.spartanb312.base.client.ModuleManager;
import net.spartanb312.base.module.modules.visuals.NoRender;
import net.spartanb312.base.utils.math.Pair;
import net.minecraft.client.gui.BossInfoClient;
import net.minecraft.client.gui.GuiBossOverlay;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static net.spartanb312.base.utils.ItemUtils.mc;

@Mixin(GuiBossOverlay.class)
public class MixinGuiBossOverlay {
    @Inject(method = "renderBossHealth", at = @At("HEAD"), cancellable = true)
    private void renderBossHealthHookPre(CallbackInfo ci) {
        if (ModuleManager.getModule(NoRender.class).isEnabled() && NoRender.INSTANCE.bossBar.getValue() == NoRender.BossBarMode.Stack) {

            Map<UUID, BossInfoClient> map = mc.ingameGUI.getBossOverlay().mapBossInfos;
            ScaledResolution scaledResolution = new ScaledResolution(mc);
            int scaledWidth = scaledResolution.getScaledWidth();
            int width = (int)((float)scaledWidth / NoRender.INSTANCE.bossBarSize.getValue() / 2.0f - 91.0f);
            int i = 12;

            if (NoRender.INSTANCE.bossBar.getValue() == NoRender.BossBarMode.Stack) {
                final HashMap<String, Pair<BossInfoClient, Integer>> map2 = new HashMap<>();

                for (Map.Entry<UUID, BossInfoClient> entry : map.entrySet()) {

                    Pair<BossInfoClient, Integer> pair;
                    String s = entry.getValue().getName().getFormattedText();
                    if (map2.containsKey(s)) {
                        pair = map2.get(s);
                        pair = new Pair<>(pair.a, pair.b + 1);
                        map2.put(s, pair);
                        continue;
                    }
                    pair = new Pair<>(entry.getValue(), 1);
                    map2.put(s, pair);

                }

                for (Map.Entry<String, Pair<BossInfoClient, Integer>> entry : map2.entrySet()) {

                    String text = entry.getKey();
                    BossInfoClient info = entry.getValue().a;
                    int i3 = entry.getValue().b;
                    text = text + " x" + i3;

                    GL11.glScalef(NoRender.INSTANCE.bossBarSize.getValue(), NoRender.INSTANCE.bossBarSize.getValue(), 1.0f);

                    GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                    mc.getTextureManager().bindTexture(GuiBossOverlay.GUI_BARS_TEXTURES);
                    mc.ingameGUI.getBossOverlay().render(width, i, info);
                    mc.fontRenderer.drawStringWithShadow(text, scaledWidth / NoRender.INSTANCE.bossBarSize.getValue() / 2.0f - (mc.fontRenderer.getStringWidth(text) / 2.0f), i - 9, 0xFFFFFF);

                    GL11.glScalef(1.0f / NoRender.INSTANCE.bossBarSize.getValue(), 1.0f / NoRender.INSTANCE.bossBarSize.getValue(), 1.0f);

                    i += 10 + mc.fontRenderer.FONT_HEIGHT;

                }
            }
        }

        if (ModuleManager.getModule(NoRender.class).isEnabled() && NoRender.INSTANCE.bossBar.getValue() != NoRender.BossBarMode.None)
            ci.cancel();
    }
}
