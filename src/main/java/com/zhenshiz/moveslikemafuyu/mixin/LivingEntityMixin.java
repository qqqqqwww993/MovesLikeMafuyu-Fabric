package com.zhenshiz.moveslikemafuyu.mixin;

import com.zhenshiz.moveslikemafuyu.Config;
import com.zhenshiz.moveslikemafuyu.event.ClimbEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Unique
    private final double CATCH_DISTANCE = 0.2;
    @Unique
    private final double FALLING_CATCH_DISTANCE = 0.6;

    @Inject(method = "onClimbable", at = @At("HEAD"), cancellable = true)
    private void onIsOnLadder(CallbackInfoReturnable<Boolean> cir) {
        if (!Config.ENABLE_CRAW.get()) return;
        if ((Object) this instanceof Player player) {
            if (!player.isLocalPlayer()) return;
            if (MovesLikeMafuyu$checkWallClimbCondition(player) && !player.isSpectator()) {
                cir.setReturnValue(true);
            }
        }
    }

    @Unique
    private boolean MovesLikeMafuyu$checkWallClimbCondition(Player player) {
        Direction facing = player.getDirection();
        BlockPos checkPos = player.blockPosition().relative(facing);
        BlockPos upperPos = checkPos.above();
        BlockPos belowPos = player.blockPosition().below();
        if (!player.onGround() && MovesLikeMafuyu$isClimbableWall(player.level(), checkPos) && !player.level().getBlockState(belowPos).isSolidRender(player.level(), belowPos) && !MovesLikeMafuyu$isClimbableWall(player.level(), upperPos) && !MovesLikeMafuyu$isClimbableWall(player.level(), player.blockPosition())) {
            AABB playerBB = player.getBoundingBox();
            double distance = ClimbEvent.Falling ? FALLING_CATCH_DISTANCE : CATCH_DISTANCE;
            AABB wallBB = new AABB(checkPos).inflate(distance);
            return playerBB.intersects(wallBB);
        }
        return false;
    }

    @Unique
    private boolean MovesLikeMafuyu$isClimbableWall(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        VoxelShape collisionShape = state.getCollisionShape(level, pos);
        return !collisionShape.isEmpty();
    }

    @Inject(method = "isFallFlying", at = @At("HEAD"), cancellable = true)
    private void onCheckFallFlying(CallbackInfoReturnable<Boolean> cir) {
        if (((Entity) (Object) this) instanceof Player player) {
            if (player.getTags().contains("slide") && !player.isSpectator()) {
                cir.setReturnValue(true);
            }
        }
    }
}
