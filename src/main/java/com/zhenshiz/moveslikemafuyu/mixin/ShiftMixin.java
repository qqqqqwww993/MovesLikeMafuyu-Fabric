package com.zhenshiz.moveslikemafuyu.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class ShiftMixin extends LivingEntity {
    protected ShiftMixin(EntityType<? extends LivingEntity> p_20966_, Level p_20967_) {
        super(p_20966_, p_20967_);
    }

    @Inject(method = "maybeBackOffFromEdge", at = @At("HEAD"), cancellable = true)
    private void redirectShiftCheck(Vec3 p_36201_, MoverType p_36202_, CallbackInfoReturnable<Vec3> cir) {
        if (this.isFallFlying() && this.onGround()) {
            cir.setReturnValue(p_36201_);
        }
    }
}
