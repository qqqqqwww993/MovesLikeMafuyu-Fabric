package com.mafuyu404.moveslikemafuyu.mixin;

import com.mafuyu404.moveslikemafuyu.Config;
import com.mafuyu404.moveslikemafuyu.event.ClimbEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow public abstract void remove(Entity.RemovalReason p_276115_);

    @Unique
    private double CATCH_DISTANCE = 0.2;
    @Unique
    private double FALLING_CATCH_DISTANCE = 0.6;

    @Inject(method = "onClimbable", at = @At("HEAD"), cancellable = true)
    private void onIsOnLadder(CallbackInfoReturnable<Boolean> cir) {
        if (!Config.enable("Craw")) return;
        if ((Object) this instanceof Player player) {
            if (checkWallClimbCondition(player) && !player.isSpectator()) {
                cir.setReturnValue(true);
            }
        }
    }

    private boolean checkWallClimbCondition(Player player) {
        Direction facing = player.getDirection();
        BlockPos checkPos = player.blockPosition().relative(facing);
        BlockPos upperPos = checkPos.above();
        if (!player.onGround() && isClimbableWall(player.level(), checkPos) && !isClimbableWall(player.level(), upperPos) && !isClimbableWall(player.level(), player.blockPosition().below())) {
            AABB playerBB = player.getBoundingBox();
            double distance = ClimbEvent.Falling ? FALLING_CATCH_DISTANCE : CATCH_DISTANCE;
            AABB wallBB = new AABB(checkPos).inflate(distance);
            return playerBB.intersects(wallBB);
        }
        return false;
    }

    private boolean isClimbableWall(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.isSolidRender(level, pos);
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
