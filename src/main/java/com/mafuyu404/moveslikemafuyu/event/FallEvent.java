package com.mafuyu404.moveslikemafuyu.event;

import com.mafuyu404.moveslikemafuyu.MovesLikeMafuyu;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MovesLikeMafuyu.MODID, value = Dist.CLIENT)
public class FallEvent {
    public static boolean Falling = true;
    @SubscribeEvent
    public static void onLivingFall(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        double verticalSpeed = player.getDeltaMovement().y;
        Falling = verticalSpeed < 0 && verticalSpeed > -1 && !player.onGround() && !player.isInWater() && !player.isPassenger();
    }
}
