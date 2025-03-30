package com.mafuyu404.moveslikemafuyu.mixin;

import com.mafuyu404.moveslikemafuyu.event.FallEvent;
import com.mafuyu404.moveslikemafuyu.event.SlideEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class WallClimbMixin {
    @Unique
    private double CATCH_DISTANCE = 0.2;
    private double FALLING_CATCH_DISTANCE = 0.5;

    @Inject(method = "onClimbable", at = @At("HEAD"), cancellable = true)
    private void onIsOnLadder(CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof Player player) {
            if (checkWallClimbCondition(player)) {
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
            double distance = FallEvent.Falling ? FALLING_CATCH_DISTANCE : CATCH_DISTANCE;
            AABB wallBB = new AABB(checkPos).inflate(distance);
            return playerBB.intersects(wallBB);
        }
        return false;
    }

    private boolean isClimbableWall(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return state.isSolidRender(level, pos) &&
                !state.is(Blocks.LADDER) && // 排除原版梯子
                !state.is(Blocks.VINE);    // 排除藤蔓
    }

    @Inject(method = "isFallFlying", at = @At("HEAD"), cancellable = true)
    private void onCheckFallFlying(CallbackInfoReturnable<Boolean> cir) {
        if (((Entity) (Object) this) instanceof Player player) {
//            System.out.print(player.getTags().contains("slide"));
//            System.out.print("\n");
            if (player.getTags().contains("slide")) {
                cir.setReturnValue(true);
            }
        }
    }
}
