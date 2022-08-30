package net.spartanb312.base.module.modules.other;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.spartanb312.base.client.ModuleManager;
import net.spartanb312.base.common.annotations.ModuleInfo;
import net.spartanb312.base.common.annotations.Parallel;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.module.Category;
import net.spartanb312.base.module.Module;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.potion.PotionEffect;

import java.util.UUID;

@Parallel
@ModuleInfo(name = "FakePlayer", category = Category.OTHER, description = "Spawn a fake player entity in client side")
public class FakePlayer extends Module {

    Setting<Integer> health = setting("Health", 10, 0, 36).des("Health of fakeplayer");
    Setting<Boolean> sneak = setting("Sneak", false).des("Makes fakeplayer crouch ");
    Setting<String> playerName = setting("Name", "B_312").des("Name of fakeplayer");

    @Override
    public void onEnable() {
        MinecraftForge.EVENT_BUS.register(this);
        if (mc.player == null || mc.world == null) return;
        EntityOtherPlayerMP fakePlayer = new EntityOtherPlayerMP(mc.world, new GameProfile(UUID.fromString("60569353-f22b-42da-b84b-d706a65c5ddf"), playerName.getValue()));
        fakePlayer.copyLocationAndAnglesFrom(mc.player);
        for (PotionEffect potionEffect : mc.player.getActivePotionEffects()) {
            fakePlayer.addPotionEffect(potionEffect);
        }
        fakePlayer.setHealth(health.getValue());
        fakePlayer.inventory.copyInventory(mc.player.inventory);
        fakePlayer.rotationYawHead = mc.player.rotationYawHead;
        if (sneak.getValue()) fakePlayer.setSneaking(true);
        mc.world.addEntityToWorld(-666, fakePlayer);
        moduleEnableFlag = true;

    }

    @Override
    public void onDisable() {
        MinecraftForge.EVENT_BUS.unregister(this);
        mc.world.removeEntityFromWorld(-666);
        moduleDisableFlag = true;
    }

    @Override
    public void onTick() {
        if (mc.player.deathTime > 0) {
            MinecraftForge.EVENT_BUS.unregister(this);
            ModuleManager.getModule(FakePlayer.class).disable();
        }
    }

    @Override
    public String getModuleInfo() {
        return playerName.getValue();
    }

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        MinecraftForge.EVENT_BUS.unregister(this);
        ModuleManager.getModule(FakePlayer.class).disable();
    }
}
