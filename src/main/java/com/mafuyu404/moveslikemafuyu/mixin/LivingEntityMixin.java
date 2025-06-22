package com.mafuyu404.moveslikemafuyu.mixin;

import com.mafuyu404.moveslikemafuyu.ModConfig;
import com.mafuyu404.moveslikemafuyu.event.ClimbEvent;
import com.mafuyu404.moveslikemafuyu.event.SlideEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Inject(method = "onClimbable", at = @At("HEAD"), cancellable = true)
    private void onIsOnLadder(CallbackInfoReturnable<Boolean> cir) {
        if (! ModConfig.enable("Craw")) return;
        if ((Object) this instanceof Player player) {
            if (!player.isLocalPlayer()) return;
            if (ClimbEvent.checkWallClimbCondition(player) && !player.isSpectator()) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "isFallFlying", at = @At("HEAD"), cancellable = true)
    private void onCheckFallFlying(CallbackInfoReturnable<Boolean> cir) {
        if (((Entity) (Object) this) instanceof Player player) {
            if (player.getTags().contains("slide") && !player.isSpectator()) {
                cir.setReturnValue(true);
            }
        }
    }

    // 添加额外的姿势检查，确保滑铲时保持正确姿势
    @Inject(method = "updateFallFlying", at = @At("HEAD"))
    private void onUpdateFallFlying(CallbackInfo ci) {
        if (((Entity) (Object) this) instanceof Player player) {
            if (player.getTags().contains("slide") && !player.isSpectator()) {
                if (player.getPose() != Pose.SWIMMING) {
                    player.setPose(Pose.SWIMMING);
                }
            }
        }
    }

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void onHurt(DamageSource damageSource, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (damageSource.is(DamageTypes.FLY_INTO_WALL) && SlideEvent.shouldCancelFlyIntoDamage(entity)) {
            cir.setReturnValue(false);
        }
    }
}
