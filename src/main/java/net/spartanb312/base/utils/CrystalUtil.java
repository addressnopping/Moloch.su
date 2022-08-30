package net.spartanb312.base.utils;

import me.thediamondsword5.moloch.hud.huds.DebugThing;
import me.thediamondsword5.moloch.utils.BlockUtil;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemFood;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.*;
import net.minecraft.world.Explosion;
import net.spartanb312.base.mixin.mixins.accessor.AccessorCPacketUseEntity;
import net.spartanb312.base.mixin.mixins.accessor.AccessorRenderManager;
import net.spartanb312.base.utils.graphics.SpartanTessellator;
import net.spartanb312.base.utils.math.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CrystalUtil {

    public static Minecraft mc = Minecraft.getMinecraft();
    public static double getRange(Vec3d a, double x, double y, double z) {
        double xl = a.x - x;
        double yl = a.y - y;
        double zl = a.z - z;
        return Math.sqrt(xl * xl + yl * yl + zl * zl);
    }

    public static boolean isReplaceable(Block block, boolean includeWater) {
        return block == Blocks.FIRE
                || block == Blocks.DOUBLE_PLANT
                || block == Blocks.VINE
                || block == Blocks.AIR
                || (!includeWater || (block == Blocks.WATER || block == Blocks.FLOWING_WATER));
    }

    public static void breakCrystal(EntityEnderCrystal entity) {
        CPacketUseEntity packet = new CPacketUseEntity(entity);
        ((AccessorCPacketUseEntity) packet).setId(((AccessorCPacketUseEntity) packet).getId());
        ((AccessorCPacketUseEntity) packet).setAction(CPacketUseEntity.Action.ATTACK);
        mc.player.connection.sendPacket(packet);
        mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
    }

    public static float calculateDamage(double posX, double posY, double posZ, Entity entity, Vec3d vec) {
        float doubleExplosionSize = 12.0F;
        double distanceSize = getRange(vec, posX, posY, posZ) / (double) doubleExplosionSize;
        Vec3d vec3d = new Vec3d(posX, posY, posZ);

        double blockDensity = entity.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());

        double v = (1.0D - distanceSize) * blockDensity;
        float damage = (float) ((int) ((v * v + v) / 2.0D * 7.0D * (double) doubleExplosionSize + 1.0D));
        double finalValue = 1.0;

        if (entity instanceof EntityLivingBase) {
            //noinspection ConstantConditions
            finalValue = getBlastReduction((EntityLivingBase) entity, getDamageMultiplied(damage), new Explosion(mc.world, null, posX, posY, posZ, 6F, false, true));
        }
        return (float) finalValue;
    }

    public static float calculateDamage(double posX, double posY, double posZ, Entity entity) {
        Vec3d offset = new Vec3d(entity.posX, entity.posY, entity.posZ);
        return calculateDamage(posX, posY, posZ, entity, offset);
    }

    private static float getBlastReduction(EntityLivingBase entity, float damage, Explosion explosion) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer ep = (EntityPlayer) entity;
            DamageSource ds = DamageSource.causeExplosionDamage(explosion);
            damage = CombatRules.getDamageAfterAbsorb(damage, (float) ep.getTotalArmorValue(), (float) ep.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());

            int k = EnchantmentHelper.getEnchantmentModifierDamage(ep.getArmorInventoryList(), ds);
            float f = MathHelper.clamp(k, 0.0F, 20.0F);
            damage = damage * (1.0F - f / 25.0F);

            if (entity.isPotionActive(MobEffects.RESISTANCE)) {
                damage = damage - (damage / 5);
            }

            damage = Math.max(damage, 0.0f);
            return damage;
        }
        damage = CombatRules.getDamageAfterAbsorb(damage, (float) entity.getTotalArmorValue(), (float) entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
        return damage;
    }

    private static float getDamageMultiplied(float damage) {
        int diff = mc.world.getDifficulty().getId();
        return damage * (diff == 0 ? 0 : (diff == 2 ? 1 : (diff == 1 ? 0.5f : 1.5f)));
    }

    public static EnumFacing enumFacing(final BlockPos blockPos) {
        for (EnumFacing enumFacing : EnumFacing.VALUES) {
            final Vec3d vec3d = new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);
            final Vec3d vec3d2 = new Vec3d(blockPos.getX() + enumFacing.getDirectionVec().getX(), blockPos.getY() + enumFacing.getDirectionVec().getY(), blockPos.getZ() + enumFacing.getDirectionVec().getZ());
            final RayTraceResult rayTraceBlocks;
            if ((rayTraceBlocks = mc.world.rayTraceBlocks(vec3d, vec3d2, false, true, false)) != null
                    && rayTraceBlocks.typeOfHit.equals(RayTraceResult.Type.BLOCK) && rayTraceBlocks.getBlockPos().equals(blockPos)) {
                return enumFacing;
            }
        }
        if (blockPos.getY() > mc.player.posY + mc.player.getEyeHeight()) {
            return EnumFacing.DOWN;
        }
        return EnumFacing.UP;
    }

    public static boolean isEating() {
        return mc.player != null && (mc.player.getHeldItemMainhand().getItem() instanceof ItemFood || mc.player.getHeldItemOffhand().getItem() instanceof ItemFood) && mc.player.isHandActive();
    }

    @SuppressWarnings("ALL")
    public static boolean canSeeBlock(BlockPos p_Pos) {
        return mc.player == null || mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + (double) mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(p_Pos.getX(), p_Pos.getY(), p_Pos.getZ()), false, true, false) != null;
    }

    public static BlockPos getPlayerPos() {
        return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
    }

    public static double getVecDistance(BlockPos a, double posX, double posY, double posZ) {
        double x1 = a.getX() - posX;
        double y1 = a.getY() - posY;
        double z1 = a.getZ() - posZ;
        return Math.sqrt(x1 * x1 + y1 * y1 + z1 * z1);
    }

    public static double getVecDistance(BlockPos pos, Entity entity) {
        return getVecDistance(pos, entity.posX, entity.posY, entity.posZ);
    }

    public static void glBillboard(float x, float y, float z) {
        float scale = 0.016666668f * 1.6f;
        GlStateManager.translate(x - ((AccessorRenderManager) mc.getRenderManager()).getRenderPosX(), y - ((AccessorRenderManager) mc.getRenderManager()).getRenderPosY(),
                z - ((AccessorRenderManager) mc.getRenderManager()).getRenderPosZ());
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(mc.getRenderManager().playerViewX, mc.gameSettings.thirdPersonView == 2 ? -1.0f : 1.0f, 0.0f, 0.0f);
        GlStateManager.scale(-scale, -scale, -scale);
    }

    public static boolean isCityable(BlockPos blockPos, EnumFacing relativeFacing, boolean diagonalCheck, boolean oneBlockCrystalMode) {
        if (mc.world.getBlockState(blockPos).getBlock() == Blocks.AIR || mc.world.getBlockState(blockPos).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(blockPos).getBlock() == Blocks.BARRIER) {
            return false;
        }

        EnumFacing toCheckDirect1;
        EnumFacing toCheckDirect2;

        switch (relativeFacing) {
            case NORTH:

            case SOUTH: {
                toCheckDirect1 = EnumFacing.EAST;
                toCheckDirect2 = EnumFacing.WEST;
                break;
            }

            default: {
                toCheckDirect1 = EnumFacing.NORTH;
                toCheckDirect2 = EnumFacing.SOUTH;
                break;
            }
        }

        BlockPos outOne = BlockUtil.extrudeBlock(blockPos, relativeFacing);
        BlockPos outOneDownOne = BlockUtil.extrudeBlock(outOne, EnumFacing.DOWN);
        BlockPos outOneUpOne = BlockUtil.extrudeBlock(outOne, EnumFacing.UP);

        BlockPos diagonalPos1Partial = BlockUtil.extrudeBlock(blockPos, toCheckDirect1);
        BlockPos diagonalPos1 = BlockUtil.extrudeBlock(diagonalPos1Partial, relativeFacing);
        BlockPos diagonalPos1DownOne = BlockUtil.extrudeBlock(diagonalPos1, EnumFacing.DOWN);
        BlockPos diagonalPos1UpOne = BlockUtil.extrudeBlock(diagonalPos1, EnumFacing.UP);
        BlockPos diagonalPos2Partial = BlockUtil.extrudeBlock(blockPos, toCheckDirect2);
        BlockPos diagonalPos2 = BlockUtil.extrudeBlock(diagonalPos2Partial, relativeFacing);
        BlockPos diagonalPos2DownOne = BlockUtil.extrudeBlock(diagonalPos2, EnumFacing.DOWN);
        BlockPos diagonalPos2UpOne = BlockUtil.extrudeBlock(diagonalPos2, EnumFacing.UP);

        if (oneBlockCrystalMode) {
            return (diagonalCheck ? ((mc.world.getBlockState(diagonalPos1Partial).getBlock() == Blocks.AIR && mc.world.getBlockState(diagonalPos1).getBlock() == Blocks.AIR && (mc.world.getBlockState(diagonalPos1DownOne).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(diagonalPos1DownOne).getBlock() == Blocks.BEDROCK)) ||
                    (mc.world.getBlockState(diagonalPos2Partial).getBlock() == Blocks.AIR && mc.world.getBlockState(diagonalPos2).getBlock() == Blocks.AIR && (mc.world.getBlockState(diagonalPos2DownOne).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(diagonalPos2DownOne).getBlock() == Blocks.BEDROCK))) : false) ||
                    (mc.world.getBlockState(outOne).getBlock() == Blocks.AIR && (mc.world.getBlockState(outOneDownOne).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(outOneDownOne).getBlock() == Blocks.BEDROCK));

        }
        else {
            return (diagonalCheck ? ((mc.world.getBlockState(diagonalPos1Partial).getBlock() == Blocks.AIR && mc.world.getBlockState(diagonalPos1).getBlock() == Blocks.AIR && mc.world.getBlockState(diagonalPos1UpOne).getBlock() == Blocks.AIR && (mc.world.getBlockState(diagonalPos1DownOne).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(diagonalPos1DownOne).getBlock() == Blocks.BEDROCK)) ||
                    (mc.world.getBlockState(diagonalPos2Partial).getBlock() == Blocks.AIR && mc.world.getBlockState(diagonalPos2).getBlock() == Blocks.AIR && mc.world.getBlockState(diagonalPos2UpOne).getBlock() == Blocks.AIR && (mc.world.getBlockState(diagonalPos2DownOne).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(diagonalPos2DownOne).getBlock() == Blocks.BEDROCK))) : false) ||
                    (mc.world.getBlockState(outOne).getBlock() == Blocks.AIR && mc.world.getBlockState(outOneUpOne).getBlock() == Blocks.AIR && (mc.world.getBlockState(outOneDownOne).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(outOneDownOne).getBlock() == Blocks.BEDROCK));
        }
    }

    public static void breakBlockingCrystals(AxisAlignedBB bb, boolean antiSuicideMode, float minHealthRemaining, float maxDamage, boolean rotate) {
        if (antiSuicideMode) {
            if (mc.player.getHealth() + mc.player.getAbsorptionAmount() - getDmgSelf() < minHealthRemaining) return;
        }
        else {
            if (getDmgSelf() >= maxDamage) return;
        }

        EntityUtil.entitiesListFlag = true;
        for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, bb)) {
            if (!(entity instanceof EntityEnderCrystal)) continue;
            if (!entity.preventEntitySpawning) continue;
            if (!entity.isEntityAlive()) continue;

            boolean sprinting = mc.player.isSprinting();
            if (sprinting) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SPRINTING));
            }

            if (rotate) RotationUtil.lookAtTarget(entity, false, 100.0f);
            breakCrystal((EntityEnderCrystal) entity);

            if (sprinting) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SPRINTING));
            }
            break;
        }
        EntityUtil.entitiesListFlag = false;
    }

    public static float getDmgSelf() {
        float dmg = 0.0f;
        EntityUtil.entitiesListFlag = true;
        for (Entity entity : EntityUtil.entitiesList()) {
            if (!(entity instanceof EntityEnderCrystal) || mc.player.getDistance(entity) > 12.0f) {
                continue;
            }

            float crystalDmg = CrystalUtil.calculateDamage(entity.posX, entity.posY, entity.posZ, mc.player);
            if (crystalDmg > dmg) {
                dmg = crystalDmg;
            }
        }
        EntityUtil.entitiesListFlag = false;

        return dmg;
    }

    public static boolean crystalPlaceable(BlockPos pos, boolean allowWater, boolean oneBlockCrystalMode) {
            if (mc.world.getBlockState(pos).getBlock() != Blocks.BEDROCK && mc.world.getBlockState(pos).getBlock() != Blocks.OBSIDIAN) {
                return false;
            }

            if (!CrystalUtil.isReplaceable(mc.world.getBlockState(BlockUtil.extrudeBlock(pos, EnumFacing.UP)).getBlock(), allowWater) ||
                    !CrystalUtil.isReplaceable(mc.world.getBlockState(BlockUtil.extrudeBlock(BlockUtil.extrudeBlock(pos, EnumFacing.UP), EnumFacing.UP)).getBlock(), allowWater)) {
                return false;
            }

            EntityUtil.entitiesListFlag = true;
            for (Entity entity : EntityUtil.entitiesList()) {
                if (entity.getEntityBoundingBox().intersects(new AxisAlignedBB(pos.x, pos.y + 1.0, pos.z,
                        pos.x + 1.0, pos.y + 3.0, pos.z + 1.0))) {
                    return false;
                }
            }
            EntityUtil.entitiesListFlag = false;

            return true;
    }

    public static Pair<BlockPos, Entity> calcPlace(boolean targetMobs, float detectRange, float range, float wallRange, float minDamage, float maxSelfDamage, boolean lethalOverride, float lethalRemainingHealth, boolean noSuicide, boolean allowWater, boolean oneBlockCrystalPlace) {
        BlockPos toPlacePos = null;
        Entity target = null;
        float highestDamage = 0.0f;
        List<BlockPos> placeablePos = BlockInteractionHelper.getSphere(getPlayerPos(), range, (int)range, false, true, 0)
                                        .stream()
                                        .filter(pos -> pos.getDistance((int) mc.player.posX, (int) mc.player.posY, (int) mc.player.posZ) <= range)
                                        .filter(pos -> crystalPlaceable(pos, oneBlockCrystalPlace, allowWater))
                                        .collect(Collectors.toList());
        List<Entity> entities = EntityUtil.entitiesList()
                                    .stream()
                                    .filter(entity -> entity instanceof EntityLivingBase)
                                    .filter(entity -> !targetMobs || EntityUtil.isEntityMob(entity))
                                    .filter(entity -> entity != mc.renderViewEntity)
                                    .filter(entity -> ((EntityLivingBase) entity).getHealth() > 0.0f)
                                    .filter(entity -> entity.getDistance(mc.player.posX, mc.player.posY, mc.player.posZ) <= detectRange)
                                    .collect(Collectors.toList());

        for (Entity entity : entities) {
            for (BlockPos pos : placeablePos) {

                if (!canSeeBlock(pos) && MathUtilFuckYou.getDistance(new Vec3d(pos), mc.player.getPositionVector()) > wallRange) continue;
                float health = mc.player.getHealth() + mc.player.getAbsorptionAmount();
                float selfDamage = calculateDamage(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, mc.player);
                float targetDamage = calculateDamage(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, entity, entity.getPositionVector());
                if (targetDamage < highestDamage) continue;
                if (targetDamage < minDamage) continue;
                if (noSuicide && selfDamage > health) continue;
                if (lethalOverride) {
                    if (health - selfDamage < lethalRemainingHealth || (targetDamage < ((EntityLivingBase) entity).getHealth() + ((EntityLivingBase) entity).getAbsorptionAmount())) {
                        continue;
                    }
                }
                else if (selfDamage > maxSelfDamage) {
                    continue;
                }

                toPlacePos = pos;
                target = entity;
                highestDamage = targetDamage;
            }
        }


        return new Pair<>(toPlacePos, target);
    }
}
