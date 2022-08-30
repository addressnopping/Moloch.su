package me.thediamondsword5.moloch.utils;

import me.thediamondsword5.moloch.client.ServerManager;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.MobEffects;
import net.minecraft.item.*;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.spartanb312.base.utils.EntityUtil;
import net.spartanb312.base.utils.RotationUtil;
import net.spartanb312.base.utils.graphics.SpartanTessellator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static net.minecraft.init.Enchantments.EFFICIENCY;
import static net.spartanb312.base.utils.RotationUtil.mc;

public class BlockUtil {

    public static long packetMineStartTime = 0L;
    public static boolean packetMiningFlag = false;
    public static boolean isPlacing = false;

    public static BlockPos extrudeBlock(BlockPos pos, EnumFacing direction) {

        switch (direction) {
            case WEST: return new BlockPos(pos.x - 1.0, pos.y, pos.z);

            case EAST: return new BlockPos(pos.x + 1.0, pos.y, pos.z);

            case NORTH: return new BlockPos(pos.x, pos.y, pos.z - 1.0);

            case SOUTH: return new BlockPos(pos.x, pos.y, pos.z + 1.0);

            case UP: return new BlockPos(pos.x, pos.y + 1.0, pos.z);

            case DOWN: return new BlockPos(pos.x, pos.y - 1.0, pos.z);
        }

        return pos;
    }

    public static boolean isBlockPlaceable(BlockPos pos) {
        Block block = mc.world.getBlockState(pos).getBlock();
        return block != Blocks.AIR && block != Blocks.WATER && block != Blocks.FLOWING_WATER && block != Blocks.LAVA && block != Blocks.FLOWING_LAVA;
    }

    public static boolean isFacePlaceble(BlockPos pos, EnumFacing facing, boolean checkEntity) {
        BlockPos pos1 = BlockUtil.extrudeBlock(pos, facing);
        return !mc.world.getBlockState(pos).getMaterial().isReplaceable() && mc.world.getBlockState(pos1).getMaterial().isReplaceable() && (!checkEntity || EntityUtil.isPosPlaceable(pos1));
    }
    public static Vec3d getBlockVecFaceCenter(BlockPos blockPos, EnumFacing face) {
        BlockPos pos = new BlockPos(Math.floor(blockPos.x), Math.floor(blockPos.y), Math.floor(blockPos.z));
        switch (face) {
            case UP: {
                return new Vec3d(
                        pos.x + 0.5,
                        pos.y + 1.0,
                        pos.z + 0.5
                );
            }

            case DOWN: {
                return new Vec3d(
                        pos.x + 0.5,
                        pos.y,
                        pos.z + 0.5
                );
            }

            case EAST: {
                return new Vec3d(
                        pos.x + 1.0,
                        pos.y + 0.5,
                        pos.z + 0.5
                );
            }

            case WEST: {
                return new Vec3d(
                        pos.x,
                        pos.y + 0.5,
                        pos.z + 0.5
                );
            }

            case NORTH: {
                return new Vec3d(
                        pos.x + 0.5,
                        pos.y + 0.5,
                        pos.z + 1.0
                );
            }

            case SOUTH: {
                return new Vec3d(
                        pos.x + 0.5,
                        pos.y + 0.5,
                        pos.z
                );
            }
        }

        return new Vec3d(0, 0, 0);
   }

   public static EnumFacing getVisibleBlockSide(Vec3d blockVec) {
       Vec3d eyeVec = mc.player.getPositionEyes(mc.getRenderPartialTicks()).subtract(blockVec);
       return EnumFacing.getFacingFromVector((float)eyeVec.x, (float)eyeVec.y, (float)eyeVec.z);
   }

    public static Vec3d getVec3dBlock(BlockPos blockPos, EnumFacing face) {
        return new Vec3d(blockPos).add(0.5, 0.5, 0.5).add(new Vec3d(face.getDirectionVec()).scale(0.5));
    }

