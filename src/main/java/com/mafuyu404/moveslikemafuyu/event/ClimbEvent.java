package com.mafuyu404.moveslikemafuyu.event;

import com.mafuyu404.moveslikemafuyu.ModConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

@Environment(EnvType.CLIENT)
public class ClimbEvent {
    private static int COOLDOWN;
    private static long cooldown = COOLDOWN;
    public static boolean Falling = true;
    private static double CATCH_DISTANCE = 0.2;
    private static double FALLING_CATCH_DISTANCE = 0.6;

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                tick(client.player);
            }
        });
    }

    public static void tick(Player player) {
        if (player.isSpectator()) return;
        Options options = Minecraft.getInstance().options;
        if (cooldown > 0 && cooldown <= COOLDOWN) {
            cooldown--;
        }
        if (! ModConfig.enable("FallingRescue")) {
            Falling = false;
            return;
        }
        double verticalSpeed = player.getDeltaMovement().y;
        Falling = verticalSpeed < 0 && verticalSpeed > -1 && !player.onGround() && !player.isInWater() && !player.isPassenger();
        if (Falling && player.onClimbable() && options.keyShift.isDown()) {
            if (player.level().getBlockState(player.blockPosition()).is(Blocks.SCAFFOLDING)) return;
            player.setDeltaMovement(0, 0, 0);
        }
    }

    public static void handleJumpOnClimbable(int key, int action) {
        if (Minecraft.getInstance().screen != null) return;
        if (! ModConfig.enable("ClimbJump")) return;
        Player player = Minecraft.getInstance().player;
        Options options = Minecraft.getInstance().options;
        if (player == null || player.isSpectator()) return;
        if (cooldown <= 0 && key == options.keyJump.key.getValue()) {
            if (player.isShiftKeyDown() && player.onClimbable()) {
                player.jumpFromGround();
                cooldown = COOLDOWN;
            }
        }
    }

    public static boolean checkWallClimbCondition(Player player) {
        Direction facing = player.getDirection();
        BlockPos checkPos = player.blockPosition().relative(facing);
        BlockPos upperPos = checkPos.above();
        BlockPos belowPos = player.blockPosition().below();
        if (!player.onGround() && isClimbableWall(player.level(), checkPos) && !player.level().getBlockState(belowPos).isSolidRender(player.level(), belowPos) && !isClimbableWall(player.level(), upperPos) && !isClimbableWall(player.level(), player.blockPosition())) {
            AABB playerBB = player.getBoundingBox();
            double distance = ClimbEvent.Falling ? FALLING_CATCH_DISTANCE : CATCH_DISTANCE;
            AABB wallBB = new AABB(checkPos).inflate(distance);
            return playerBB.intersects(wallBB);
        }
        return false;
    }

    private static boolean isClimbableWall(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        VoxelShape collisionShape = state.getCollisionShape(level, pos);
        return !collisionShape.isEmpty();
    }

    public static void onConfigLoad() {
        COOLDOWN = ModConfig.ConfigCache.getInt("ClimbJumpCooldown");
    }
}