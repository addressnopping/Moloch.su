package me.thediamondsword5.moloch.module.modules.other;

import com.mojang.authlib.GameProfile;
import me.thediamondsword5.moloch.event.events.entity.TurnEvent;
import me.thediamondsword5.moloch.event.events.player.PlayerAttackEvent;
import me.thediamondsword5.moloch.event.events.render.RenderViewEntityGuiEvent;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.MovementInput;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.spartanb312.base.client.ModuleManager;
import net.spartanb312.base.common.annotations.ModuleInfo;
import net.spartanb312.base.common.annotations.Parallel;
import net.spartanb312.base.core.event.Listener;
import net.spartanb312.base.core.setting.Setting;
import net.spartanb312.base.event.events.network.PacketEvent;
import net.spartanb312.base.module.Category;
import net.spartanb312.base.module.Module;
import net.spartanb312.base.utils.RotationUtil;

@Parallel
@ModuleInfo(name = "Freecam", category = Category.OTHER, description = "Move camera off of player body and through walls")
public class Freecam extends Module {

    public static Freecam INSTANCE;
    public EntityPlayer camera = null;
    private float prevYaw = 0.0f;
    private float prevPitch = 0.0f;
    private boolean rotationFlag = false;
    private final int cameraEntityID = 6666666;

    //disables on shutdown (see MixinMinecraft)
    //see MixinRenderPlayer for forcing player to be rendered while enabled
    Setting<Float> horizontalSpeed = setting("HorizontalSpeed", 15.0f, 1.0f, 50.0f).des("Speed going right or left or forward or backward");
    Setting<Float> verticalSpeed = setting("VerticalSpeed", 15.0f, 1.0f, 50.0f).des("Speed going up or down");
    Setting<Boolean> rotate = setting("Rotate", false).des("Automatically rotate your player to face where you are hitting in freecam");

    public Freecam() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        MinecraftForge.EVENT_BUS.register(this);
        moduleEnableFlag = true;
    }

    @Override
    public void onDisable() {
        MinecraftForge.EVENT_BUS.unregister(this);
        resetFreecam();
        moduleDisableFlag = true;
    }

    @Override
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketUseEntity && ((CPacketUseEntity) event.getPacket()).getEntityFromWorld(mc.world) == mc.player)
            event.cancel();
    }

    @Override
    public void onTick() {
        if (mc.player.deathTime > 0.0) {
            MinecraftForge.EVENT_BUS.unregister(this);
            resetFreecam();
            ModuleManager.getModule(Freecam.class).disable();
        }
        else if (camera == null && mc.player.ticksExisted > 5) {
            camera = new CameraEntity(mc.world, mc.session.getProfile());
            camera.copyLocationAndAnglesFrom(mc.player);
            mc.world.addEntityToWorld(cameraEntityID, camera);
            resetMovement(mc.player.movementInput);
            mc.renderViewEntity = camera;
        }
    }

    @Override
    public void onRenderTick() {
        if (rotate.getValue() && camera != null && mc.player.ticksExisted > 5 && mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit != RayTraceResult.Type.MISS && mc.objectMouseOver.hitVec != null) {

            if (!rotationFlag) {
                prevYaw = mc.player.rotationYawHead;
                prevPitch = mc.player.rotationPitch;
                rotationFlag = true;
            }

            float[] r = RotationUtil.getRotations(mc.player.getPositionEyes(mc.getRenderPartialTicks()), mc.objectMouseOver.hitVec);
            mc.player.rotationYaw = r[0];
            mc.player.rotationPitch = r[1];
        }
    }

    @Listener
    public void onRenderGuiForRenderViewEntity(RenderViewEntityGuiEvent event) {
        if (camera != null && mc.player.ticksExisted > 5)
            event.entityPlayer = mc.player;
    }

    @Listener
    public void onTurn(TurnEvent event) {
        if (event.entity == mc.player) {
            camera.turn(event.yaw, event.pitch);
            event.cancel();
        }
    }

    @Listener
    public void onPlayerAttackPre(PlayerAttackEvent event) {
        if (event.target == mc.player)
            event.cancel();
    }

    @SubscribeEvent
    public void onInput(InputUpdateEvent event) {
        resetMovement(event.getMovementInput());
    }

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        MinecraftForge.EVENT_BUS.unregister(this);
        resetFreecam();
        ModuleManager.getModule(Freecam.class).disable();
    }

    private void resetMovement(MovementInput movementInput) {
        if (movementInput instanceof MovementInputFromOptions) {
            movementInput.moveForward = 0.0f;
            movementInput.moveStrafe = 0.0f;
            movementInput.forwardKeyDown = false;
            movementInput.backKeyDown = false;
            movementInput.leftKeyDown = false;
            movementInput.rightKeyDown = false;
            movementInput.jump = false;
            movementInput.sneak = false;
        }
    }

    public void resetFreecam() {
        mc.renderViewEntity = mc.player;
        mc.world.removeEntityFromWorld(cameraEntityID);
        camera = null;

        if (rotate.getValue()) {
            mc.player.rotationYawHead = prevYaw;
            mc.player.rotationYaw = prevYaw;
            mc.player.rotationPitch = prevPitch;
            rotationFlag = false;
        }
    }

    private class CameraEntity extends EntityOtherPlayerMP {
        public CameraEntity(World worldIn, GameProfile gameProfileIn) {
            super(worldIn, gameProfileIn);
        }

        public CameraEntity(World worldIn) {
            this(worldIn, mc.player.getGameProfile());
        }

        //trollheck pasted
        @Override
        public void onLivingUpdate() {
            inventory.copyInventory(mc.player.inventory);
            updateEntityActionState();

            if (mc.gameSettings.keyBindForward.isKeyDown()) moveForward = 1.0f;
            else if (mc.gameSettings.keyBindBack.isKeyDown()) moveForward = -1.0f;
            else moveForward = 0.0f;

            if (mc.gameSettings.keyBindRight.isKeyDown()) moveStrafing = 1.0f;
            else if (mc.gameSettings.keyBindLeft.isKeyDown()) moveStrafing = -1.0f;
            else moveStrafing = 0.0f;

            if (mc.gameSettings.keyBindJump.isKeyDown()) moveVertical = 1.0f;
            else if (mc.gameSettings.keyBindSneak.isKeyDown()) moveVertical = -1.0f;
            else moveVertical = 0.0f;

            float yaw = RotationUtil.normalizeAngle((float)(Math.atan2(moveForward, moveStrafing) * (180.0f / Math.PI)) - 90.0f);
            double yawRadian = (rotationYaw - yaw) * (Math.PI / 180.0f);
            float speed = (horizontalSpeed.getValue() / 20.0f) * Math.min(Math.abs(moveForward) + Math.abs(moveStrafing), 1.0f);

            motionX = -Math.sin(yawRadian) * speed;
            motionY = moveVertical * (verticalSpeed.getValue() / 20.0f);
            motionZ = Math.cos(yawRadian) * speed;

            if (mc.gameSettings.keyBindSprint.isKeyDown()) {
                motionX *= 1.5;
                motionY *= 1.5;
                motionZ *= 1.5;
            }

            move(MoverType.SELF, motionX, motionY, motionZ);
        }

        @Override
        public float getEyeHeight() {
            return 1.65f;
        }

        @Override
        public boolean isSpectator() {
            return true;
        }

        @Override
        public boolean isInvisible() {
            return true;
        }

        @Override
        public boolean isInvisibleToPlayer(EntityPlayer player) {
            return true;
        }
    }
}