    public static float blockBreakSpeed(IBlockState blockMaterial, ItemStack tool) {
        float mineSpeed = tool.getDestroySpeed(blockMaterial);
        int efficiencyFactor = EnchantmentHelper.getEnchantmentLevel(EFFICIENCY, tool);

        mineSpeed = (float) (mineSpeed > 1.0 && efficiencyFactor > 0 ? (efficiencyFactor * efficiencyFactor + mineSpeed + 1.0) : mineSpeed);

        if (mc.player.isPotionActive(MobEffects.HASTE)) {
            mineSpeed *= 1.0f + Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.HASTE)).getAmplifier() * 0.2f;
        }

        if (mc.player.isPotionActive(MobEffects.MINING_FATIGUE)) {
            switch (Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.MINING_FATIGUE)).getAmplifier()) {
                case 0 : {
                    mineSpeed *= 0.3f;
                    break;
                }

                case 1: {
                    mineSpeed *= 0.09f;
                    break;
                }

                case 2: {
                    mineSpeed *= 0.0027f;
                    break;
                }

                default: {
                    mineSpeed *= 0.00081f;
                }
            }
        }

        if (!mc.player.onGround || (mc.player.isInWater() && EnchantmentHelper.getEnchantmentLevel(Enchantments.AQUA_AFFINITY, mc.player.inventory.armorItemInSlot(0)) == 0)) {
            mineSpeed /= 5.0f;
        }

        return mineSpeed;
    }

    public static double blockBrokenTime(BlockPos pos, ItemStack tool) {
        IBlockState blockMaterial = mc.world.getBlockState(pos);
        float damageTicks = blockBreakSpeed(blockMaterial, tool) /
                blockMaterial.getBlockHardness(mc.world, pos) / 30.0f;
        return (Math.ceil(1.0f / damageTicks) * 50.0f) * (20.0f / ServerManager.getTPS());
    }

    public static void placeBlock(BlockPos pos, EnumFacing facing, boolean packetPlace, boolean offHand, boolean spoofRotate) {
        isPlacing = true;

        if (!mc.player.isSneaking()) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            mc.player.setSneaking(true);
        }

        Vec3d blockVec = getVec3dBlock(pos, facing);

        if (spoofRotate) {
            float[] r = RotationUtil.getRotationsBlock(pos, facing, true);
            RotationUtil.setYawAndPitchBlock(r[0], r[1]);
        }

        if (packetPlace) {
            float x = (float)(blockVec.x - pos.getX());
            float y = (float)(blockVec.y - pos.getY());
            float z = (float)(blockVec.z - pos.getZ());
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, facing, offHand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, x, y, z));
        }
        else {
            mc.playerController.processRightClickBlock(mc.player, mc.world, pos, facing, blockVec, offHand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
        }

        mc.player.swingArm(EnumHand.MAIN_HAND);

        if (spoofRotate) {
            RotationUtil.resetRotationBlock();
        }

        if (!mc.player.isSneaking()) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            mc.player.setSneaking(true);
            mc.playerController.updateController();
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            mc.player.setSneaking(false);
            mc.playerController.updateController();
        }


        isPlacing = false;
    }

    public static void mineBlock(BlockPos pos, EnumFacing face, boolean packetMine) {
        if (packetMine) {
            packetMineStartTime = System.currentTimeMillis();
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, face));
            mc.player.swingArm(EnumHand.MAIN_HAND);
        }
        else if (mc.playerController.onPlayerDamageBlock(pos, face)) {
            mc.player.swingArm(EnumHand.MAIN_HAND);
        }
    }

    //for some reason pos1 == pos2 doesnt work so i have to use this instead :shrug:
    //14/7/22 update - trollhack compatibility issues with comparing blockpos to objectMouseOver blockpos so now theres a bb check too :even_bigger_shrug:
    public static boolean isSameBlockPos(BlockPos pos1, BlockPos pos2) {
        AxisAlignedBB bb1 = SpartanTessellator.getBoundingFromPos(pos1);
        AxisAlignedBB bb2 = SpartanTessellator.getBoundingFromPos(pos2);
        return bb1.maxX == bb2.maxX && bb1.maxY == bb2.maxY && bb1.maxZ == bb2.maxZ;
    }

    public static List<EnumFacing> getVisibleSides(BlockPos pos) {
        List<EnumFacing> list = new ArrayList<>();
        boolean isFullBox = !mc.world.getBlockState(pos).isFullBlock() || !mc.world.isAirBlock(pos);
        Vec3d eyesVec = mc.player.getPositionEyes(mc.getRenderPartialTicks());
        Vec3d blockVec = new Vec3d(pos).add(0.5, 0.5, 0.5);
        double diffX = eyesVec.x - blockVec.x;
        double diffY = eyesVec.y - blockVec.y;
        double diffZ = eyesVec.z - blockVec.z;

        if (diffX < -0.5) {
            list.add(EnumFacing.WEST);
        } else if (diffX > 0.5) {
            list.add(EnumFacing.EAST);
        } else if (isFullBox) {
            list.add(EnumFacing.WEST);
            list.add(EnumFacing.EAST);
        }

        if (diffY < -0.5) {
            list.add(EnumFacing.DOWN);
        } else if (diffY > 0.5) {
            list.add(EnumFacing.UP);
        } else {
            list.add(EnumFacing.DOWN);
            list.add(EnumFacing.UP);
        }

        if (diffZ < -0.5) {
            list.add(EnumFacing.NORTH);
        } else if (diffZ > 0.5) {
            list.add(EnumFacing.SOUTH);
        } else if (isFullBox) {
            list.add(EnumFacing.NORTH);
            list.add(EnumFacing.SOUTH);
        }

        return list;
    }
}
