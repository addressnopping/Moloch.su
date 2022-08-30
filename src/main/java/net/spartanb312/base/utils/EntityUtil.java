package net.spartanb312.base.utils;

import net.spartanb312.base.utils.graphics.RenderHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityShulkerBullet;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class EntityUtil {

    public static Minecraft mc = Minecraft.getMinecraft();
    public static boolean isEntityPlayerLoaded = false;
    public static boolean isEntityMobLoaded = false;
    public static boolean isEntityAnimalLoaded = false;
    public static boolean isEntityCrystalLoaded = false;
    public static boolean isEntityProjectileLoaded = false;
    public static List<Entity> entitiesList1 = new ArrayList<>();
    public static boolean entitiesListFlag = false;

    public static List<Entity> entitiesList() {
        if (!entitiesListFlag && !(mc.world == null || mc.world.loadedEntityList == null || mc.world.loadedEntityList.size() == 0))
            entitiesList1 = new ArrayList<>(mc.world.loadedEntityList);

        return entitiesList1;
    }
    
    public static boolean isEntityMob(Entity entity) {
        return (entity instanceof EntityMob || entity instanceof EntityShulker || entity instanceof EntitySlime || entity instanceof EntityGhast);
    }

    public static boolean isEntityAnimal(Entity entity) {
        return (entity instanceof EntityAnimal || entity instanceof EntitySquid);
    }

    public static Vec3d getInterpolatedAmount(Entity entity, double x, double y, double z) {
        return new Vec3d((entity.posX - entity.lastTickPosX) * x, (entity.posY - entity.lastTickPosY) * y, (entity.posZ - entity.lastTickPosZ) * z);
    }

    public static Vec3d getInterpolatedEntityPos(Entity entity, double ticks) {
        return new Vec3d(entity.lastTickPosX + ((entity.posX - entity.lastTickPosX) * ticks), entity.lastTickPosY + ((entity.posY - entity.lastTickPosY) * ticks), entity.lastTickPosZ + ((entity.posZ - entity.lastTickPosZ) * ticks));
    }

    public static Vec3d getInterpolatedAmount(Entity entity, double ticks) {
        return EntityUtil.getInterpolatedAmount(entity, ticks, ticks, ticks);
    }

    public static boolean isPlayerInHole() {
        BlockPos blockPos = getLocalPlayerPosFloored();

        IBlockState blockState = mc.world.getBlockState(blockPos);

        if (blockState.getBlock() != Blocks.AIR)
            return false;

        if (mc.world.getBlockState(blockPos.up()).getBlock() != Blocks.AIR)
            return false;

        if (mc.world.getBlockState(blockPos.down()).getBlock() == Blocks.AIR)
            return false;

        final BlockPos[] touchingBlocks = new BlockPos[]
                {blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west()};

        int validHorizontalBlocks = 0;
        for (BlockPos touching : touchingBlocks) {
            final IBlockState touchingState = mc.world.getBlockState(touching);
            if ((touchingState.getBlock() != Blocks.AIR) && touchingState.isFullBlock())
                validHorizontalBlocks++;
        }

        return validHorizontalBlocks >= 4;
    }

    public static BlockPos getLocalPlayerPosFloored() {
        return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
    }

    public static boolean isFakeLocalPlayer(Entity entity) {
        return entity != null && entity.getEntityId() == -100 && mc.player != entity;
    }

    public static boolean isPassive(Entity e) {
        if (e instanceof EntityWolf && ((EntityWolf) e).isAngry()) {
            return false;
        }
        if (e instanceof EntityAgeable || e instanceof EntityAmbientCreature || e instanceof EntitySquid) {
            return true;
        }
        return e instanceof EntityIronGolem && ((EntityIronGolem) e).getRevengeTarget() == null;
    }

    public static boolean isLiving(Entity e) {
        return e instanceof EntityLivingBase;
    }

    public static float[] calculateLookAt(double px, double py, double pz, EntityPlayer me) {
        double dirX = me.posX - px;
        double dirY = me.posY - py;
        double dirZ = me.posZ - pz;
        double len = Math.sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);
        dirX /= len;
        dirY /= len;
        dirZ /= len;
        double pitch = Math.asin(dirY);
        double yaw = Math.atan2(dirZ, dirX);
        pitch = pitch * 180.0d / Math.PI;
        yaw = yaw * 180.0d / Math.PI;
        yaw += 90f;
        return new float[]{(float) yaw, (float) pitch};
    }

    public static void runEntityCheck() {
        isEntityPlayerLoaded = false;
        isEntityMobLoaded = false;
        isEntityAnimalLoaded = false;
        isEntityCrystalLoaded = false;
        isEntityProjectileLoaded = false;
        for (Entity entity : entitiesList()) {
            entitiesListFlag = true;
            if (!RenderHelper.isInViewFrustrum(entity)) continue;
            if (entity instanceof EntityPlayer && entity != mc.player) isEntityPlayerLoaded = true;
            if (entity instanceof EntityMob || entity instanceof EntitySlime || entity instanceof EntityGhast || entity instanceof EntityDragon) isEntityMobLoaded = true;
            if (EntityUtil.isEntityAnimal(entity)) isEntityAnimalLoaded = true;
            if (entity instanceof EntityEnderCrystal) isEntityCrystalLoaded = true;
            if (entity instanceof IProjectile || entity instanceof EntityShulkerBullet || entity instanceof EntityFireball || entity instanceof EntityEnderEye) isEntityProjectileLoaded = true;
        }
        entitiesListFlag = false;
    }

    public static double calculateDistanceWithPartialTicks(double originalPos, double finalPos, float renderPartialTicks) {
        return finalPos + (originalPos - finalPos) * (double)renderPartialTicks;
    }

    public static Vec3d interpolateEntity(Entity entity, float renderPartialTicks) {
        return new Vec3d(calculateDistanceWithPartialTicks(entity.posX, entity.lastTickPosX, renderPartialTicks), calculateDistanceWithPartialTicks(entity.posY, entity.lastTickPosY, renderPartialTicks), calculateDistanceWithPartialTicks(entity.posZ, entity.lastTickPosZ, renderPartialTicks));
    }

    public static Vec3d interpolateEntityRender(Entity entity, float renderPartialTicks) {
        return new Vec3d(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * renderPartialTicks - mc.getRenderManager().renderPosX, entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * renderPartialTicks - mc.getRenderManager().renderPosY, entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * renderPartialTicks - mc.getRenderManager().renderPosZ);
    }

    public static boolean isBurrowed(Entity entity) {
        BlockPos pos = new BlockPos(Math.floor(entity.posX), Math.floor(entity.posY + 0.2), Math.floor(entity.posZ));
        return mc.world.getBlockState(pos).getBlock() != Blocks.AIR && mc.world.getBlockState(pos).getBlock() != Blocks.WATER && mc.world.getBlockState(pos).getBlock() != Blocks.FLOWING_WATER && mc.world.getBlockState(pos).getBlock() != Blocks.LAVA && mc.world.getBlockState(pos).getBlock() != Blocks.FLOWING_LAVA;
    }

    public static boolean isPosPlaceable(BlockPos pos) {
        return mc.world.getBlockState(pos).getMaterial().isReplaceable() && mc.world.checkNoEntityCollision(new AxisAlignedBB(pos), mc.player);
    }

    private static void centerPlayer(double x, double y, double z) {
        mc.player.connection.sendPacket(new CPacketPlayer.Position(x, y, z, true));
        mc.player.setPosition(x, y, z);
    }

    private static double getDst(Vec3d vec) {
        return mc.player.getPositionVector().distanceTo(vec);
    }

    public static void setCenter() {
        BlockPos centerPos = mc.player.getPosition();
        double y = centerPos.getY();
        double x = centerPos.getX();
        double z = centerPos.getZ();

        final Vec3d plusPlus = new Vec3d(x + 0.5, y, z + 0.5);
        final Vec3d plusMinus = new Vec3d(x + 0.5, y, z - 0.5);
        final Vec3d minusMinus = new Vec3d(x - 0.5, y, z - 0.5);
        final Vec3d minusPlus = new Vec3d(x - 0.5, y, z + 0.5);

        if (getDst(plusPlus) < getDst(plusMinus) && getDst(plusPlus) < getDst(minusMinus) && getDst(plusPlus) < getDst(minusPlus)) {
            x = centerPos.getX() + 0.5;
            z = centerPos.getZ() + 0.5;
        }
        if (getDst(plusMinus) < getDst(plusPlus) && getDst(plusMinus) < getDst(minusMinus) && getDst(plusMinus) < getDst(minusPlus)) {
            x = centerPos.getX() + 0.5;
            z = centerPos.getZ() - 0.5;
        }
        if (getDst(minusMinus) < getDst(plusPlus) && getDst(minusMinus) < getDst(plusMinus) && getDst(minusMinus) < getDst(minusPlus)) {
            x = centerPos.getX() - 0.5;
            z = centerPos.getZ() - 0.5;
        }
        if (getDst(minusPlus) < getDst(plusPlus) && getDst(minusPlus) < getDst(plusMinus) && getDst(minusPlus) < getDst(minusMinus)) {
            x = centerPos.getX() - 0.5;
            z = centerPos.getZ() + 0.5;
        }
        centerPlayer(x, y, z);
    }

    public static Vec3d selfCenterPos() {
        BlockPos centerPos = mc.player.getPosition();
        double y = centerPos.getY();
        double x = centerPos.getX();
        double z = centerPos.getZ();

        final Vec3d plusPlus = new Vec3d(x + 0.5, y, z + 0.5);
        final Vec3d plusMinus = new Vec3d(x + 0.5, y, z - 0.5);
        final Vec3d minusMinus = new Vec3d(x - 0.5, y, z - 0.5);
        final Vec3d minusPlus = new Vec3d(x - 0.5, y, z + 0.5);

        if (getDst(plusPlus) < getDst(plusMinus) && getDst(plusPlus) < getDst(minusMinus) && getDst(plusPlus) < getDst(minusPlus)) {
            x = centerPos.getX() + 0.5;
            z = centerPos.getZ() + 0.5;
        }
        if (getDst(plusMinus) < getDst(plusPlus) && getDst(plusMinus) < getDst(minusMinus) && getDst(plusMinus) < getDst(minusPlus)) {
            x = centerPos.getX() + 0.5;
            z = centerPos.getZ() - 0.5;
        }
        if (getDst(minusMinus) < getDst(plusPlus) && getDst(minusMinus) < getDst(plusMinus) && getDst(minusMinus) < getDst(minusPlus)) {
            x = centerPos.getX() - 0.5;
            z = centerPos.getZ() - 0.5;
        }
        if (getDst(minusPlus) < getDst(plusPlus) && getDst(minusPlus) < getDst(plusMinus) && getDst(minusPlus) < getDst(minusMinus)) {
            x = centerPos.getX() - 0.5;
            z = centerPos.getZ() + 0.5;
        }
        return new Vec3d(x, y, z);
    }

    public static boolean isEntityVisible(Entity entity) {
        return mc.player.canEntityBeSeen(entity);
    }

    public static double getInterpDistance(float partialTicks, Entity entity, Entity entity2) {
        Vec3d interp = interpolateEntity(entity, partialTicks);
        Vec3d interp2 = interpolateEntity(entity2, partialTicks);

        double x = interp.x - interp2.x;
        double y = interp.y - interp2.y;
        double z = interp.z - interp2.z;

        return Math.sqrt((x * x) + (y * y) + (z * z));
    }

    public static ModelBase getModel(Entity entity) {
        if (entity instanceof EntityPlayer) return new ModelPlayer(0.0f, false);
        if (entity instanceof EntityBat) return new ModelBat();
        if (entity instanceof EntityBlaze) return new ModelBlaze();
        if (entity instanceof EntitySpider) return new ModelSpider();
        if (entity instanceof EntityChicken) return new ModelChicken();
        if (entity instanceof EntityCow) return new ModelCow();
        if (entity instanceof EntityCreeper) return new ModelCreeper();
        if (entity instanceof EntityDonkey || entity instanceof EntityHorse || entity instanceof EntityMule || entity instanceof EntitySkeletonHorse || entity instanceof EntityZombieHorse) return new ModelHorse();
        if (entity instanceof EntityGuardian) return new ModelGuardian();
        if (entity instanceof EntityEnderCrystal) return new ModelEnderCrystal(0.0f, false);
        if (entity instanceof EntityDragon) return new ModelDragon(0.0f);
        if (entity instanceof EntityEnderman) return new ModelEnderman(0.0f);
        if (entity instanceof EntityEndermite) return new ModelEnderMite();
        if (entity instanceof EntityEvoker || entity instanceof EntityIllusionIllager || entity instanceof EntityVindicator) return new ModelIllager(0.0f, 0.0f, 64, 64);
        if (entity instanceof EntityGhast) return new ModelGhast();
        if (entity instanceof EntityZombieVillager) return new ModelZombieVillager();
        if (entity instanceof EntityGiantZombie || entity instanceof EntityZombie) return new ModelZombie();
        if (entity instanceof EntityLlama) return new ModelLlama(0.0f);
        if (entity instanceof EntityMagmaCube) return new ModelMagmaCube();
        if (entity instanceof EntityOcelot) return new ModelOcelot();
        if (entity instanceof EntityParrot) return new ModelParrot();
        if (entity instanceof EntityPig) return new ModelPig();
        if (entity instanceof EntityPolarBear) return new ModelPolarBear();
        if (entity instanceof EntityRabbit) return new ModelRabbit();
        if (entity instanceof EntitySheep) return new ModelSheep2();
        if (entity instanceof EntityShulker) return new ModelShulker();
        if (entity instanceof EntitySilverfish) return new ModelSilverfish();
        if (entity instanceof EntitySkeleton || entity instanceof EntityStray || entity instanceof EntityWitherSkeleton) return new ModelSkeleton();
        if (entity instanceof EntitySlime) return new ModelSlime(16);
        if (entity instanceof EntitySnowman) return new ModelSnowMan();
        if (entity instanceof EntitySquid) return new ModelSquid();
        if (entity instanceof EntityVex) return new ModelVex();
        if (entity instanceof EntityVillager) return new ModelVillager(0.0f);
        if (entity instanceof EntityIronGolem) return new ModelIronGolem();
        if (entity instanceof EntityWitch) return new ModelWitch(0.0f);
        if (entity instanceof EntityWither) return new ModelWither(0.0f);
        if (entity instanceof EntityWolf) return new ModelWolf();
        return null;
    }
    public static AxisAlignedBB scaleBB(Vec3d vec, AxisAlignedBB bb, float scale) {
        Vec3d center = new Vec3d(bb.minX + ((bb.maxX - bb.minX) * 0.5f), bb.minY + ((bb.maxY - bb.minY) * 0.5f), bb.minZ + ((bb.maxZ - bb.minZ) * 0.5f));
        double newWidth = (bb.maxX - bb.minX) * 0.5f * scale;
        double newHeight = (bb.maxY - bb.minY) * 0.5f * scale;
        double newLength = (bb.maxZ - bb.minZ) * 0.5f * scale;
        return new AxisAlignedBB(center.x + newWidth, center.y + newHeight, center.z + newLength,
                center.x - newWidth, center.y - newHeight, center.z - newLength);
    }

    public static boolean canStep() {
        return mc.world != null && mc.player != null && !mc.player.isInWater() && !mc.player.isInLava() && !mc.player.isOnLadder() && !mc.gameSettings.keyBindJump.isKeyDown();
    }

    public static boolean isOnGround(double height) {
        return !mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, -height, 0.0)).isEmpty();
    }

    public static double[] motionPredict(float magnitude, float xOffset, float yOffset, float zOffset) {
        double[] d = MathUtilFuckYou.cartesianToPolar3d(xOffset, yOffset, zOffset);
        double[] d1 = MathUtilFuckYou.polarToCartesian3d(magnitude, d[1], d[2]);

        return new double[]{d1[0], d1[2]};
    }

    //pasted from trollheck <3
    public static double getMovementYaw() {
        float forward = mc.player.movementInput.moveForward > 0.0f ? 1.0f :
                        mc.player.movementInput.moveForward < 0.0f ? -1.0f : 0.0f;
        float strafe = mc.player.movementInput.moveStrafe > 0.0f ? 1.0f :
                        mc.player.movementInput.moveStrafe < 0.0f ? -1.0f : 0.0f;

        float s = 90.0f * strafe;
        s *= forward != 0.0f ? forward * 0.5f : 1.0f;
        float yaw = mc.player.rotationYaw - s;
        yaw -= forward == -1.0f ? 180.0f : 0.0f;

        return yaw * (Math.PI / 180.0f);
    }
}
