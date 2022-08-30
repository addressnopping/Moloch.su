package net.spartanb312.base.mixin.mixins.client;

import me.thediamondsword5.moloch.event.events.render.DrawScreenEvent;
import me.thediamondsword5.moloch.hud.huds.DebugThing;
import me.thediamondsword5.moloch.module.modules.other.NameSpoof;
import me.thediamondsword5.moloch.module.modules.visuals.HoleRender;
import net.spartanb312.base.BaseCenter;
import net.spartanb312.base.client.FontManager;
import net.spartanb312.base.client.ModuleManager;
import net.spartanb312.base.module.modules.visuals.NoRender;
import net.spartanb312.base.utils.ChatUtil;
import net.spartanb312.base.utils.ColorUtil;
import me.thediamondsword5.moloch.mixinotherstuff.IChatLine;
import me.thediamondsword5.moloch.module.modules.client.ChatSettings;
import net.minecraft.client.gui.*;
import net.minecraft.client.Minecraft;
import net.spartanb312.base.utils.RotationUtil;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.spartanb312.base.BaseCenter.fontManager;

@Mixin(GuiNewChat.class)
public abstract class MixinGuiNewChat extends Gui {
    @Shadow public abstract int getLineCount();
    @Shadow @Final public List<ChatLine> drawnChatLines;
    @Shadow @Final private Minecraft mc;
    private ChatLine currentLine = null;
    private int intFlag = 0;

    //fobus moment
    @Inject(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/ChatLine;getUpdatedCounter()I"), locals = LocalCapture.CAPTURE_FAILSOFT)
    public void grabChatLine(int updateCounter, CallbackInfo ci, int i, int j, float f, boolean flag, float f1, int k, int l, int il, ChatLine chatLine) {
        currentLine = chatLine;
    }

    @Redirect(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;FFI)I"))
    private int drawStringWithShadow(FontRenderer fontRenderer, String text, float x, float y, int color) {

        if (ChatSettings.INSTANCE.chatTimeStamps.getValue()) {

            ChatSettings.drawnChatLines = new ArrayList<>(drawnChatLines);

            //hashmap randomly crashes mc at random ass times so im just going to slap this here
            try {
                if (IChatLine.storedTime.size() > 100 || ChatSettings.drawnChatLines.size() > 100) {
                    for (Map.Entry<ChatLine, String> entry : new HashMap<>(IChatLine.storedTime).entrySet()) {
                        if (!ChatSettings.drawnChatLines.contains(entry.getKey())) IChatLine.storedTime.remove(entry.getKey());
                    }
                }
            }
            catch (Exception ignored){}

            text = (ChatSettings.INSTANCE.chatTimeStampsColor.getValue() == ChatSettings.StringColors.Lgbtq ? "\u061c" : "") + ChatUtil.SECTIONSIGN + ChatUtil.colorString(ChatSettings.INSTANCE.chatTimeStampsColor) + ChatUtil.bracketLeft(ChatSettings.INSTANCE.chatTimeStampBrackets) + IChatLine.storedTime.get(currentLine) + ChatUtil.bracketRight(ChatSettings.INSTANCE.chatTimeStampBrackets) + (!text.contains("\u034f") ? "\u00a7r" : "") + (ChatSettings.INSTANCE.chatTimeStampSpace.getValue() ? " " : "") + text;
        }

        if (ModuleManager.getModule(NameSpoof.class).isEnabled()) {
            text = text.replaceAll(mc.player.getName(), NameSpoof.INSTANCE.name.getValue());
        }

        if (text.contains("\u034f")) {
            fontManager.drawLgbtqString(text, x, y, ChatSettings.INSTANCE.lgbtqDynamic.getValue() ? ColorUtil.rainbow(ChatSettings.INSTANCE.lgbtqRealSpeed.getValue(), 1.0f, 1.0f, ChatSettings.INSTANCE.lgbtqSaturation.getValue(), ChatSettings.INSTANCE.lgbtqBright.getValue()) : Color.HSBtoRGB(ChatSettings.INSTANCE.lgbtqStart.getValue(), ChatSettings.INSTANCE.lgbtqSaturation.getValue(), ChatSettings.INSTANCE.lgbtqBright.getValue()), ChatSettings.INSTANCE.lgbtqSpeed.getValue(), true);
        } else {
            mc.fontRenderer.drawStringWithShadow(text, x, y, color);
        }
        return 0;
    }

    @Inject(method = "drawChat", at = @At("HEAD"))
    public void drawChatPre(int updateCounter, CallbackInfo ci) {
        intFlag = 0;
    }

    @Redirect(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;drawRect(IIIII)V"))
    private void drawRectBackgroundClean(int left, int top, int right, int bottom, int color) {
        intFlag += 1;
        if (!(NoRender.INSTANCE.chat.getValue() && NoRender.INSTANCE.isEnabled())) {
            Gui.drawRect(left, top, (ChatSettings.INSTANCE.chatTimeStamps.getValue() && intFlag < (getLineCount() + 1)) ? (right + FontManager.getWidth(ChatSettings.INSTANCE.chatTimeStamps24hr.getValue() ? "<88:88>      " : "<88:88 PM>      ")) : right, bottom, color);
        }
    }

    @Redirect(method = "getChatComponent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;getStringWidth(Ljava/lang/String;)I"))
    public int getChatComponentRedirect(FontRenderer renderer, String text) {
        if (ModuleManager.getModule(NameSpoof.class).isEnabled()) {
            return renderer.getStringWidth(text.replaceAll(mc.player.getName(), NameSpoof.INSTANCE.name.getValue()));
        }

        return renderer.getStringWidth(text);
    }
}
