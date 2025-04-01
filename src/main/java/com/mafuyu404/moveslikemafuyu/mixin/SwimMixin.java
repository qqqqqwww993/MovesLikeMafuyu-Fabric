package com.mafuyu404.moveslikemafuyu.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.extensions.IForgeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Entity.class)
public abstract class SwimMixin {
    @Shadow public abstract void setSwimming(boolean p_20283_);

    @Inject(method = "isUnderWater", at = @At("HEAD"), cancellable = true)
    private void qqq(CallbackInfoReturnable<Boolean> cir) {
//        cir.setReturnValue(false);
//        if (((Player) (Object) this).isInWater()) {
//            cir.setReturnValue(true);
//        }
    }
    @Inject(method = "isInWater", at = @At("HEAD"), cancellable = true)
    private void checkWater(CallbackInfoReturnable<Boolean> cir) {
//        this.flu
    }
    @Inject(method = "updateSwimming", at = @At("HEAD"), cancellable = true)
    private void stillSwim(CallbackInfo ci) {
//        this.setSwimming(true);
//        ci.cancel();
    }
}
